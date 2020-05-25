package ke.co.skybill.revenuecollection.accounts.kafka.models;

import ke.co.skybill.revenuecollection.accounts.entities.ClientDepartment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDeptEvent {
    private String event;
    private ClientDepartment data;
}
