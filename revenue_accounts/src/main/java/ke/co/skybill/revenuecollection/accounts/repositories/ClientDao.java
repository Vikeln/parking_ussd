package ke.co.skybill.revenuecollection.accounts.repositories;

import ke.co.skybill.revenuecollection.accounts.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientDao extends JpaRepository<Client, Integer> {

}
