package ke.co.skybill.revenuecollection.app.repository;

import ke.co.skybill.revenuecollection.app.entities.App;
import ke.co.skybill.revenuecollection.app.entities.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppDao extends JpaRepository<App,String> {
    boolean existsByAppName(String appName);
    boolean existsByAppKey(String appName);
    App findDistinctByAppKey(String a);
    Page<App> findAllByDateDeletedIsNull(Pageable pageable);


    List<App> findAllByDateDeletedIsNull();

    List<App> findAllByDateDeletedIsNullAndAppUserIsNull();
    List<App> findAllByDateDeletedIsNullAndAppUserIsNullAndAppKey(String key);

    Optional<App> findDistinctByAppName(String appName);
    Optional<App> findTopByCorrelatorIsNullAndStatus(Status status);
    Optional<App> findTopByCorrelatorIsNotNullAndBranchCorrelatorIsNull();
}
