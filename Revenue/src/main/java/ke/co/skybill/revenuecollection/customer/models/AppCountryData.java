package ke.co.skybill.revenuecollection.customer.models;

import ke.co.skybill.revenuecollection.customer.entities.AppCountry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppCountryData {
    private Integer id;
    private Integer countryId;
    private String appKey;

}
