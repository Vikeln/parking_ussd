package ke.co.skybill.revenuecollection.customer.kafka.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppsEvent {
    private String event;
    private AppBody data;
}
