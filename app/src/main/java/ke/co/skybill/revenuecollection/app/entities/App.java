package ke.co.skybill.revenuecollection.app.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class App {
    @Id
    private String appKey;
    private String parent;
    private String appName;
    private Date dateCreated;
    private Date dateDeleted;

    private String gatewayAppKey;
    private String gatewayAppSecret;
    private String gatewaySev;
    private String correlator;
    private String branchCorrelator;

    private String appUser;

    @JoinColumn(name = "docType", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private DocType docType;
    private String certificateNumber;



    @JoinColumn(name = "status", referencedColumnName = "code")
    @ManyToOne(optional = false)
    private Status status;

    @JoinColumn(name = "appCountry", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private AppCountry appCountry;

    private String phoneNumber;
    private String physicalAddress;
    private String postalAddress;
    private String postalCode;
    private String city;
    private String emailAddress;

    public App(String appKey) {
        this.appKey = appKey;
    }

}
