package mz.skybill.ussd.parking.services;

import mz.skybill.ussd.parking.entities.Session;
import mz.skybill.ussd.parking.entities.SessionLog;
import mz.skybill.ussd.parking.models.VerificationResponse;
import mz.skybill.ussd.parking.repos.SessionLogRepository;
import mz.skybill.ussd.parking.repos.SessionRepository;
import mz.skybill.ussd.parking.utils.Config;
import mz.skybill.ussd.parking.utils.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
public class USSDService {
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private SessionLogRepository logRepository;
    @Autowired
    private NetworkService networkService;
    @Autowired
    private Config config;

    Logger log = LoggerFactory.getLogger(USSDService.class);

    public Session findSession(String msisdn, String sessionId, String operator) {
        return sessionRepository.findFirstByMsisdnAndSessionIdAndOperator(msisdn, sessionId, operator);
    }

    public Session findUserSessionWithDate(String msisdn, Date date) {
        /*Call to Accounts Service to check if the user is Registered. If not send the registration USSD Menu
         */
        return sessionRepository.findFirstByMsisdnAndExpiryDateIsGreaterThan(msisdn, date);
    }

    public Boolean findUserIsRegistered(String msisdn) {
        log.info("msisdn " + msisdn);
        if (msisdn.startsWith(" 258")) {
            msisdn = "+" + msisdn.substring(1);
        }
        log.info("msisdn after " + msisdn);
        ResponseEntity<String> responseEntity = networkService.sendData(config.getAccountService() + "/users/verify/" + msisdn, HttpMethod.GET, null, null);
        boolean isRegistered = false;
        if (responseEntity.getStatusCodeValue() == 200) {
            VerificationResponse verificationResponse = new Util<VerificationResponse>().fromJson(responseEntity.getBody(), VerificationResponse.class);
            if (verificationResponse.getStatus().getCode() == 0) {
                isRegistered = true;
            }
        }
        return isRegistered;
    }

    public SessionLog findLastLog(Session session) {
        log.info("Last_Log: {}", Util.toJson(logRepository.findFirstBySessionOrderByIdDesc(session)));
        return logRepository.findFirstBySessionOrderByIdDesc(session);
    }

    @Transactional
    public SessionLog save(SessionLog log) {
        Session session = sessionRepository.save(log.getSession());
        log.setSession(session);
        return logRepository.save(log);
    }

    @Transactional
    public Session save(Session session) {
        return sessionRepository.save(session);
    }
}
