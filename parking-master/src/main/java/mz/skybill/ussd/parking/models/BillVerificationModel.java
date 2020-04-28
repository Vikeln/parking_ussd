package mz.skybill.ussd.parking.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillVerificationModel {
    private Item status;
    private CustomerProduct customerProduct;
    private Date dateApproved;
    private String invoice;
    private Boolean approved;
}
