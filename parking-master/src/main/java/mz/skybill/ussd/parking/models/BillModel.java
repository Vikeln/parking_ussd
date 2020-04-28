package mz.skybill.ussd.parking.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillModel {
    private List<ItemCodeValue> charges;
    private List<ItemCodeValue> fees;
    private double totalAmount;
    private Report report;
    private Status status;
    private String invoiceLink;
}
