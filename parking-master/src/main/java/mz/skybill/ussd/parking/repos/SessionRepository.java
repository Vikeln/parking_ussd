package mz.skybill.ussd.parking.repos;

import mz.skybill.ussd.parking.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface SessionRepository extends JpaRepository<Session, Long> {
    Session findFirstByMsisdnAndSessionIdAndOperator(String msisdn, String sessionId, String operator);

    Session findFirstByMsisdnAndExpiryDateIsGreaterThan(String msisdn, Date date);
}
