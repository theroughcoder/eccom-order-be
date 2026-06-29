package be.ecom_order_be.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private String userId;
    private List<OrderItemRequest> orderItems;
    private ShippingAddressRequest shippingAddress;
    private String paymentMethod;
    private double itemsPrice;
    private double shippingPrice;
    private double tax;
    private double totalPrice;
}
