package be.ecom_order_be.dto.request;

import lombok.Data;

@Data
public class MarkOrderPaidRequest {
    private String paymentIntentId;
}
