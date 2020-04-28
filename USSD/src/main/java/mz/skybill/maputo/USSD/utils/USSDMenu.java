package mz.skybill.maputo.USSD.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import mz.skybill.matola.ussd.entities.Session;
import mz.skybill.matola.ussd.entities.SessionLog;
import mz.skybill.matola.ussd.models.*;
import mz.skybill.matola.ussd.repos.SessionLogRepository;
import mz.skybill.matola.ussd.services.CustomerService;
import mz.skybill.matola.ussd.services.MatolaService;
import mz.skybill.matola.ussd.services.USSDService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class USSDMenu<T> {


    private  static  MatolaService matolaService;
    private  static CustomerService customerService;
    private static SessionLogRepository sessionLogRepository;
    private static ObjectMapper objectMapper;
    private static USSDService ussdService;




    public USSDMenu(MatolaService matolaService,USSDService ussdService, CustomerService customerService,SessionLogRepository sessionLogRepository,ObjectMapper objectMapper)
    {
        this.customerService = customerService;
        this.sessionLogRepository = sessionLogRepository;
        this.objectMapper = objectMapper;
        this.ussdService=ussdService;
        this.matolaService = matolaService;
    }

    public static Logger log = LoggerFactory.getLogger(USSDMenu.class);

    public static Object returnObject(Object object){
        return object;
    }


    public static String  getMatolaUssdMessage(Session session,List<String> inputs,Object object) {
        String text = "";
        if (inputs.isEmpty()) {

            ProductListResponseData productListResponseData = matolaService.getAllProducts(session);
            String newText = Util.prependText(Translator.toLocale("select.option"),USSDUtil.prefillData(productListResponseData,Constants.Exceptions.PERMITS),Translator.toLocale("menu.page"));
            text = USSDUtil.getText(newText, State.CON);

        } else if (inputs.stream().findFirst().get().equalsIgnoreCase("1")) {
            text = IpaOptions(session, USSDUtil.skipFirst(inputs),object);
        }  else if (inputs.stream().findFirst().get().equalsIgnoreCase("2")) {

            text = MarketOptions(session, USSDUtil.skipFirst(inputs),object);

        } else if (inputs.stream().findFirst().get().equalsIgnoreCase("3")){

            text = BusinessPermitOptions(session, USSDUtil.skipFirst(inputs),object);
        } else if (inputs.stream().findFirst().get().equalsIgnoreCase("99")){

            text = getVerifyReceipt(session,USSDUtil.skipFirst(inputs));
        }else if (inputs.stream().findFirst().get().equalsIgnoreCase("100")){

            text = getUserProfile(session,USSDUtil.skipFirst(inputs));
        } else {
            text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
        }
        return text;
    }


    //Main Menu
    public static String IpaOptions(Session session, List<String> inputs,Object object) {
        String text;
        switch (inputs.size()) {
            case 0:
                text = USSDUtil.getText(Translator.toLocale("ipa.options"), State.CON);
                break;
            default:
                switch (inputs.stream().findFirst().get()) {
                    case "1":
                        text = ipaPaymentOptions(session, USSDUtil.skipFirst(inputs),object);
                        break;
                    case "2":
                        text = listBeneficiaries(session,USSDUtil.skipFirst(inputs));
                        break;
                    case "3":
                        text = addBeneficiary(session,USSDUtil.skipFirst(inputs));
                        break;
                    default:
                        text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);

                }
        }

        return text;
    }

    public static String MarketOptions(Session session,List<String> inputs,Object object){
        String text;
        switch (inputs.size()) {
            case 0:
                text = USSDUtil.getText(Translator.toLocale("markets.options"), State.CON);
                break;
            default:
                switch (inputs.stream().findFirst().get()) {
                    case "1":
                        text = getMarketOptionsList(session,USSDUtil.skipFirst(inputs),object);
                        break;
                    case "2":
                        text = checkMarketPaymentStatus(session,USSDUtil.skipFirst(inputs));
                        break;
                    default:
                        text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
                }
        }

        return text;

    }

    public static String BusinessPermitOptions(Session session,List<String> inputs,Object object){
        String text;
        switch (inputs.size()) {
            case 0:
                text = USSDUtil.getText(Translator.toLocale("business.permits.options"), State.CON);
                break;
            default:
                switch (inputs.stream().findFirst().get()) {
                    case "1":
                        text = getBusinessPermitOptionsList(session,USSDUtil.skipFirst(inputs),object);
                        break;
                    case "2":
                        text = checkBusinessPermitPaymentStatus(session,USSDUtil.skipFirst(inputs));
                        break;
                    case "3":
                        text = getPermitSerial(session,USSDUtil.skipFirst(inputs));
                        break;
                    default:
                        text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
                }
        }

        return text;

    }

    public static String getVerifyReceipt(Session session, List<String> inputs){
        String text;
        List<SessionLog> sessionLogs = sessionLogRepository.findAllBySession(session);
        List<String> data = USSDUtil.getStringInputs(sessionLogs);
        Object object = null;
        switch (inputs.size()) {
            case 0:
                text = USSDUtil.getText(Translator.toLocale("user.receipt"), State.CON);
                break;
            case 1:
//                object = manipulationService.getUssdInputs(session,sessionLogs,data);
                log.info("Alte_Objects: {}",object);
                text = USSDUtil.getText(Translator.toLocale("receipt.details"), State.END);
                break;
            default:
                text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
        }
        return text;

    }

    public static String getUserProfile(Session session,List<String> inputs){
        String text;
        UserModel userModel = null;
        if (session != null){
            userModel = customerService.getUserProfile(session);
        }
        String newText = Util.prependText("",USSDUtil.prefillUserData(userModel),"");
        text = USSDUtil.getText(newText, State.CON);
        return text;
    }



    //Sub-Menu

    public static String ipaPaymentOptions(Session session,List<String> inputs,Object object){
        String text = null;
        boolean isText = false;
        switch (inputs.size()) {
            case 0:
                text = USSDUtil.getText(Translator.toLocale("ipa.payment.options"), State.CON);
                break;
            default:
                switch (inputs.stream().findFirst().get()) {
                    case "1":
                        break;
                    case "2":
                        break;
                    case "3":
                        break;
                    default:
                        isText = true;
                        text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);

                }
                if (!isText)
                    text = paymentOptions(session, USSDUtil.skipFirst(inputs),object);
        }

        return text;
    }

    public static String getMarketOptionsList(Session session, List<String> inputs,Object object){
//        CustomerProductListModel customerProducts = customerService.getCustomerProductList(Constants.MatolaProducts.MARKETS,session);
        List<CustomerProduct> customerProducts = customerService.getCustomerProductsBasedOnProduct(Constants.MatolaProducts.MARKETS,session);
        String text = null;
        boolean isText = false;
        switch (inputs.size()){
            case 0:
                String newText = "";
                if (!customerProducts.isEmpty()){
                    newText = Util.prependText(Translator.toLocale("market.pay"),USSDUtil.prefillData(customerProducts,Constants.Exceptions.MARKETS),"");

                } else {
                    newText = Util.prependText(" ",USSDUtil.prefillData(customerProducts,Constants.Exceptions.MARKETS),"");

                }
                text = USSDUtil.getText(newText, State.CON);
                break;
            default:
                switch (inputs.stream().findFirst().get()){
                    case "1":
                        break;
                    case "2":
                        break;
                    case "3":
                        break;
                    case "4":
                        break;
                    default:
                        isText = true;
                        text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
                }
                if (!isText)
                    text = getPaymentPeriod(session, USSDUtil.skipFirst(inputs),Constants.MatolaProducts.MARKETS);

        }

        return text;
    }

    public static String checkMarketPaymentStatus(Session session, List<String> inputs){
//        CustomerProductListModel customerProducts = customerService.getCustomerProductList(Constants.MatolaProducts.MARKETS,session);
        List<CustomerProduct> customerProducts = customerService.getCustomerProductsBasedOnProduct(Constants.MatolaProducts.MARKETS,session);

        String text = null;
        boolean isText = false;
        switch (inputs.size()){
            case 0:
                String newText = "";
                if (!customerProducts.isEmpty()){
                    newText = Util.prependText(Translator.toLocale("check.payment.status"),USSDUtil.prefillData(customerProducts,Constants.Exceptions.MARKETS),"");
                } else {
                    newText = Util.prependText("",USSDUtil.prefillData(customerProducts,Constants.Exceptions.MARKETS),"");

                }
                text = USSDUtil.getText(newText, State.CON);
                break;
            default:
                switch (inputs.stream().findFirst().get()){
                    case "1":
                        break;
                    case "2":
                        break;
                    case "3":
                        break;
                    case "4":
                        break;
                    default:
                        isText = true;
                        text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
                }
                if (!isText)
                    text = USSDUtil.getText(Translator.toLocale("notification.message"), State.END);

        }

        return text;

    }

    public static  String getBusinessPermitOptionsList(Session session, List<String> inputs,Object object){
//        CustomerProductListModel customerProducts = customerService.getCustomerProductList(Constants.MatolaProducts.TAE,session);
        List<CustomerProduct> customerProducts = customerService.getCustomerProductsBasedOnProduct(Constants.MatolaProducts.TAE,session);
        String text = null;
        boolean isText = false;
        switch (inputs.size()){
            case 0:
                String newText = "";
                if (!customerProducts.isEmpty()){
                    newText =Util.prependText(Translator.toLocale("pay"),USSDUtil.prefillData(customerProducts,Constants.Exceptions.PERMITS),"");

                } else {
                    newText =Util.prependText(" ",USSDUtil.prefillData(customerProducts,Constants.Exceptions.PERMITS),"");

                }
                text = USSDUtil.getText(newText, State.CON);
                break;
            default:
                switch (inputs.stream().findFirst().get()){
                    case "1":
                        break;
                    case "2":
                        break;
                    case "3":
                        break;
                    case "4":
                        break;
                    default:
                        isText = true;
                        text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
                }
                if (!isText)
                    text = getPaymentPeriod(session, USSDUtil.skipFirst(inputs),Constants.MatolaProducts.TAE);

        }

        return text;
    }

    public static String checkBusinessPermitPaymentStatus(Session session, List<String> inputs){
//        CustomerProductListModel customerProducts = customerService.getCustomerProductList(Constants.MatolaProducts.TAE,session);
        List<CustomerProduct> customerProducts = customerService.getCustomerProductsBasedOnProduct(Constants.MatolaProducts.TAE,session);
        String text = null;
        boolean isText = false;
        switch (inputs.size()){
            case 0:
                String newText = "";
                if (!customerProducts.isEmpty()){
                    newText =Util.prependText(Translator.toLocale("pay"),USSDUtil.prefillData(customerProducts,Constants.Exceptions.PERMITS),"");
                } else {
                    newText =Util.prependText(" ",USSDUtil.prefillData(customerProducts,Constants.Exceptions.PERMITS),"");
                }
                text = USSDUtil.getText(newText, State.CON);
                break;
            default:
                switch (inputs.stream().findFirst().get()){
                    case "1":
                        break;
                    case "2":
                        break;
                    case "3":
                        break;
                    case "4":
                        break;
                    default:
                        isText = true;
                        text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
                }
                if (!isText)
                    text = USSDUtil.getText(Translator.toLocale("notification.message"), State.END);

        }

        return text;

    }

    public static String getPermitSerial(Session session, List<String> inputs){
        String text;
        switch (inputs.size()) {
            case 0:
                text = USSDUtil.getText(Translator.toLocale("user.permit.serial"), State.CON);
                break;
            default:
//                text = getPermitSerialOptions(session,USSDUtil.skipFirst(inputs));
                text =  getPaymentPeriod(session,USSDUtil.skipFirst(inputs),Constants.MatolaProducts.TAE);
                break;
        }
        return text;

    }


        //Get BusinessPermit Serial Options
    public static String getPermitSerialOptions(Session session,List<String> inputs){
        String data = session.getSessionInputs().get(session.getSessionInputs().size() -1);
        CustomerProduct customerProduct = null;
        if (data != null){
            customerProduct = matolaService.checkRenewalProfile(data,session).getData();
        }
        String text;
        switch (inputs.size()){
            case 0:
                String newText = Util.prependText("",USSDUtil.prefillData(data,customerProduct),Translator.toLocale("payment.options"));
                if (customerProduct != null && customerProduct.getProduct().getName().equals(Constants.MatolaProducts.TAE)){
                    text = USSDUtil.getText(newText, State.CON);
                }
                else {
                    text = USSDUtil.getText(Translator.toLocale("permit.unavailable",data), State.CON);
                }
                break;
            default:
                /*
                switch (inputs.stream().findFirst().get()) {
                    case "1":
                        text = USSDUtil.getText(Translator.toLocale("mpesaInvoice.success"), State.END);
                        break;
                    case "2":
                        text = USSDUtil.getText(Translator.toLocale("movitelInvoice.success"), State.END);
                        break;
                    case "3":
                        text = USSDUtil.getText(Translator.toLocale("mKashInvoice.success"), State.END);
                        break;
                    case "4":
                        text = USSDUtil.getText(Translator.toLocale("bankInvoice.success"), State.END);
                        break;
                    default:
                        text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
                        break;

                }

                 */
                text = "Nice";
                break;

        }

        return text;


    }

    public static String IpraOptions(Session session, List<String> inputs,Object object) {
        String text = null;
        boolean isText = false;
        switch (inputs.size()) {
            case 0:
                text = USSDUtil.getText(Translator.toLocale("ipra.options"), State.CON);
                break;
            default:
                switch (inputs.stream().findFirst().get()) {
                    case "1":
                        text = IpraOptionsList(session,USSDUtil.skipFirst(inputs),object);
                        break;
                    case "2":
                        text = USSDUtil.getText(Translator.toLocale("payment.options"), State.CON);
                        break;
                    default:
                        if (!isText)
                            text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
                }
        }

        return text;
    }

    public static String IpraOptionsList(Session session, List<String> inputs,Object object){
        String text = null;
        boolean isText = false;
        switch (inputs.size()){
            case 0:
                text = USSDUtil.getText(Translator.toLocale("propety_list_option"), State.CON);
                break;
            default:
                switch (inputs.stream().findFirst().get()){
                    case "1":
                        break;
                    case "2":
                        break;
                    case "3":
                        break;
                    case "4":
                        break;
                    default:
                        isText = true;
                        text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
                }
                if (!isText)
                    text = paymentOptions(session, USSDUtil.skipFirst(inputs),object);

        }

        return text;
    }

    public static String addBeneficiary(Session session,List<String> inputs){
        String text;
        switch (inputs.size()) {
            case 0:
                text = USSDUtil.getText(Translator.toLocale("user.phone"), State.CON);
                break;
            case 1:
                text = USSDUtil.getText(Translator.toLocale("user.id"), State.CON);
                break;
            case 2:
                text = USSDUtil.getText(Translator.toLocale("user.first.name"), State.CON);
                break;
            case 3:
                text = USSDUtil.getText(Translator.toLocale("user.last.name"), State.CON);
                break;
            case 4:
                text = USSDUtil.getText(Translator.toLocale("user.success"), State.END);
                break;
            default:
                text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);

        }

        return text;


    }

    public static String listBeneficiaries(Session session,List<String> inputs){
        String text;
        List<BeneficiaryModel> beneficiaryModels = matolaService.getCustomersBeneficiary(session).getData();
        String newText = Util.prependText(Translator.toLocale("list.beneficiaries"),USSDUtil.prefillBeneficiaryData(beneficiaryModels,Constants.Exceptions.BENEFICIARIES),"");
        text = USSDUtil.getText(newText, State.CON);
        return text;

    }

    //General Use

    public static String getPaymentPeriod(Session session, List<String> inputs,String parentProduct){

        List<ProductPaymentPeriodModel> paymentPeriodModels = null;

        int productId = matolaService.getAllProducts(session).getData().stream()
                .filter(productModel -> productModel.getName().equalsIgnoreCase(parentProduct)).mapToInt(productModel -> productModel.getId()).findFirst().orElse(0);

        if (productId != 0)
            paymentPeriodModels = matolaService.getPaymentPeriods(session,productId).getData();



        String text = null;
        boolean isText = false;
        switch (inputs.size()){
            case 0:
                String newText = Util.prependText(Translator.toLocale("payment.period"),USSDUtil.prefillPaymentListData(paymentPeriodModels),"");
                text = USSDUtil.getText(newText, State.CON);
                break;
            default:
                switch (inputs.stream().findFirst().get()){
                    case "1":
                        break;
                    case "2":
                        break;
                    case "3":
                        break;
                    case "4":
                        break;
                    case "5":
                        break;
                    default:
                        isText = true;
                        text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
                }
                if (!isText)
                    text = paymentOptions(session, USSDUtil.skipFirst(inputs),null);

        }

        return text;

    }

    public static String paymentOptions(Session session, List<String> inputs,Object object) {



        Item paymentMessage = null;
        //Logic to get Bill Presentment

        List<String> inputData = session.getSessionInputs();
        int paymentPeriod = 0;
        IpaBeneficiaryOption ipaBeneficiaryOption = null;
        CustomerProduct customerProduct = null;
        String productPaymentPeriod = null;
//        inputData.remove(0);
        List<SessionLog> sessionLogs = sessionLogRepository.findAllBySession(session);
        List<String> data = USSDUtil.getStringInputs(sessionLogs);

        if (inputData.get(1).equalsIgnoreCase("1")){
            ipaBeneficiaryOption = customerService.getBillPresentmentBeneficiary(session);
            customerProduct = matolaService.getCustomerProduct(1,session).getData().get(0);
        } else {
            customerProduct = customerService.getCustomerProduct(data.get(0).toUpperCase(),data.get(2),session);
        }


//        IpaBeneficiaryOption ipaBeneficiaryOption = customerService.getBeneficiaries(data.get(2),session);
        StatusResponseData statusResponseData = customerService.getBillPresentment(ipaBeneficiaryOption,customerProduct,data.get(0),session);
        String text;
        switch (inputs.size()) {
            case 0:
                if (statusResponseData.getData() != null){
                    String newText = Util.prependText("",USSDUtil.prefillBillPresentmentData(statusResponseData,Constants.Exceptions.PERMITS),Translator.toLocale("payment.options"));
                    text = USSDUtil.getText(newText, State.CON);
                } else {
                    String newText = Util.prependText(Translator.toLocale("transaction.failed"),USSDUtil.prefillBillPresentmentData(statusResponseData,""),Translator.toLocale("try.again"));
                    text = USSDUtil.getText(newText, State.END);
                }
                break;
            default:
                paymentMessage = objectMapper.convertValue(object,Item.class);
//                paymentMessage = matolaService.taxPaymentEnforcement(null,)
                switch (inputs.stream().findFirst().get()) {
                    case "1":
                        text = USSDUtil.getText(Translator.toLocale("movitelInvoice.success"), State.END);
                        break;
                    case "2":
                        text = USSDUtil.getText(Translator.toLocale("movitelInvoice.success"), State.END);
                        break;
                    case "3":
                        text = USSDUtil.getText(Translator.toLocale("mKashInvoice.success"), State.END);
                        break;
                    case "4":
                        text = USSDUtil.getText(Translator.toLocale("bankInvoice.success"), State.END);
                        break;
                    default:
                        text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
                }
        }

        return text;
    }

    public static String registerUser(Session session, List<String> inputs) {
        String text;
        switch (inputs.size()) {
            case 0:
                text = USSDUtil.getText(Translator.toLocale("user.id"), State.CON);
                break;
            case 1:
                text = USSDUtil.getText(Translator.toLocale("user.first.name"), State.CON);
                break;
            case 2:
                text = USSDUtil.getText(Translator.toLocale("user.last.name"), State.CON);
                break;
            case 3:
                text = USSDUtil.getText(Translator.toLocale("user.success"), State.CON);
                break;
            default:
                text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
        }
        return text;
    }

    public static String exemptionOption(Session session, List<String> inputs) {
        List<ProductExemptionModel> taxExemptionModels = matolaService.getProductExemptions(session).getData();
        String text = null;
        boolean isText = false;
        switch (inputs.size()) {
            case 0:
                String newText = Util.prependText(Translator.toLocale("ipa.exemption"),USSDUtil.prefillData(taxExemptionModels),"");
                text = USSDUtil.getText(newText, State.CON);
                break;
            default:
                switch (inputs.stream().findFirst().get()) {
                    case "1":
                        break;
                    case "2":
                        break;
                    case "3":
                        break;
                    case "4":
                        break;
                    case "5":
                        break;
                    case "6":
                        break;
                    case "7":
                        break;
                    case "8":
                        break;
                    default:
                        isText = true;
                        text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
                }
                if (!isText)
                    text = USSDUtil.getText(Translator.toLocale("matola.exemption"), State.END);
        }

        return text;


    }




    interface State {
        boolean CON = false;
        boolean END = true;
    }
}
