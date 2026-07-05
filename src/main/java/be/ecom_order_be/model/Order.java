package be.ecom_order_be.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentIntentId;
    private String clientSecret;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems = new ArrayList<>();

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
