package mz.skybill.ussd.parking.services;

import mz.skybill.ussd.parking.entities.Session;
import mz.skybill.ussd.parking.entities.SessionLog;
import mz.skybill.ussd.parking.models.*;
import mz.skybill.ussd.parking.repos.SessionRepository;
import mz.skybill.ussd.parking.utils.USSDUtil;
import mz.skybill.ussd.parking.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManipulationService {
    @Autowired
    private MatolaService matolaService;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private CustomerService customerService;


    Logger log = LoggerFactory.getLogger(MatolaService.class);


    public Object getUssdInputs(Session session, List<SessionLog> sessionLogs) {
        List<String> sessionInputs = session.getSessionInputs();
        sessionInputs.remove(0);

        CustomerProduct customerProduct = null;
        IpaBeneficiaryOption ipaBeneficiaryOption = null;
        CustomerProductListModel customerProductListModel = null;
        TaxExemptionModel taxExemptionModel = null;
        Object object = null;
        int product_paymentId = 0;

        List<String> data = USSDUtil.getStringInputs(sessionLogs);
        log.info("DATA: {}", data);
        int payment_productId = 0;
//        Collections.reverse(data);
        if (sessionInputs.size() >= 1) {
            switch (sessionInputs.get(0)) {
                case "1":
                    break;
                case "2":
                    break;
                case "3":   // Penalties
//                    customerProduct = customerService.getCustomerProduct(data.get(0).toUpperCase(), data.get(2), session);
//                    switch (sessionInputs.size()) {
//                        case 2:
//                            break;
//                        case 3:
//                            if (sessionInputs.get(1).equals("2")) {
//                                object = matolaService.checkProductProfile(customerProduct, session);
//                            } else {
//
//                                customerProduct = customerService.getCustomerProduct(data.get(0).toUpperCase(), data.get(2), session);
////                                matolaService.checkProductFile(customerProduct,session);
//                            }
//
//                            break;
//                        case 5:
//                            if (customerProduct != null)
//                                payment_productId = customerService.getProductPaymentPeriod(session, data);
//                            object = matolaService.taxPaymentEnforcement(null, customerProduct, session, payment_productId);
////                            else
////                                object = customerService.getBillPresentment(null,customerProduct,data.get(2),session);
//
//                            break;
//
//                    }
                    break;
                case "5":
//                    switch (sessionInputs.size()) {
//                        case 2:
//                            object = matolaService.verifyReceipt(data.get(1), session);
//                            break;
//
//                    }
                    break;
                default:
//                    CustomerModel customerModel = matolaService.registerUser(null, data.get(0), data.get(1), data.get(2), session);
//                    if (customerModel != null) {
//                        String accesstoken = matolaService.getAccessToken(session.getMsisdn());
//                        session.setAccessToken(accesstoken);
//                    }
                    break;


            }

        }


        return object;
    }


}
