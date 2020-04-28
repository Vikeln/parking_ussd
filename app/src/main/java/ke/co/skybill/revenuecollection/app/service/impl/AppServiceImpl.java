package ke.co.skybill.revenuecollection.app.service.impl;


import ke.co.skybill.revenuecollection.app.entities.App;
import ke.co.skybill.revenuecollection.app.entities.AppCountry;
import ke.co.skybill.revenuecollection.app.entities.Country;
import ke.co.skybill.revenuecollection.app.entities.DocType;
import ke.co.skybill.revenuecollection.app.kafka.MessageProducer;
import ke.co.skybill.revenuecollection.app.kafka.models.AppCountryData;
import ke.co.skybill.revenuecollection.app.models.*;
import ke.co.skybill.revenuecollection.app.repository.*;
import ke.co.skybill.revenuecollection.app.service.AppService;
import ke.co.skybill.revenuecollection.app.service.NetworkService;
import ke.co.skybill.revenuecollection.app.service.SkybillGateway;
import ke.co.skybill.revenuecollection.app.utils.Response;
import ke.co.skybill.revenuecollection.app.utils.SingleItemResponse;
import ke.co.skybill.revenuecollection.app.utils.Util;
import org.apache.kafka.common.metrics.stats.Count;
import org.aspectj.bridge.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class AppServiceImpl implements AppService {
    private static final Logger log = LoggerFactory.getLogger(AppServiceImpl.class.getName());

    @Value(value = "${topic.app}")
    private String appTopic;

    @Value(value = "${topic.country}")
    private String countryTopic;

    @Autowired
    private SkybillGateway skybillGateway;

    @Autowired
    private MessageProducer messageProducer;

    @Autowired
    private DocTypeDao docTypeDao;
    @Autowired
    private AppDao appDao;

    @Autowired
    private NetworkService networkService;

    @Autowired
    private AppCountryDao appCountryDao;

    @Autowired
    private CountryDao countryDao;

    @Autowired
    private StatusDao statusDao;

    @Override
    public SingleItemResponse<App> createApp(AppRequest request) {
        if (!countryDao.existsById(request.getCountry()))
            return new SingleItemResponse(notFoundStatus("Country"), null);
        if (!docTypeDao.existsById(request.getDocType()))
            return new SingleItemResponse(notFoundStatus("Doc Type"), null);
        App app;
        if (appDao.existsByAppKey(request.getKey()))
            app = appDao.findDistinctByAppKey(request.getKey());
        else {
            app = new App();
            boolean done = false;
            while (!done) {
                String appk = generateAppKey();
                if (!appDao.existsById(appk)) {
                    done = true;
                    app.setAppKey(appk);
                }
            }

        }

        Country country = countryDao.findById(request.getCountry()).get();

        if (!appCountryDao.existsByCountryAndApp(country, app.getAppKey())) {
            AppCountry appCountry = new AppCountry();
            appCountry.setCountry(country);
            appCountry.setCountryName(country.getCountryName());
            appCountry.setApp(app.getAppKey());
            appCountry = appCountryDao.save(appCountry);
            app.setAppCountry(appCountry);
        }


        app.setAppName(request.getName());
        app.setDateCreated(new Date());


        if (request.getParent() != null) {
            if (appDao.existsById(request.getParent())) {
                App parent = appDao.findById(request.getParent()).get();
                app.setParent(parent.getAppKey());
            }
        }

        app.setCity(request.getCity());
        app.setDocType(new DocType(request.getDocType()));
        app.setEmailAddress(request.getEmailAddress());
        app.setPhoneNumber(request.getPhoneNumber());
        app.setPostalCode(request.getPostalCode());
        app.setPostalAddress(request.getPostalAddress());
        app.setPhysicalAddress(request.getPhysicalAddress());
        app.setCertificateNumber(request.getCertificateNumber());
        app.setStatus(new ke.co.skybill.revenuecollection.app.entities.Status("P"));
        app = appDao.save(app);
        app = appDao.findById(app.getAppKey()).get();

        if (app.getCorrelator() != null && app.getGatewayAppKey() != null && app.getGatewayAppSecret() != null && app.getGatewaySev() != null && app.getBranchCorrelator() != null) {
            ProducerEvent event = new ProducerEvent("createUpdate", app);
            messageProducer.publish(appTopic, Util.toJson(event));

            ProducerEvent event1 = new ProducerEvent("createUpdate", AppCountryData.transform(app.getAppCountry()));
            messageProducer.publish(countryTopic, Util.toJson(event1));

        }

        return new SingleItemResponse(Response.SUCCESS.status(), app);
    }

    @Override
    public SingleItemResponse<Status> deleteApp(String name) {
        if (!appDao.existsByAppName(name))
            return new SingleItemResponse<>();
        App app = appDao.findDistinctByAppName(name).get();
        app.setDateDeleted(new Date());
        app = appDao.save(app);

        ProducerEvent event = new ProducerEvent("delete", app);
        messageProducer.publish(appTopic, Util.toJson(event));

        return new SingleItemResponse(Response.SUCCESS.status(), null);
    }


    @Override
    public SingleItemResponse<App> assignApp(AssignAppRequest request) {
        Status not = Response.NOT_FOUND.status();
        if (!appDao.existsByAppName(request.getAppName()))
            return new SingleItemResponse(new Status(not.getCode(), MessageFormat.format(not.getMessage(), "App")), null);
        App app = appDao.findDistinctByAppName(request.getAppName()).get();
        app.setGatewayAppKey(request.getGatewayAppKey());
        app.setGatewayAppSecret(request.getGatewayAppSecret());
        app.setGatewaySev(request.getGatewaySev());
        app.setBranchCorrelator(request.getGatewayBranchCorrelator());
        app.setCorrelator(request.getGatewaycorrelator());
        app.setAppUser(request.getAppUser());
        app.setStatus(new ke.co.skybill.revenuecollection.app.entities.Status("C"));

        app = appDao.save(app);

        ClientResponse response = skybillGateway.createAppCustomer(app.getAppCountry().getCountry().getCountryCode(), app.getAppName(), " Customer", app.getPhoneNumber(), app.getCertificateNumber(), networkService.getToken(app.getGatewayAppKey(), app.getGatewayAppSecret()), app.getGatewayAppKey());

        if (response.getStatus().getCode() == 0) {
            app.setAppUser(Integer.toString(response.getItemId()));
            app = appDao.save(app);

            app = appDao.findById(app.getAppKey()).get();
            if (app.getCorrelator() != null && app.getGatewayAppKey() != null && app.getGatewayAppSecret() != null && app.getGatewaySev() != null && app.getBranchCorrelator() != null) {
                ProducerEvent event = new ProducerEvent("createUpdate", AppBody.transform(app));
                messageProducer.publish(appTopic, Util.toJson(event));

                ProducerEvent event1 = new ProducerEvent("createUpdate", AppCountryData.transform(app.getAppCountry()));
                messageProducer.publish(countryTopic, Util.toJson(event1));

            }
        }

        return new SingleItemResponse(Response.SUCCESS.status(), app);
    }


    public static String generateAppKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    public static Status notFoundStatus(String s) {
        return new Status(Response.NOT_FOUND.status().getCode(), MessageFormat.format(Response.NOT_FOUND.status().getMessage(), s));
    }
}

