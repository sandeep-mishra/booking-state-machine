package mishra.sandeep.bookingstatemachine.repository;

import mishra.sandeep.bookingstatemachine.booking.Instruction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructionRepository extends JpaRepository<Instruction, Long> {
}
