package be.ecom_order_be.client;

import be.ecom_order_be.dto.request.CreatePaymentIntentRequest;
import be.ecom_order_be.dto.response.PaymentIntentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentServiceClient {
    private final RestClient restClient;

    public PaymentServiceClient(RestClient.Builder restClientBuilder,
                                @Value("${services.payment.url}") String paymentServiceUrl) {
        this.restClient = restClientBuilder.clone()
                .baseUrl(paymentServiceUrl)  // direct URL, no Eureka lookup
                .build();
    }

    public PaymentIntentResponse createPaymentIntent(CreatePaymentIntentRequest request) {
        return restClient.post()
                .uri("/api/payments/intents")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(PaymentIntentResponse.class);
    }
}
