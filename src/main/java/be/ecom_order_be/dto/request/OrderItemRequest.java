package be.ecom_order_be.dto.request;

import lombok.Data;

@Data
public class OrderItemRequest {
    private Long id;
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
}
