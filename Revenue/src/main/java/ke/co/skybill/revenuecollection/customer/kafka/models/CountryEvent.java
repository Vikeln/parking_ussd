package ke.co.skybill.revenuecollection.customer.kafka.models;

import ke.co.skybill.revenuecollection.customer.models.AppCountryData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryEvent {
    private String event;
    private AppCountryData data;
}
