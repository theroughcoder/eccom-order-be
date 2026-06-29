package be.ecom_order_be.client;

import be.ecom_order_be.dto.request.ValidateProducStockCountRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;

@Component
public class ProductServiceClient {
    private final RestClient restClient;

    public ProductServiceClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
                .baseUrl("http://ECOM-PRODUCT-BE")  // Eureka service name
                .build();
    }

    public String validateProductInStock(ValidateProducStockCountRequest request) {
        return restClient.post()
                .uri("/api/products/check-products-quantity")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(String.class);
    }
}
