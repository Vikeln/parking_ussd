package mz.skybill.ussd.parking.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerProduct {
    private Integer id;
    private String serial;
    private List<ProductFieldModel> fields;
    private List<ProductFieldModel> values;
    private CustomerModel customer;
    private CustomerStatus status;
    private ProductModel product;
}
