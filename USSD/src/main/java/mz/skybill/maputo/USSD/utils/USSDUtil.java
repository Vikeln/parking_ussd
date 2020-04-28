package mz.skybill.maputo.USSD.utils;


import mz.skybill.maputo.USSD.entities.Session;
import mz.skybill.maputo.USSD.entities.SessionLog;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class USSDUtil<T> {

    @Autowired
    private static USSDService ussdService;

    public static Logger log = LoggerFactory.getLogger(USSDUtil.class);




    public static String getText(String text, boolean isEnd) {
        if (!isEnd) {
            if (text.startsWith("#")) {
                return "CON " + text.substring(1) + Translator.toLocale("menu.exit");
            } else {
                return "CON " + text.trim() + Translator.toLocale("menu.main.back.exit");
            }
        } else {
            return "END " + text.trim();
        }
    }

    public static String append(String text,String addedText) {
        String newText = text.substring(4);
        if(newText.startsWith("!")){
            newText = newText.substring(1);
        }
        return text.substring(0,4).concat(addedText+". "+newText);
    }

    public static void goBack(Session session, List<String> inputs) {
        List<String> sessionInputs = new ArrayList<>(Arrays.asList(session.getInputs().split("\\*")));
        if(sessionInputs.size() > 1){
            sessionInputs.remove(sessionInputs.size()-1);
            inputs.remove(inputs.size()-1);
            session.setInputs(String.join("*",sessionInputs));
            ussdService.save(session);
        }
    }


    public static List<String> skipFirst(List<String> inputs) {
        return skip(inputs, 1);
    }

    public static List<String> skip(List<String> inputs, int count) {
        return inputs.stream().skip(count).collect(Collectors.toList());
    }

    public static String getInput(List<String> inputs) {
        return inputs.size() > 0 ? inputs.get(inputs.size() - 1) : "";
    }

    public static String exit(Session session) {
        return getText(Translator.toLocale("thank.you"),true);
    }

    public static String logout(Session session) {
        return getText(Translator.toLocale("thank.you.logout"), true);
    }

    public static List<String> prefillData(CustomerProductListModel customerProductListModel,String exception){
        List<String> inputs = new ArrayList<>();
        String text = "";
        int i = 1;

        if (customerProductListModel != null){
            for (CustomerProduct customerProduct : customerProductListModel.getCustomerProducts()){

                String name = customerProduct.getFields().stream().
                        filter(productFieldModel -> productFieldModel.getField().getName().equalsIgnoreCase(Constants.Fields.NAME)).
                        map(productFieldModel -> productFieldModel.getValue()).findFirst().orElse(null);

                if (name == null)
                    name = "";
                text = Translator.toLocale("prefill.data",i, name+"-"+customerProduct.getSerial());
                i++;
                inputs.add(text);
            }
        } else {
            text = Translator.toLocale("products.unavailable",exception);
            inputs.add(text);

        }

        return inputs;
    }

    public static List<String> prefillData( List<CustomerProduct> customerProducts,String exception){
        List<String> inputs = new ArrayList<>();
        String text = "";
        int i = 1;

        if (!customerProducts.isEmpty()){
            for (CustomerProduct customerProduct :customerProducts){
                String name = "";

                if (customerProduct.getProduct().getName().equalsIgnoreCase(Constants.MatolaProducts.MARKETS)){
                    log.info("HERE");
                    name = customerProduct.getValues().stream()
                            .filter(productFieldModel -> productFieldModel.getField().getName().equalsIgnoreCase(Constants.Fields.MARkET_STRUCTURE)).
                                    map(productFieldModel -> productFieldModel.getValue()).findFirst().orElse(null);
                } else {
                    log.info("THERE");
                    name  = customerProduct.getValues().stream().
                            filter(productFieldModel -> productFieldModel.getField().getName().equalsIgnoreCase(Constants.Fields.NAME)).
                            map(productFieldModel -> productFieldModel.getValue()).findFirst().orElse(null);

                }

                text = Translator.toLocale("prefill.data",i, name+"-"+customerProduct.getSerial());
                i++;
                inputs.add(text);
            }
        } else {
            text = Translator.toLocale("products.unavailable",exception);
            inputs.add(text);

        }

        return inputs;
    }

    public static List<String> prefillPaymentListData(List<ProductPaymentPeriodModel> paymentPeriodModels){
        List<String> inputs = new ArrayList<>();
        String text = "";
        int i = 1;

        if (!paymentPeriodModels.isEmpty()){
            for (ProductPaymentPeriodModel paymentPeriodModel :paymentPeriodModels){

                String name = "";
                text = Translator.toLocale("prefill.data",i,paymentPeriodModel.getPaymentPeriod().getName());
                i++;
                inputs.add(text);
            }
        } else {
            text = Translator.toLocale("products.unavailable");
            inputs.add(text);

        }

        return inputs;
    }

    public static List<String> prefillData(ProductListResponseData productListResponseData,String exception){
        List<String> inputs = new ArrayList<>();
        String text = "";
        int i = 1;

        if (productListResponseData != null){
            Collections.reverse( productListResponseData.getData());
            for (ProductModel productModel : productListResponseData.getData()){

                text = Translator.  toLocale("prefill.data",i, productModel.getName());
                i++;
                inputs.add(text);
            }
        } else {
            text = Translator.toLocale("products.unavailable",exception);
            inputs.add(text);

        }

        return inputs;

    }

    public static List<String> prefillData(List<ProductExemptionModel> taxExemptionModels){
        List<String> inputs = new ArrayList<>();
        String text = "";
        int i = 1;
        if (!taxExemptionModels.isEmpty()){
            for (ProductExemptionModel taxExemptionModel: taxExemptionModels){
                text = Translator.toLocale("prefill.data",i,taxExemptionModel.getTaxExemption().getName());
                i++;
                inputs.add(text);
            }
        } else {
            text = Translator.toLocale("products.unavailable");
            inputs.add(text);

        }

        return inputs;
    }

    public static List<String> prefillData(String data,CustomerProduct customerProduct){
        List<String> inputs = new ArrayList<>();
        String text = "";
        if (data != null && customerProduct != null){
            text = Translator.toLocale("confirmation.renewal",data,customerProduct.getCustomer().getFirstName()+" "+customerProduct.getCustomer().getLastName());
            inputs.add(text);

        } else {
            text = Translator.toLocale("products.unavailable");
            inputs.add(text);
        }

        return inputs;
    }

    public static List<String> prefillBeneficiaryData(List<BeneficiaryModel> beneficiaryModels,String exception){
        List<String> inputs = new ArrayList<>();
        String text = "";
        int i = 1;
        if (!beneficiaryModels.isEmpty()){
            for (BeneficiaryModel beneficiaryModel : beneficiaryModels){
                text = Translator.toLocale("prefill.data",i, beneficiaryModel.getCustomer().getFirstName()+" "+beneficiaryModel.getCustomer().getLastName());
                i++;
                inputs.add(text);
            }
        } else {
            text = Translator.toLocale("products.unavailable",exception);
            inputs.add(text);

        }

        return inputs;

    }

    public static List<String> prefillUserData(UserModel userModel){
        List<String> inputs = new ArrayList<>();
        String text = "";
        if (userModel != null){
            text = Translator.toLocale("user.profile",userModel.getAccountOwner().getFirstName()+" "+userModel.getAccountOwner().getLastName(), userModel.getAccount().getUsername() );
            inputs.add(text);
        } else
            inputs.add(text);
        return inputs;

    }

    public static List<String> prefillBillPresentmentData( StatusResponseData statusResponseData,String Exception){
        List<String> inputs = new ArrayList<>();
        String text = "";
        String beneficiary = null;
        String appendIpaText = null;
        if (statusResponseData.getData() != null){
            String appendedText = null;
            if (statusResponseData.getData().getCharges().get(0).getCode().contains(Constants.MatolaProducts.IPAALT)){
                beneficiary = statusResponseData.getData().getCharges().stream().
                        filter(itemCodeValue -> itemCodeValue.getName().equalsIgnoreCase(statusResponseData.getData().getReport().getCustomerName()))
                        .map(ItemCodeValue::getName).findFirst().orElse(null);
                if (statusResponseData.getData().getCharges().size() == 1){
                  appendedText = beneficiary;
                } else if(beneficiary != null){
                    appendedText = "Self and Beneficiaries";
                } else {
                    appendedText = "My Beneficiaries";
                }
            } else if (statusResponseData.getData().getCharges().get(0).getCode().contains(Constants.SerialProducts.MARKETSERIAL)) {
                appendedText =Constants.MatolaProducts.MARKETS;
            }else  {
                appendedText =Constants.MatolaProducts.TAE;
            }
            text = Translator.toLocale("bill.presentment",
                    statusResponseData.getData().getTotalAmount(),appendedText );
            inputs.add(text);
        } else{
            text= statusResponseData.getStatus().getMessage();
            inputs.add(text);
        }



        return inputs;

    }

    public static List<String> getStringInputs(List<SessionLog> sessionLogs){
        String input = "";
        List<String> inputs = new ArrayList<>();
        for (SessionLog sessionLog : sessionLogs){
            boolean isRemoved = false;
            if (sessionLog.getUserInput().length() == 1 && !sessionLog.getUserInput().equals("0")){
                input = StringUtils.substringBetween(sessionLog.getScreenText(),sessionLog.getUserInput()+":","\n");
            } else if (sessionLog.getUserInput().equals("0")){
                isRemoved = true;
                inputs.remove(inputs.size()-1);
            }
//            else if ( sessionLog.getUserInput().equals("99")){
//            }
            else {
                input = sessionLog.getUserInput();
            }

            if (!isRemoved)
                inputs.add(input);


        }
        log.info("INputs_BEFORE_CHANGE: {}",inputs);
        return inputs;

    }


}