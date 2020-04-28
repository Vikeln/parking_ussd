package mz.skybill.ussd.parking.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerProductParkingRequest {
    private Integer id;
    private Integer customer;
    private Integer product;
    private String vehicleRegistration;

    public static CustomerProductParkingRequest transform(Integer customer, Integer product, String vehicleRegistration) {
        CustomerProductParkingRequest model = new CustomerProductParkingRequest();
        model.setCustomer(customer);
        model.setProduct(product);
        model.setVehicleRegistration(vehicleRegistration);
        return model;
    }
}
