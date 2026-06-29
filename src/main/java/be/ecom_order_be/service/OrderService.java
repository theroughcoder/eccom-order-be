package be.ecom_order_be.service;

import be.ecom_order_be.client.ProductServiceClient;
import be.ecom_order_be.dto.request.OrderRequest;
import be.ecom_order_be.dto.request.ValidateProducStockCountRequest;
import be.ecom_order_be.dto.request.OrderItemRequest;
import be.ecom_order_be.dto.response.OrderHistoryResponse;
import be.ecom_order_be.dto.response.OrderResponse;
import be.ecom_order_be.dto.response.ShippingAddressResponse;
import be.ecom_order_be.model.DeliveryStatus;
import be.ecom_order_be.model.Order;
import be.ecom_order_be.model.ShippingAddress;
import be.ecom_order_be.repository.OrderRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductServiceClient productServiceClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<OrderHistoryResponse> getOrderHistory(String authHeader) {
        String userId = extractUserIdFromToken(authHeader);
        log.info("Fetching order history for userId: {}", userId);
        return orderRepository.findByUserId(userId).stream().map(order -> {
            OrderHistoryResponse r = new OrderHistoryResponse();
            r.setId(order.getId());
            r.setCreatedAt(order.getCreatedAt());
            r.setTotalPrice(order.getTotalPrice());
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

    public List<OrderResponse> createOrder(OrderRequest request, String authHeader) {

        ValidateProducStockCountRequest validateRequest = new ValidateProducStockCountRequest();
        validateRequest.setOrderItems(
            request.getOrderItems().stream().map(item -> {
                be.ecom_order_be.model.OrderItem oi = new be.ecom_order_be.model.OrderItem();
                oi.setId(item.getId());
                oi.setQuantity(item.getQuantity());
                oi.setName(item.getName());
                return oi;
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

        List<Order> orders = request.getOrderItems().stream().map(itemReq -> {
            Order order = new Order();
            order.setProductId(itemReq.getId());
            order.setSlug(itemReq.getSlug());
            order.setImage(itemReq.getImage());
            order.setName(itemReq.getName());
            order.setRating(itemReq.getRating());
            order.setNumReviews(itemReq.getNumReviews());
            order.setPrice(itemReq.getPrice());
            order.setCountInStock(itemReq.getCountInStock());
            order.setCategory(itemReq.getCategory());
            order.setDescription(itemReq.getDescription());
            order.setQuantity(itemReq.getQuantity());
            order.setShippingAddress(shippingAddress);
            order.setUserId(userId);
            order.setPaymentMethod(request.getPaymentMethod());
            order.setItemsPrice(request.getItemsPrice());
            order.setShippingPrice(request.getShippingPrice());
            order.setTax(request.getTax());
            order.setTotalPrice(request.getTotalPrice());
            return order;
        }).collect(Collectors.toList());

        return orderRepository.saveAll(orders).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setProductId(order.getProductId());
        response.setSlug(order.getSlug());
        response.setImage(order.getImage());
        response.setName(order.getName());
        response.setRating(order.getRating());
        response.setNumReviews(order.getNumReviews());
        response.setPrice(order.getPrice());
        response.setCountInStock(order.getCountInStock());
        response.setCategory(order.getCategory());
        response.setDescription(order.getDescription());
        response.setQuantity(order.getQuantity());

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
        response.setDeliveryStatus(order.getDeliveryStatus());
        response.setDelivered(order.getDeliveryStatus() == DeliveryStatus.DELIVERED);
        response.setDeliveredAt(order.getDeliveredAt());
        response.setCreatedAt(order.getCreatedAt());

        return response;
    }
}
