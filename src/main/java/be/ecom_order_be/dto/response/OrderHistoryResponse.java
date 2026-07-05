package be.ecom_order_be.dto.response;

import be.ecom_order_be.model.DeliveryStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    // Lombok generates isPaid()/isDelivered() getters for these boolean fields, which Jackson
    // would otherwise serialize as "paid"/"delivered" (stripping the "is") instead of "isPaid"/"isDelivered".
    @JsonProperty("isPaid")
    public boolean isPaid() {
        return isPaid;
    }

    @JsonProperty("isDelivered")
    public boolean isDelivered() {
        return isDelivered;
    }
}
