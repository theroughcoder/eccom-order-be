package be.ecom_order_be.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ValidateProducStockCountRequest {
    private List<ProductStockItem> orderItems;
}
