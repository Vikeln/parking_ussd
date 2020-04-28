package mz.skybill.ussd.parking.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptResponse {
    private Status status;
    private BillModel data;
}
