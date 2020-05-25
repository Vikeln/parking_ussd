package ke.co.skybill.revenuecollection.customer.models;

import ke.co.skybill.revenuecollection.customer.entities.UserMovementLog;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovementLogModel {
    private Integer id;
    private UserActionLogModel userActionLog;
    private double longitude;
    private double latitude;
    private Date dateCreated;

    public static MovementLogModel transform(UserMovementLog log) {
        MovementLogModel model = new MovementLogModel();
        model.setDateCreated(log.getDateCreated());
        model.setId(log.getId());
        model.setLatitude(log.getLatitude());
        model.setLongitude(log.getLongitude());
        if (log.getUserActionLog() != null)
            model.setUserActionLog(UserActionLogModel.transform(log.getUserActionLog()));
        return model;
    }
}
