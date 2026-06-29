package be.ecom_backend.model;

import lombok.Data;

@Data
public class OrderItem {
    private long id;
    private int quantity;
    private String name;
}
