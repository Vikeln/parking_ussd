package mz.skybill.ussd.parking.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerProductModel {

    private Integer id;
    private String serial;
    private CustomerModel customer;
    private ProductModel product;
    private PaymentStatusModel paymentStatus;
    private List<ProductPropertyFieldModel> values;

}
