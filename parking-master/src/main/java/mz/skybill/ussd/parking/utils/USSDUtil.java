package mz.skybill.ussd.parking.utils;

import mz.skybill.ussd.parking.entities.Session;
import mz.skybill.ussd.parking.entities.SessionLog;
import mz.skybill.ussd.parking.models.*;
import mz.skybill.ussd.parking.services.USSDService;
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

    public static String append(String text, String addedText) {
        String newText = text.substring(4);
        if (newText.startsWith("!")) {
            newText = newText.substring(1);
        }
        return text.substring(0, 4).concat(addedText + ". " + newText);
    }

    public static void goBack(Session session, List<String> inputs) {
        List<String> sessionInputs = new ArrayList<>(Arrays.asList(session.getInputs().split("\\*")));
        if (sessionInputs.size() > 1) {
            sessionInputs.remove(sessionInputs.size() - 1);
            inputs.remove(inputs.size() - 1);
            session.setInputs(String.join("*", sessionInputs));
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
        return getText(Translator.toLocale("thank.you"), true);
    }

    public static String logout(Session session) {
        return getText(Translator.toLocale("thank.you.logout"), true);
    }


    public static List<String> prefillCharges(List<CustomerProductChargeEntryModel> models) {
        List<String> inputs = new ArrayList<>();
        String text = "";
        int i = 1;

        if (models != null) {

            for (CustomerProductChargeEntryModel productModel : models) {

                text = Translator.toLocale("prefill.data", i, productModel.getCustomerProduct().getSerial() + " : MZN " + Double.valueOf(productModel.getValue()));
                i++;
                inputs.add(text);
            }
        } else {
            text = Translator.toLocale("charges.unavailable");
            inputs.add(text);

        }

        return inputs;

    }

    public static List<String> prefillVehicles(List<CustomerProduct> models) {
        List<String> inputs = new ArrayList<>();
        String text = "";
        int i = 1;
        if (models != null) {
            for (CustomerProduct productModel : models) {
                text = Translator.toLocale("prefill.data", i, productModel.getSerial());
                i++;
                inputs.add(text);
            }
        }
        return inputs;
    }

    public static List<String> prefillData(List<String> data) {
        List<String> inputs = new ArrayList<>();
        String amount = "0";
        String text = "";
        if (!data.isEmpty()) {
            if (data.get(0).equalsIgnoreCase(Constants.Parking.DAILY)) {

                amount = "200";
            } else
                switch (data.get(2)) {
                    case Constants.Seasons.DAILY:
                        amount = "200";
                        break;
                    case Constants.Seasons.MONTHLY:
                        amount = "5.000";
                        break;
                    case Constants.Seasons.SEMIANUALLY:
                        amount = "29.500";
                        break;
                    case Constants.Seasons.ANNUALLY:
                        amount = "60.000";
                        break;
                    default:
                        amount = "200";
                        break;
                }
            text = Translator.toLocale("confirm.prefiill", data.get(1), amount, amount);
            inputs.add(text);

        } else {
            text = Translator.toLocale("products.unavailable");
            inputs.add(text);

        }

        return inputs;
    }

    public static List<String> prefillData(String data, CustomerProduct customerProduct) {
        List<String> inputs = new ArrayList<>();
        String text = "";
        if (data != null && customerProduct != null) {
            text = Translator.toLocale("confirmation.renewal", data, customerProduct.getCustomer().getFirstName() + " " + customerProduct.getCustomer().getLastName());
            inputs.add(text);

        } else {
            text = Translator.toLocale("products.unavailable");
            inputs.add(text);
        }

        return inputs;
    }

    public static List<String> prefillBeneficiaryData(List<BeneficiaryModel> beneficiaryModels, String exception) {
        List<String> inputs = new ArrayList<>();
        String text = "";
        int i = 1;
        if (!beneficiaryModels.isEmpty()) {
            for (BeneficiaryModel beneficiaryModel : beneficiaryModels) {
                text = Translator.toLocale("prefill.data", i, beneficiaryModel.getCustomer().getFirstName() + " " + beneficiaryModel.getCustomer().getLastName());
                i++;
                inputs.add(text);
            }
        } else {
            text = Translator.toLocale("products.unavailable", exception);
            inputs.add(text);

        }

        return inputs;

    }

    public static List<String> prefillUserData(UserModel userModel) {
        List<String> inputs = new ArrayList<>();
        String text = "";
        if (userModel != null) {
            text = Translator.toLocale("user.profile", userModel.getAccountOwner().getFirstName() + " " + userModel.getAccountOwner().getLastName(), userModel.getAccount().getUsername());
            inputs.add(text);
        } else
            inputs.add(text);
        return inputs;

    }


    public static List<String> getStringInputs(List<SessionLog> sessionLogs) {
        String input = "";
        List<String> inputs = new ArrayList<>();
        for (SessionLog sessionLog : sessionLogs) {
            boolean isRemoved = false;
            if (sessionLog.getUserInput().length() == 1 && !sessionLog.getUserInput().equals("0")) {
                input = StringUtils.substringBetween(sessionLog.getScreenText(), sessionLog.getUserInput() + ":", "\n");
            } else if (sessionLog.getUserInput().equals("0")) {
                isRemoved = true;
                inputs.remove(inputs.size() - 1);
            }
//            else if ( sessionLog.getUserInput().equals("99")){
//            }
            else {
                input = sessionLog.getUserInput();
            }

            if (!isRemoved)
                inputs.add(input);


        }
        log.info("INputs_BEFORE_CHANGE: {}", inputs);
        return inputs;

    }


}