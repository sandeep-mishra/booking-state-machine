package mishra.sandeep.acceptancestatemachine.repository;

import mishra.sandeep.acceptancestatemachine.model.Acceptance;
import mishra.sandeep.acceptancestatemachine.model.AcceptanceStates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AcceptanceRepository extends JpaRepository<Acceptance, String> {

    List<Acceptance> findByState(AcceptanceStates state);
}
