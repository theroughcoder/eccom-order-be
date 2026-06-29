package be.ecom_order_be.dto.response;

import lombok.Data;

@Data
public class ShippingAddressResponse {
    private String fullName;
    private String address;
    private String city;
    private String postalCode;
}
