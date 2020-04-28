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
public class CustomerProductEntryModel {
    private Integer id;
    private boolean approved;
    private double principle;
    private String invoice;
    private Date dateCreated;
    private Date dateApproved;
    private Item approvedBy;
    private PaymentPeriodModel paymentPeriod;
    private PaymentStatusModel status;
}
