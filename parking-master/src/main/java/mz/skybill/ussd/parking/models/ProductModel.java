package mz.skybill.ussd.parking.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductModel {
    private Integer id;
    private String name;
    private boolean global;
    private CategoryModel category;
}
