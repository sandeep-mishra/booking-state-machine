package mishra.sandeep.bookingstatemachine.booking;

import lombok.Data;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.statemachine.annotation.WithStateMachine;

import javax.persistence.Entity;

@Data
@WithStateMachine
@Entity
public class Instruction {
    String id;
    String type;
    InstructionStates state;
    String timestamp;

    public Instruction()
    {}
    public Instruction(String id, String type, InstructionStates state) {
        this.id = id;
        this.state = state;
        this.type = type;
    }
}
