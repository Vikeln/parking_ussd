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
public class CustomerProductListModel {
    private int id;
    private String name;
    private boolean global;
    private double unitValue;
    private Item parentProduct;
    private Item productType;
    private List<CustomerProduct> customerProducts;
    private CategoryModel category;

}
