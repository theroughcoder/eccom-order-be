package be.ecom_order_be.dto.response;

import lombok.Data;

@Data
public class PaymentIntentResponse {
    private String id;
    private String orderId;
    private double amount;
    private String currency;
    private String status;
    private String clientSecret;
}
