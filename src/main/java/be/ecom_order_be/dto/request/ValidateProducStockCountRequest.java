package be.ecom_order_be.dto.request;

import be.ecom_order_be.model.OrderItem;
import lombok.Data;

import java.util.List;

@Data
public class ValidateProducStockCountRequest {
    private List<OrderItem> orderItems;
}
