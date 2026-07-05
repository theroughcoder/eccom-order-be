package be.ecom_order_be.service;

import be.ecom_order_be.client.PaymentServiceClient;
import be.ecom_order_be.client.ProductServiceClient;
import be.ecom_order_be.dto.request.CreatePaymentIntentRequest;
import be.ecom_order_be.dto.request.MarkOrderPaidRequest;
import be.ecom_order_be.dto.request.OrderRequest;
import be.ecom_order_be.dto.request.ProductStockItem;
import be.ecom_order_be.dto.request.ValidateProducStockCountRequest;
import be.ecom_order_be.dto.response.OrderHistoryResponse;
import be.ecom_order_be.dto.response.OrderItemResponse;
import be.ecom_order_be.dto.response.OrderResponse;
import be.ecom_order_be.dto.response.PaymentIntentResponse;
import be.ecom_order_be.dto.response.ShippingAddressResponse;
import be.ecom_order_be.model.DeliveryStatus;
import be.ecom_order_be.model.Order;
import be.ecom_order_be.model.OrderItem;
import be.ecom_order_be.model.ShippingAddress;
import be.ecom_order_be.repository.OrderRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<OrderHistoryResponse> getOrderHistory(String authHeader) {
        String userId = extractUserIdFromToken(authHeader);
        log.info("Fetching order history for userId: {}", userId);
        return orderRepository.findByUserId(userId).stream().map(order -> {
            OrderHistoryResponse r = new OrderHistoryResponse();
            r.setId(order.getId());
            r.setCreatedAt(order.getCreatedAt());
            r.setTotalPrice(order.getTotalPrice());
            r.setPaid(order.isPaid());
            r.setPaidAt(order.getPaidAt());
            r.setDeliveryStatus(order.getDeliveryStatus());
            r.setDelivered(order.getDeliveryStatus() == DeliveryStatus.DELIVERED);
            r.setDeliveredAt(order.getDeliveredAt());
            return r;
        }).collect(Collectors.toList());
    }

    private String extractUserIdFromToken(String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            String payload = token.split("\\.")[1];
            byte[] decoded = Base64.getUrlDecoder().decode(payload);
            JsonNode claims = objectMapper.readTree(decoded);
            log.info("JWT claims: {}", claims);
            JsonNode idNode = claims.get("sub");
            return idNode != null ? idNode.asText() : null;
        } catch (Exception e) {
            log.debug("JWT claims: {} error");
            throw new RuntimeException("Invalid token");
        }
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request, String authHeader) {

        ValidateProducStockCountRequest validateRequest = new ValidateProducStockCountRequest();
        validateRequest.setOrderItems(
            request.getOrderItems().stream().map(item -> {
                ProductStockItem stockItem = new ProductStockItem();
                stockItem.setId(item.getId());
                stockItem.setQuantity(item.getQuantity());
                stockItem.setName(item.getName());
                return stockItem;
            }).collect(Collectors.toList())
        );
        String validate = productServiceClient.validateProductInStock(validateRequest);
        if (!"Stock validation successful".equals(validate)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, validate);
        }
        String userId = extractUserIdFromToken(authHeader);
        ShippingAddress shippingAddress = new ShippingAddress();
        shippingAddress.setFullName(request.getShippingAddress().getFullName());
        shippingAddress.setAddress(request.getShippingAddress().getAddress());
        shippingAddress.setCity(request.getShippingAddress().getCity());
        shippingAddress.setPostalCode(request.getShippingAddress().getPostalCode());

        Order order = new Order();
        order.setUserId(userId);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setItemsPrice(request.getItemsPrice());
        order.setShippingPrice(request.getShippingPrice());
        order.setTax(request.getTax());
        order.setTotalPrice(request.getTotalPrice());

        List<OrderItem> items = request.getOrderItems().stream().map(itemReq -> {
            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProductId(itemReq.getId());
            item.setSlug(itemReq.getSlug());
            item.setImage(itemReq.getImage());
            item.setName(itemReq.getName());
            item.setRating(itemReq.getRating());
            item.setNumReviews(itemReq.getNumReviews());
            item.setPrice(itemReq.getPrice());
            item.setCountInStock(itemReq.getCountInStock());
            item.setCategory(itemReq.getCategory());
            item.setDescription(itemReq.getDescription());
            item.setQuantity(itemReq.getQuantity());
            return item;
        }).collect(Collectors.toList());
        order.setOrderItems(items);

        // Saved once (without a payment intent yet) to obtain the generated order id, which is
        // needed to correlate the payment intent with this order. If payment-intent creation
        // below fails, the exception rolls back this insert since the method is @Transactional.
        Order savedOrder = orderRepository.save(order);

        CreatePaymentIntentRequest paymentIntentRequest = new CreatePaymentIntentRequest();
        paymentIntentRequest.setOrderId(savedOrder.getId().toString());
        paymentIntentRequest.setAmount(savedOrder.getTotalPrice());
        paymentIntentRequest.setCurrency("INR");
        PaymentIntentResponse paymentIntent;
        try {
            paymentIntent = paymentServiceClient.createPaymentIntent(paymentIntentRequest);
        } catch (Exception e) {
            log.error("Failed to create payment intent for order {}", savedOrder.getId(), e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Unable to start payment for this order, please try again");
        }

        savedOrder.setPaymentIntentId(paymentIntent.getId());
        savedOrder.setClientSecret(paymentIntent.getClientSecret());
        Order finalOrder = orderRepository.save(savedOrder);

        return mapToResponse(finalOrder);
    }

    public OrderResponse getOrderDetail(Long id, String authHeader) {
        String userId = extractUserIdFromToken(authHeader);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        if (!order.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have access to this order");
        }
        return mapToResponse(order);
    }

    public void markOrderPaid(Long id, MarkOrderPaidRequest request) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        if (order.isPaid()) {
            return;
        }
        order.setPaid(true);
        order.setPaidAt(LocalDateTime.now());
        orderRepository.save(order);
        log.info("Order {} marked paid via payment intent {}", id, request.getPaymentIntentId());
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());

        ShippingAddressResponse sa = new ShippingAddressResponse();
        sa.setFullName(order.getShippingAddress().getFullName());
        sa.setAddress(order.getShippingAddress().getAddress());
        sa.setCity(order.getShippingAddress().getCity());
        sa.setPostalCode(order.getShippingAddress().getPostalCode());
        response.setShippingAddress(sa);

        response.setPaymentMethod(order.getPaymentMethod());
        response.setItemsPrice(order.getItemsPrice());
        response.setShippingPrice(order.getShippingPrice());
        response.setTax(order.getTax());
        response.setTotalPrice(order.getTotalPrice());
        response.setPaid(order.isPaid());
        response.setPaidAt(order.getPaidAt());
        response.setDeliveryStatus(order.getDeliveryStatus());
        response.setDelivered(order.getDeliveryStatus() == DeliveryStatus.DELIVERED);
        response.setDeliveredAt(order.getDeliveredAt());
        response.setCreatedAt(order.getCreatedAt());
        response.setPaymentIntentId(order.getPaymentIntentId());
        response.setClientSecret(order.getClientSecret());

        response.setOrderItems(order.getOrderItems().stream().map(item -> {
            OrderItemResponse itemResponse = new OrderItemResponse();
            itemResponse.setId(item.getId());
            itemResponse.setProductId(item.getProductId());
            itemResponse.setSlug(item.getSlug());
            itemResponse.setImage(item.getImage());
            itemResponse.setName(item.getName());
            itemResponse.setPrice(item.getPrice());
            itemResponse.setQuantity(item.getQuantity());
            return itemResponse;
        }).collect(Collectors.toList()));

        return response;
    }
}
