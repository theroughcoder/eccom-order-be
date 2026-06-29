package be.ecom_order_be.model;

import lombok.Data;

@Data
public class OrderItem {
    private long id;
    private int quantity;
    private String name;
}
