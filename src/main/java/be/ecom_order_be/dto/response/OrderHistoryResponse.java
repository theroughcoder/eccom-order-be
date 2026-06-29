package be.ecom_order_be.dto.response;

import be.ecom_order_be.model.DeliveryStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderHistoryResponse {
    private Long id;
    private LocalDateTime createdAt;
    private double totalPrice;
    private boolean isPaid;
    private LocalDateTime paidAt;
    private DeliveryStatus deliveryStatus;
    private boolean isDelivered;
    private LocalDateTime deliveredAt;
}
