package mz.skybill.ussd.parking.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnforceParkingRequest {
    private Integer customer;
    private String paymentMethod;
    private String cardName;
    private Integer product;
    private Integer paymentPeriod;
    private List<EnforceVehicleRequest> vehicles;
    private Integer parkingType;

    public static EnforceParkingRequest transform(Integer customer, String method, Integer product, Integer paymentPeriod, Integer type,List<EnforceVehicleRequest> vehicles){
        EnforceParkingRequest model = new EnforceParkingRequest();
        if (customer!=null)
            model.setCustomer(customer);
        model.setPaymentMethod(method);
        model.setProduct(product);
        model.setPaymentPeriod(paymentPeriod);
        model.setParkingType(type);
        model.setVehicles(vehicles);

        return model;
    }
}
