package mz.skybill.ussd.parking.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import mz.skybill.ussd.parking.entities.Session;
import mz.skybill.ussd.parking.models.*;
import mz.skybill.ussd.parking.repos.SessionLogRepository;
import mz.skybill.ussd.parking.repos.SessionRepository;
import mz.skybill.ussd.parking.services.CustomerService;
import mz.skybill.ussd.parking.services.MatolaService;
import mz.skybill.ussd.parking.services.USSDService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class USSDMenu<T> {

    private static SessionRepository sessionRepository;
    private static MatolaService matolaService;
    private static CustomerService customerService;
    private static SessionLogRepository sessionLogRepository;
    private static ObjectMapper objectMapper;
    private static USSDService ussdService;


    public USSDMenu(MatolaService matolaService, USSDService ussdService, CustomerService customerService, SessionLogRepository sessionLogRepository, ObjectMapper objectMapper, SessionRepository sessionRepository) {
        this.customerService = customerService;
        this.sessionLogRepository = sessionLogRepository;
        this.objectMapper = objectMapper;
        this.ussdService = ussdService;
        this.matolaService = matolaService;
        this.sessionRepository = sessionRepository;
    }

    public static Logger log = LoggerFactory.getLogger(USSDMenu.class);

    public static Object returnObject(Object object) {
        return object;
    }


    public static String OpeningMenu(Session session, List<String> inputs, Object object) {
        String text = "";
        if (inputs.isEmpty()) {
            if (session.isRegistered())
                text = USSDUtil.getText(Translator.toLocale("welcome.user"), State.CON);
            else
                text = USSDUtil.getText(Translator.toLocale("welcome.newuser"), State.CON);

        } else if (inputs.stream().findFirst().get().equalsIgnoreCase("1")) {
            if (session.isRegistered())
                text = ChoiceVehicles(session, USSDUtil.skipFirst(inputs));
            else
                text = getNumberPlate(session, USSDUtil.skipFirst(inputs));

        } else if (inputs.stream().findFirst().get().equalsIgnoreCase("2")) {

            text = getNumberPlate(session, USSDUtil.skipFirst(inputs));

        } else if (inputs.stream().findFirst().get().equalsIgnoreCase("3")) {
            if (session.isRegistered())
                text = chargeOptions(session, USSDUtil.skipFirst(inputs));
            else
                text = getNumberPlate(session, USSDUtil.skipFirst(inputs));
        } else if (inputs.stream().findFirst().get().equalsIgnoreCase("4")) {

            text = MyVehicles(session, USSDUtil.skipFirst(inputs));
        } else {
            text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
        }
        return text;
    }

    //Main Menu
    public static String getNumberPlate(Session session, List<String> inputs) {
        String text;
        if (inputs.size() == 0) {
            text = USSDUtil.getText(Translator.toLocale("user.number.plate"), State.CON);
        } else {
            List<String> data = USSDUtil.getStringInputs(sessionLogRepository.findAllBySession(session));
            if (data.get(0).equalsIgnoreCase(Constants.Parking.DAILY))
                text = DailyParkingOptions(session, USSDUtil.skipFirst(inputs));
            else if (data.get(0).equalsIgnoreCase(Constants.Parking.SEASONAL))
                text = ParkingTypes(session, USSDUtil.skipFirst(inputs));
            else if (data.get(0).equalsIgnoreCase(Constants.Parking.Vehicles))
                text = registerNewVehicle(session, USSDUtil.skipFirst(inputs));
            else {
                text = Clamping(session, USSDUtil.skipFirst(inputs));
            }
        }

        return text;
    }


    public static String switchops(Session session, List<String> inputs) {
        String text = null;
        boolean isText = false;
        switch (inputs.size()) {
            case 0:
                text = USSDUtil.getText(Translator.toLocale("daily.options"), State.CON);
                break;
            default:
                switch (inputs.stream().findFirst().get()) {
                    case "1":
                        break;
                    case "2":
                        break;
                    default:
                        isText = true;
                        text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
                        break;
                }

                List<String> sessionInputs = new ArrayList<>(Arrays.asList(session.getInputs().split("\\*")));

                if (Integer.parseInt(sessionInputs.get(sessionInputs.size() - 1)) == 1)
                    text = ZoneOptions(session, USSDUtil.skipFirst(inputs));
                else
                    text = HourlyOptions(session, USSDUtil.skipFirst(inputs));
        }

        return text;
    }


    public static String MyVehicles(Session session, List<String> inputs) {
        String text = null;
        boolean isText = false;
        switch (inputs.size()) {
            case 0:
                ProductCustomerResponseData data = matolaService.getCustomerProduct(140, session);
                if (data.getData().isEmpty())
                    text = USSDUtil.getText(Translator.toLocale("no.vehicles"), State.CON);
                else {
                    List<String> strings = USSDUtil.prefillVehicles(data.getData());
                    text = Translator.toLocale("list.vehicles");
                    for (String s : strings) {
                        text += s;

                        if (strings.get(strings.size() - 1) == s)
                            text = text.trim() + Translator.toLocale("menu.main.back.exit");
                    }
                }
                break;
            default:
                List<String> sessionInputs = new ArrayList<>(Arrays.asList(session.getInputs().split("\\*")));
                text = getNumberPlate(session, USSDUtil.skipFirst(inputs));
        }

        return text;
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String shareVehicles(Session session, List<String> inputs) {

        String text = null;
        List<String> sessionInputs = new ArrayList<>(Arrays.asList(session.getInputs().split("\\*")));
        log.info("sessionInputs {}", Util.toJson(sessionInputs));
        log.info("sessionInputs {}", Util.toJson(sessionInputs.get(sessionInputs.size() - 1)));
        return text;
    }

    public static String ChoiceVehicles(Session session, List<String> inputs) {
        String text = null;
        boolean isText = false;
        switch (inputs.size()) {
            case 0:
                ProductCustomerResponseData data = matolaService.getCustomerProduct(140, session);
                if (data.getData().isEmpty())
                    text = USSDUtil.getText(Translator.toLocale("no.vehicles"), State.CON);
                else {
                    List<String> strings = USSDUtil.prefillVehicles(data.getData());
                    text = Translator.toLocale("choice.vehicles");
                    for (String s : strings) {
                        text += s;
//                        if (strings.get(strings.size() - 1) != s)
//                            text += "\n";

                        if (strings.get(strings.size() - 1) == s)
                            text = text.trim() + Translator.toLocale("menu.main.back.exit");
                    }
                }
                break;
            default:
                log.info("got here");
                List<String> sessionInputs = new ArrayList<>(Arrays.asList(session.getInputs().split("\\*")));
//                if (Integer.parseInt(sessionInputs.get(sessionInputs.size() - 1)) == 1)
//                    text = getNumberPlate(session, USSDUtil.skipFirst(inputs));
//                else
                if (sessionInputs.size() == 3) {
                    ProductCustomerResponseData data1 = matolaService.getCustomerProduct(140, session);
                    String in = "";
                    int i = 0;
                    log.info(Util.toJson(sessionInputs));
                    for (String s : sessionInputs) {
                        if (sessionInputs.get(sessionInputs.size() - 1) != s) {
                            in += s + "*";
                        } else {
                            in += data1.getData().get(Integer.parseInt(sessionInputs.get(sessionInputs.size() - 1)) - 1).getSerial() + "*";
                        }
                        ++i;
                    }
                    session.setInputs(in);
                    log.info("session.setInputs(in) " + in);
                    session = sessionRepository.save(session);

                    text = DailyParkingOptions(session, USSDUtil.skipFirst(inputs));
                } else
                    text = getNumberPlate(session, USSDUtil.skipFirst(inputs));
//                else {
//
//                }

        }

        return text;
    }

    public static String ZoneOptions(Session session, List<String> inputs) {
        String text = null;
        boolean isText = false;
        switch (inputs.size()) {
            case 0:
                text = USSDUtil.getText(Translator.toLocale("zone.options"), State.CON);
                break;
            default:
                switch (inputs.stream().findFirst().get()) {
                    case "1":
                        break;
                    case "2":
                        break;
                    default:
                        isText = true;
                        text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
                        break;
                }
//
//                List<String> data = USSDUtil.getStringInputs(sessionLogRepository.findAllBySession(session));
////                log.info("data kwa after choosing zones {}", Util.toJson(data));

                if (!isText)
                    text = paymentConfirmation(session, USSDUtil.skipFirst(inputs));

        }

        return text;
    }

    public static String chargeOptions(Session session, List<String> inputs) {
        String text = null;
        boolean isText = false;
        switch (inputs.size()) {
            case 0:
                text = USSDUtil.getText(Translator.toLocale("charge.options"), State.CON);
                break;
            default:
                switch (inputs.stream().findFirst().get()) {
                    case "1":
                        break;
                    case "2":
                        break;
                    default:
                        isText = true;
                        text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
                        break;
                }

                List<String> sessionInputs = new ArrayList<>(Arrays.asList(session.getInputs().split("\\*")));

                log.info("sessionInputs {}", Util.toJson(sessionInputs));

                if (!isText) {
                    if (isnumeric(sessionInputs.get(sessionInputs.size() - 1)) && Integer.parseInt(sessionInputs.get(sessionInputs.size() - 1)) == 1)
//                    if (Integer.parseInt(sessionInputs.get(sessionInputs.size() - 1)) == 1)
                        text = Clamping(session, USSDUtil.skipFirst(inputs));
                    else
                        text = getNumberPlate(session, USSDUtil.skipFirst(inputs));
                }


        }

        return text;
    }

    public static boolean isnumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }

    public static String registerNewVehicle(Session session, List<String> inputs) {
        String text = null;
        boolean isText = false;
        List<String> data = USSDUtil.getStringInputs(sessionLogRepository.findAllBySession(session));
        log.info("Data {}", Util.toJson(data));
        switch (inputs.size()) {
            case 0:
                List<String> sessionInputs = new ArrayList<>(Arrays.asList(session.getInputs().split("\\*")));
                CustomerProductParkingRequest model = CustomerProductParkingRequest.transform(matolaService.getUser(session).getAccountOwner().getId(), 140, sessionInputs.get(sessionInputs.size() - 1));
                log.info("CustomerProductParkingRequest {}", Util.toJson(model));
                CustomerProductResponse response = matolaService.registerVehicleParking(model, session);
                if (response.getStatus().getCode() == 0)
                    text = USSDUtil.getText(Translator.toLocale("vehicle.register.sucess", response.getData().getSerial()), State.END);
                else
                    text = USSDUtil.getText(Translator.toLocale("vehicle.register.fail", model.getVehicleRegistration(), response.getStatus().getMessage()), State.END);
                break;
            default:
                switch (inputs.stream().findFirst().get()) {
                    default:
                        isText = true;
                        text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
                        break;
                }
        }

        return text;
    }

    public static String SeasonParkingOptions(Session session, List<String> inputs) {
        String text = null;
        boolean isText = false;
        switch (inputs.size()) {
            case 0:
                text = USSDUtil.getText(Translator.toLocale("season.options"), State.CON);
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
                        break;
                }
                if (!isText)
                    text = ZoneOptions(session, USSDUtil.skipFirst(inputs));
        }

        return text;


    }

    public static String DailyParkingOptions(Session session, List<String> inputs) {
        String text = null;
        boolean isText = false;
        switch (inputs.size()) {
            case 0:
                text = USSDUtil.getText(Translator.toLocale("daily.options"), State.CON);
                break;
            default:
                switch (inputs.stream().findFirst().get()) {
                    case "1":
                        break;
                    case "2":
                        break;
                    default:
                        isText = true;
                        text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
                        break;
                }

                List<String> sessionInputs = new ArrayList<>(Arrays.asList(session.getInputs().split("\\*")));

                if (Integer.parseInt(sessionInputs.get(sessionInputs.size() - 1)) == 1)
                    text = ZoneOptions(session, USSDUtil.skipFirst(inputs));
                else
                    text = HourlyOptions(session, USSDUtil.skipFirst(inputs));
        }

        return text;
    }


    public static String HourlyOptions(Session session, List<String> inputs) {
        String text = null;
        boolean isText = false;
        switch (inputs.size()) {
            case 0:
                text = USSDUtil.getText(Translator.toLocale("hourly.options"), State.CON);
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
                        break;
                }


                if (!isText)
                    text = ZoneOptions(session, USSDUtil.skipFirst(inputs));
        }

        return text;
    }
