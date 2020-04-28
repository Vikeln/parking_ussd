package mz.skybill.ussd.parking.repos;

import mz.skybill.ussd.parking.entities.Session;
import mz.skybill.ussd.parking.entities.SessionLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SessionLogRepository extends JpaRepository<SessionLog, Long> {
    List<SessionLog> findAllBySession(Session session);

    SessionLog findFirstBySessionOrderByIdDesc(Session session);

    List<SessionLog> findAllBySessionOrderByIdAsc(Session session);
}
