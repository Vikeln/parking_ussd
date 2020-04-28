package mz.skybill.maputo.USSD.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import mz.skybill.maputo.USSD.entities.Session;
import mz.skybill.maputo.USSD.entities.SessionLog;
import mz.skybill.matola.ussd.entities.Session;
import mz.skybill.matola.ussd.entities.SessionLog;
import mz.skybill.matola.ussd.models.*;
import mz.skybill.matola.ussd.repos.SessionLogRepository;
import mz.skybill.matola.ussd.services.CustomerService;
import mz.skybill.matola.ussd.services.MatolaService;
import mz.skybill.matola.ussd.services.USSDService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class EndDisplay {

    private  static MatolaService matolaService;
    private  static CustomerService customerService;
    private static SessionLogRepository sessionLogRepository;
    private static ObjectMapper objectMapper;
    private static USSDService ussdService;




    public EndDisplay(MatolaService matolaService,USSDService ussdService, CustomerService customerService,SessionLogRepository sessionLogRepository,ObjectMapper objectMapper)
    {
        this.matolaService = matolaService;
        this.customerService = customerService;
        this.sessionLogRepository = sessionLogRepository;
        this.objectMapper = objectMapper;
        this.ussdService=ussdService;
    }

    public static Logger log = LoggerFactory.getLogger(USSDMenu.class);


    public static String getEndTextDisplay(Object object, Session session, List<SessionLog> sessionLogs){
        String displayText = Translator.toLocale("last.text.null");
        List<String> sessionInputs = session.getSessionInputs();


        if (sessionInputs.size() >= 1){
            switch (sessionInputs.get(0)){
                case "1":
                    switch (sessionInputs.size()){
                        case 2:
                            break;
                        case 3:
                            CustomerTaxExemptionResponseData customerTaxExemptionResponseData = new Util<CustomerTaxExemptionResponseData>().fromObject(CustomerTaxExemptionResponseData.class,object);
                            if (customerTaxExemptionResponseData != null)
                                displayText = Translator.toLocale("response",customerTaxExemptionResponseData.getStatus().getMessage());
                            break;
                        case 4:
                            StatusResponseData statusResponseData = new Util<StatusResponseData>().fromObject(StatusResponseData.class,object);
                            if (statusResponseData != null)
                                displayText = Translator.toLocale("response",statusResponseData.getData().getReport().getResponse().getTxtMessage());
                            break;
                        case 5:
                            break;
                        case 6:
                    }
                    break;
                case "2":
                    //Check Product Profile
                    switch (sessionInputs.size()){
                        case 2:
                            break;
                        case 3:
                            ProductProfileModel productProfileModel = new ProductProfileModel();
                            ProductProfileResponseData productProfileResponseData = new Util<ProductProfileResponseData>().fromObject(ProductProfileResponseData.class,object);
                            if (productProfileResponseData != null){
                                productProfileModel = productProfileResponseData.getData();
                            }
                            displayText = Translator.toLocale("check.product.profile",
                                    productProfileModel.getCustomerProduct().getSerial(),
                                    productProfileModel.getCustomerProduct().getStatus().getName(),
                                    productProfileModel.getEntries().get(0).getPaymentPeriod().getName(),
                                    productProfileModel.getEntries().get(0).getStatus().getName());


                            break;
                        case 5:
                            log.info("HERE_THERE");
                            StatusResponseData statusResponseData = new Util<StatusResponseData>().fromObject(StatusResponseData.class,object);
                            if (statusResponseData != null)
                                displayText = Translator.toLocale("response",statusResponseData.getData().getReport().getResponse().getTxtMessage());
                            log.info("DISPLAY_TEXT: {}",displayText);
                            break;

                    }
                    break;
                case "3":
                    switch (sessionInputs.size()){
                        case 2:
                            break;
                        case 3:
                            ProductProfileModel productProfileModel = new ProductProfileModel();
                            ProductProfileResponseData productProfileResponseData = new Util<ProductProfileResponseData>().fromObject(ProductProfileResponseData.class,object);
                            if (productProfileResponseData != null){
                                productProfileModel = productProfileResponseData.getData();
                            }
                            displayText = Translator.toLocale("check.product.profile",
                                    productProfileModel.getCustomerProduct().getSerial(),
                                    productProfileModel.getCustomerProduct().getStatus().getName(),
                                    productProfileModel.getEntries().get(0).getPaymentPeriod().getName() != null?  productProfileModel.getEntries().get(0).getPaymentPeriod().getName() : "Annually",
                                    productProfileModel.getEntries().get(0).getStatus().getName());
                            break;
                        case 5:
                            StatusResponseData statusResponseData = new Util<StatusResponseData>().fromObject(StatusResponseData.class,object);
                            if (statusResponseData != null)
                                displayText = Translator.toLocale("response",statusResponseData.getData().getReport().getResponse().getTxtMessage());
                            break;
                        default:
                            break;
                    }
                    break;
                case "5":
                    switch (sessionInputs.size()){
                        case 2:
                            BillVerificationResponseData billVerificationResponseData = null;
                            BillVerificationModel billVerificationModel = null;
                            billVerificationResponseData = new Util<BillVerificationResponseData>().fromObject(BillVerificationResponseData.class,object);
                            if (billVerificationResponseData.getData() == null || billVerificationResponseData.getData().isEmpty() ){
                                displayText = billVerificationResponseData.getStatus().getMessage();
                            }
                            else{
                                billVerificationModel = billVerificationResponseData.getData().get(0);
                            }

                            if (billVerificationModel != null ){
                                if (billVerificationModel.getApproved().equals(false)){
                                    displayText = Translator.toLocale("verify.invoice.notapproved",
                                            billVerificationModel.getInvoice(),
                                            billVerificationModel.getCustomerProduct().getCustomer().getFirstName()+" "+billVerificationModel.getCustomerProduct().getCustomer().getLastName(),
                                            billVerificationModel.getCustomerProduct().getSerial());
                                } else{
                                    displayText = Translator.toLocale("verify.invoice.approved",
                                            billVerificationModel.getInvoice(),
                                            billVerificationModel.getCustomerProduct().getCustomer().getFirstName()+" "+billVerificationModel.getCustomerProduct().getCustomer().getLastName(),
                                            billVerificationModel.getCustomerProduct().getSerial(),billVerificationModel.getDateApproved());
                                }

                            }
                            break;
                        default:
                            break;

                    }
                    break;
                default:
                    break;


            }

        }

        return displayText;

    }
}
