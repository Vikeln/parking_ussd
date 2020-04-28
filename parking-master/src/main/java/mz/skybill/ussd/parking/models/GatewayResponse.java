package mz.skybill.ussd.parking.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GatewayResponse {
    private Status status;
    private String txtMessage;
    private double amount;
    private Integer itemId;
    private String invoiceNo;
    private String id;
    private String code;
    private String merchantRef;
}
