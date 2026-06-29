package be.ecom_order_be.dto.response;

import be.ecom_order_be.model.DeliveryStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderResponse {
    private Long id;
    private Long productId;
    private String slug;
    private String image;
    private String name;
    private double rating;
    private int numReviews;
    private double price;
    private int countInStock;
    private String category;
    private String description;
    private int quantity;
    private ShippingAddressResponse shippingAddress;
    private String paymentMethod;
    private double itemsPrice;
    private double shippingPrice;
    private double tax;
    private double totalPrice;
    private boolean isPaid;
    private LocalDateTime paidAt;
    private DeliveryStatus deliveryStatus;
    private boolean isDelivered;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
}
