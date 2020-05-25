package ke.co.skybill.revenuecollection.accounts.kafka.models;

import ke.co.skybill.revenuecollection.accounts.entities.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ClientEvent {
    private String event;
    private Client data;
}
