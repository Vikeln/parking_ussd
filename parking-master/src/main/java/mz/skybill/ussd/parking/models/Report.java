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
public class Report {
    private Integer duration;
    private List<Integer> entryID;
    private String product;
    private String paymentMethod;
    private Status status;
    private String customer;
    private GatewayResponse response;
    private List<String> badInvoices;
    private String serial;
    private Integer customerId;
    private String customerName;
    private String customerContact;
    private String description;
    private double amount;
}
