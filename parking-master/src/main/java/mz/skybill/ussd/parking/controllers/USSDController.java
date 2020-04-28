package mz.skybill.ussd.parking.controllers;

import mz.skybill.ussd.parking.entities.Session;
import mz.skybill.ussd.parking.entities.SessionLog;
import mz.skybill.ussd.parking.repos.SessionLogRepository;
import mz.skybill.ussd.parking.repos.SessionRepository;
import mz.skybill.ussd.parking.services.ManipulationService;
import mz.skybill.ussd.parking.services.MatolaService;
import mz.skybill.ussd.parking.services.USSDService;
import mz.skybill.ussd.parking.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;

@RestController
@RequestMapping(value = "ussd/v1", produces = MediaType.TEXT_PLAIN_VALUE)
public class USSDController {

    Logger log = LoggerFactory.getLogger(USSDController.class);


    @Autowired
    private USSDService ussdService;
    @Autowired
    private MatolaService matolaService;
    @Autowired
    private ManipulationService manipulationService;
    @Autowired
    private SessionLogRepository sessionLogRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private Config config;


    @RequestMapping("/test")
    public String index(HttpServletRequest request, HttpServletResponse response) {
        String serviceCode = request.getParameter("service_code") != null ? request.getParameter("service_code") : request.getParameter("code");
        String msisdn = request.getParameter("MSISDN") != null ? request.getParameter("MSISDN") : request.getParameter("msisdn");
        String sessionId = request.getParameter("session_id") != null ? request.getParameter("session_id") : request.getParameter("sessionId");
        String input = request.getParameter("service_code") != null ? request.getParameter("ussd_string") : request.getParameter("input");
        String newRequest = request.getParameter("newrequest");
        String operator;
        if (newRequest != null && !newRequest.isEmpty()) {
            operator = Operator.AIRTEL;
        } else {
            operator = Operator.SAFARICOM;
        }


        if (input == null) {
            return Translator.toLocale("missing.input");
        }


        return processRequest(response, msisdn, input, sessionId, operator, serviceCode);
    }

    private String processRequest(HttpServletResponse response, String msisdn, String input, String sessionId, String operator, String serviceCode) {
        Session session = new Session();
        Object object = null;
        boolean isRegistered = false;
        List<String> newInputs = new ArrayList<>();
        String text = null;
        boolean isNew;
        if (input.isEmpty()) {
            isNew = true;
        } else {
            newInputs.addAll(new ArrayList<>(asList(input.split("\\*"))));
            session = ussdService.findSession(msisdn, sessionId, operator);
            if (session == null) {
                isNew = true;
                session = new Session();

                if (newInputs.stream().findFirst().get().equals(serviceCode)) {
                    newInputs.remove(0);
                }
            } else {
                isNew = false;
            }
        }

        List<String> sessionInputs;
        if (isNew) {
            session.setDateCreated(new Date());
            session.setOperator(operator);
            session.setMsisdn(msisdn);
            session.setSessionId(sessionId);
            sessionInputs = newInputs;

            isRegistered = ussdService.findUserIsRegistered(session.getMsisdn());
            if (isRegistered) {
                session.setRegistered(true);
                String accesstoken = matolaService.getAccessToken(msisdn);
                if (accesstoken != null) {
                    log.info("AT: {}",accesstoken);
                    session.setAccessToken(accesstoken);
                } else {
                    session.setAccessToken(null);
                }
            }

        } else if (!newInputs.isEmpty()) {
            sessionInputs = new ArrayList<>(asList(session.getInputs().split("\\*")));
            if (sessionInputs.size() > 1 && USSDUtil.getInput(newInputs).equals("0")) {
                sessionInputs.remove(sessionInputs.size() - 1);
            } else if (!sessionInputs.isEmpty() && USSDUtil.getInput(newInputs).equals("00")) {
                sessionInputs = sessionInputs.subList(0, 1);
            } else if (!sessionInputs.isEmpty() && USSDUtil.getInput(newInputs).equals("99")) {
                text = USSDUtil.exit(session);
            } else if (sessionInputs.isEmpty()) {
                sessionInputs.addAll(newInputs);
            } else {
                sessionInputs.add(newInputs.get(newInputs.size() - 1));
            }
        } else {
            sessionInputs = new ArrayList<>();
        }

        if (sessionInputs.isEmpty()) {
            sessionInputs.add("0");
        }

        session.setSessionInputs(sessionInputs);
        session.setInputs(String.join("*", sessionInputs));

        if (text == null) {
            text = mainMenu(session, object);
        }

        String screenText;
        if (operator.equals(Operator.AIRTEL)) {
            if (text.startsWith("CON")) {
                response.addHeader("Freeflow", "FC");
            } else {
                response.addHeader("Freeflow", "FB");
            }
            text = text.substring(4);
            screenText = text;
        } else {
            screenText = text.substring(4);
        }

        // We remove (!) from the text displayed but keep if in the logs to help in noting sensitive information.
        if (screenText.startsWith("!")) {
            if (operator.equals(Operator.AIRTEL)) {
                text = screenText.substring(1);
            } else {
                text = text.substring(0, 4).concat(screenText.substring(1));
            }
        }

        if (!isNew) {
            SessionLog previousLog = ussdService.findLastLog(session);
            //Sensitive information. Cannot be shown in plain text.
            if (previousLog.getScreenText().startsWith("!")) {
                previousLog.setUserInput("****");
                previousLog.setScreenText(previousLog.getScreenText().substring(1));
            } else {
                previousLog.setUserInput(newInputs.get(newInputs.size() - 1));
            }


            ussdService.save(previousLog);



            if (text.startsWith("END") || previousLog.getScreenText().contains("Enter Last Name")) {
                List<SessionLog> sessionLogs = sessionLogRepository.findAllBySession(session);
                manipulationService.getUssdInputs(session,sessionLogs);

                object = manipulationService.getUssdInputs(session, sessionLogs);
                if (object != null)
                    text = EndDisplay.getEndTextDisplay(object, session, sessionLogs);

            }




        }

        SessionLog log = new SessionLog();
        log.setSession(session);
        log.setDateCreated(new Date());
        log.setScreenText(screenText);
        log.setUserInput("");
        ussdService.save(log);
        return text;
    }

    public String mainMenu(Session session, Object object) {
        String text = "CON";
        List<String> inputs = USSDUtil.skipFirst(session.getSessionInputs());
        if (session.isRegistered()) {
            switch (session.getSessionInputs().stream().findFirst().get()) {
                case "0":
                    Session dbSession = ussdService.findUserSessionWithDate(session.getMsisdn(), new Date());

                    if (dbSession != null) {
                        text = USSDUtil.getText(Translator.toLocale("session.failed"), State.END);
                    } else {
                        text = USSDMenu.OpeningMenu(session, inputs, object);
                    }
                    break;

                case "100":
                    break;
            }

        } else {
            text = USSDUtil.getText(Translator.toLocale("full.registration.required"), State.END);

        }

        return text;
    }

    interface Operator {
        String SAFARICOM = "safaricom";
        String AIRTEL = "airtel";
    }

    interface State {
        boolean CON = false;
        boolean END = true;
    }

}
