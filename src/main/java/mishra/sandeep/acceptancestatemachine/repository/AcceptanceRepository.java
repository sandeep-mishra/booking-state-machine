package mishra.sandeep.acceptancestatemachine.repository;

import mishra.sandeep.acceptancestatemachine.model.Acceptance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcceptanceRepository extends JpaRepository<Acceptance, String> {
}
