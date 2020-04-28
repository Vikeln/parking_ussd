package mz.skybill.ussd.parking.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnforceVehicleRequest {
    private String hourlyParkingDuration;
    private Integer zone;
    private String registration;

    public  static EnforceVehicleRequest transform(String reg ,Integer zone ,String hourlyParkingDuration){
        EnforceVehicleRequest model = new EnforceVehicleRequest();
        model.setRegistration(reg);
        if (hourlyParkingDuration!=null)
            model.setHourlyParkingDuration(hourlyParkingDuration+":00");
        model.setZone(zone);
        return model;
    }
}
