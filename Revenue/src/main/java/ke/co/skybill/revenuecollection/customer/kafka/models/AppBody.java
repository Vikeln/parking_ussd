package ke.co.skybill.revenuecollection.customer.kafka.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppBody {
    private String appKey;
    private String parent;
    private String appUser;
    private String appName;

    private String gatewayAppKey;
    private String gatewaySev;
    private String gatewayAppSecret;
    private String correlator;
    private String branchCorrelator;

}
