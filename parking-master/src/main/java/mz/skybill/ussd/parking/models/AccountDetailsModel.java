package mz.skybill.ussd.parking.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDetailsModel {
    private int id;
    private String firstName;
    private String lastName;
    private String emailAddress;

}
