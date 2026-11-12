package be.ecom_order_be.client;

import be.ecom_order_be.dto.request.ValidateProducStockCountRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClient;

@Component
public class ProductServiceClient {
    private final RestClient restClient;

    public ProductServiceClient(RestClient.Builder restClientBuilder,
                                @Value("${services.product.url}") String productServiceUrl) {
        this.restClient = restClientBuilder.clone()
                .baseUrl(productServiceUrl)  // direct URL, no Eureka lookup
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
