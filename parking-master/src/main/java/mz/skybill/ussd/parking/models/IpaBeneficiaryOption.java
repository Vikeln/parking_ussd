package mz.skybill.ussd.parking.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IpaBeneficiaryOption {
    private int ipaOption;
    private List<Integer> beneficiaries;
}
