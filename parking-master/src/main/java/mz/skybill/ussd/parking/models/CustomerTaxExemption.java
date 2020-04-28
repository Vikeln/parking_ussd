package mz.skybill.ussd.parking.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerTaxExemption {
    private Integer id;
    private CustomerModel customer;
    private CustomerProduct product;
}
