package be.ecom_order_be.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(columnDefinition = "TEXT")
    private String description;

    private int quantity;

    @Embedded
    private ShippingAddress shippingAddress;

    private String paymentMethod;
    private double itemsPrice;
    private double shippingPrice;
    private double tax;
    private double totalPrice;

    private String userId;

    private boolean isPaid;
    private LocalDateTime paidAt;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;

    private LocalDateTime deliveredAt;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
