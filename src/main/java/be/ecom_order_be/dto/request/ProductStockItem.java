package be.ecom_order_be.dto.request;

import lombok.Data;

@Data
public class ProductStockItem {
    private long id;
    private int quantity;
    private String name;
}
