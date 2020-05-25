package ke.co.skybill.revenuecollection.accounts.repositories;

import ke.co.skybill.revenuecollection.accounts.entities.ClientDepartment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientDepartmentDao extends JpaRepository<ClientDepartment, Integer> {

}
