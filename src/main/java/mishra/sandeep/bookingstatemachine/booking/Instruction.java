package mishra.sandeep.bookingstatemachine.booking;

import lombok.Data;
import org.springframework.statemachine.annotation.WithStateMachine;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "Instruction")
@WithStateMachine
public class Instruction {
    @Id
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
