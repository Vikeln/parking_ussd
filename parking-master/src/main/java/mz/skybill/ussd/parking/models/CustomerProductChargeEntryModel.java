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
public class CustomerProductChargeEntryModel {

    private Integer id;
    private double value;
    private String invoice;
    private String narrative;
    private UserModelShort chargedBy;
    private Date dateCharged;
    private Date dueDate;
    private PenaltyModel productCharge;
    private CustomerProductModel customerProduct;
    private PaymentStatusModel status;

}
