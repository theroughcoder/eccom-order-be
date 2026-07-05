package be.ecom_order_be.dto.response;

import lombok.Data;

@Data
public class OrderItemResponse {
    private Long id;
    private Long productId;
    private String slug;
    private String image;
    private String name;
    private double price;
    private int quantity;
}
