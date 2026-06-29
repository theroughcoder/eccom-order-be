package be.ecom_order_be.dto.request;

import lombok.Data;

@Data
public class ShippingAddressRequest {
    private String fullName;
    private String address;
    private String city;
    private String postalCode;
}
