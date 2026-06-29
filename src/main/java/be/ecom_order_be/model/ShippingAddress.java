package be.ecom_order_be.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class ShippingAddress {
    private String fullName;
    private String address;
    private String city;
    private String postalCode;
}
