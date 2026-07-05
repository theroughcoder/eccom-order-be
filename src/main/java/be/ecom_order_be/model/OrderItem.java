package be.ecom_order_be.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "order_items")
@Data
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Order order;

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
}