//
//    public static String FullDayOptions(Session session, List<String> inputs){
//        String text = null;
//        boolean isText = false;
//        switch (inputs.size()) {
//            case 0:
//                text = USSDUtil.getText(Translator.toLocale("hourly.options"), State.CON);
//                break;
//            default:
//                switch (inputs.stream().findFirst().get()) {
//                    case "1":
//                        break;
//                    case "2":
//                        break;
//                    case "3":
//                        break;
//                    case "4":
//                        break;
//                    case "5":
//                        break;
//                    case "6":
//                        break;
//                    case "7":
//                        break;
//                    case "8":
//                        break;
//                    default:
//                        isText = true;
//                        text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
//                        break;
//                }
//
//
//                if (!isText)
//                    text = ZoneOptions(session, USSDUtil.skipFirst(inputs));
//        }
//
//        return text;
//    }

    public static String ParkingTypes(Session session, List<String> inputs) {
        String text = null;
        boolean isText = false;
        switch (inputs.size()) {
            case 0:
                text = USSDUtil.getText(Translator.toLocale("parking.options"), State.CON);
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
                        break;
                }
                if (!isText)
                    text = SeasonParkingOptions(session, USSDUtil.skipFirst(inputs));
        }

        return text;


    }


    public static String Clamping(Session session, List<String> inputs) {
        String text = null;
        boolean isText = false;
        switch (inputs.size()) {
            case 0:
                List<String> sessionInputs = new ArrayList<>(Arrays.asList(session.getInputs().split("\\*")));

                List<CustomerProductChargeEntryModel> charges;
                if (sessionInputs.size() > 2 && Integer.parseInt(sessionInputs.get(2)) != 1)
                    charges = matolaService.getCharges(sessionInputs.get(3), null, session);
                else
                    charges = matolaService.getCharges(null, matolaService.getUser(session).getAccountOwner().getId(), session);

//                if (session.isRegistered())
//                else
                List<String> strings = USSDUtil.prefillCharges(charges);
                if (strings.isEmpty() && sessionInputs.size() > 2 && Integer.parseInt(sessionInputs.get(2)) != 1)
                    text = USSDUtil.getText(Translator.toLocale("charges.unavailable.serial", sessionInputs.get(3)), State.END);
//                    text = USSDUtil.getText(Translator.toLocale("charges.unavailable.serial", sessionInputs.get(3)), State.END);
                else if (strings.isEmpty())
                    text = USSDUtil.getText(Translator.toLocale("charges.unavailable"), State.END);
                else {
                    text = Translator.toLocale("list.charges");

                    for (String s : strings) {
                        text += s;
                        if (strings.get(strings.size() - 1) != s)
                            text += "\n";

                        if (strings.get(strings.size() - 1) == s)
                            text = text.trim() + Translator.toLocale("menu.main.back.exit");
                    }
                }
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
                        break;
                }
                if (!isText)
                    text = SeasonParkingOptions(session, USSDUtil.skipFirst(inputs));
        }

        return text;


    }

    public static String paymentConfirmation(Session session, List<String> inputs) {
        String text;
        List<EnforceVehicleRequest> requests = new ArrayList<>();
        List<String> data = USSDUtil.getStringInputs(sessionLogRepository.findAllBySession(session));
        switch (inputs.size()) {
            case 0:
                ReceiptResponse model;
                if (data.get(0).equalsIgnoreCase(Constants.Parking.DAILY)) {
                    requests.add(EnforceVehicleRequest.transform(
                            data.get(1),
                            data.get(2).equalsIgnoreCase(Constants.DailyOptions.Full) ? (data.get(3).equalsIgnoreCase("Zone A(Amarela)") ? 1 : 2) : (data.get(4).equalsIgnoreCase("Zone A(Amarela)") ? 1 : 2),
                            data.get(2).equalsIgnoreCase(Constants.DailyOptions.Full) ? null : data.get(3))
                    );
                    model = matolaService.receiptParking(EnforceParkingRequest.transform(null, "", 140, data.get(2).equalsIgnoreCase(Constants.DailyOptions.Full) ? 8 : 10, 1, requests), session);
                } else {
                    requests.add(EnforceVehicleRequest.transform(
                            data.get(1),
                            (data.get(4).equalsIgnoreCase("Zone A(Amarela)") ? 1 : 2),
                            null)
                    );
                    model = matolaService.receiptParking(EnforceParkingRequest.transform(null, "", 140, data.get(3).equalsIgnoreCase(Constants.Season.MONTHLY) ? 20 : (data.get(3).equalsIgnoreCase(Constants.Season.Annual) ? 21 : 19), data.get(2).equalsIgnoreCase(Constants.ParkingType.Residential) ? 2 : (data.get(2).equalsIgnoreCase(Constants.ParkingType.Commercial) ? 3 : 4), requests), session);
                }
                log.info("model from receipt {}", Util.toJson(model));
//                String newText = Util.prependText("", USSDUtil.prefillData(data), "");
                text = Translator.toLocale("confirm.prefiill", data.get(1), model.getData().getTotalAmount(), model.getData().getTotalAmount());
                text = USSDUtil.getText(text, State.CON);
                break;
            default:
                log.info("inputs after pay confirm    " + inputs.stream().findFirst().get());
                List<String> sessionInputs = new ArrayList<>(Arrays.asList(session.getInputs().split("\\*")));

                log.info("switching after pay confirm    " + Integer.parseInt(sessionInputs.get(sessionInputs.size() - 1)));

                switch (Integer.parseInt(sessionInputs.get(sessionInputs.size() - 1))) {
                    case 1:
                        if (data.get(0).equalsIgnoreCase(Constants.Parking.DAILY)) {
                            requests.add(EnforceVehicleRequest.transform(
                                    data.get(1),
                                    data.get(2).equalsIgnoreCase(Constants.DailyOptions.Full) ? (data.get(3).equalsIgnoreCase("Zone A(Amarela)") ? 1 : 2) : (data.get(4).equalsIgnoreCase("Zone A(Amarela)") ? 1 : 2),
                                    data.get(2).equalsIgnoreCase(Constants.DailyOptions.Full) ? null : data.get(3))
                            );
                            model = matolaService.enforceParking(EnforceParkingRequest.transform(null, "", 140, data.get(2).equalsIgnoreCase(Constants.DailyOptions.Full) ? 8 : 10, 1, requests), session);
                        } else {
                            requests.add(EnforceVehicleRequest.transform(
                                    data.get(1),
                                    (data.get(4).equalsIgnoreCase("Zone A(Amarela)") ? 1 : 2),
                                    null)
                            );
                            model = matolaService.enforceParking(EnforceParkingRequest.transform(null, "", 140, data.get(3).equalsIgnoreCase(Constants.Season.MONTHLY) ? 20 : (data.get(3).equalsIgnoreCase(Constants.Season.Annual) ? 21 : 19), data.get(2).equalsIgnoreCase(Constants.ParkingType.Residential) ? 2 : (data.get(2).equalsIgnoreCase(Constants.ParkingType.Commercial) ? 3 : 4), requests), session);
                        }
                        String message = "";
                        if (model.getData().getReport().getResponse().getTxtMessage() != null)
                            message = model.getData().getReport().getResponse().getTxtMessage();
                        else if (model.getData().getReport().getResponse().getInvoiceNo() != null)
                            message += "Please pay MZN " + model.getData().getTotalAmount() + " using reference invoiceNo : " + model.getData().getReport().getResponse().getInvoiceNo();
                        else
                            message = model.getStatus().getMessage();
                        text = USSDUtil.getText(Translator.toLocale("payment.sucess", message), State.END);
                        break;
                    default:
                        text = USSDUtil.getText(Translator.toLocale("invalid.option"), State.CON);
                }
        }

        return text;
    }


    interface State {
        boolean CON = false;
        boolean END = true;
    }
}
