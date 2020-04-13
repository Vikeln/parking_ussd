package ke.co.skybill.revenuecollection.customer.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillPaymentRequest {

    private List<Integer> billEntries;
    private Integer customer;
    private String paymentMethod;
}
