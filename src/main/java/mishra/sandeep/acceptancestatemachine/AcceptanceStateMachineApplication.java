package mishra.sandeep.acceptancestatemachine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("mishra.sandeep.acceptancestatemachine.model")
public class AcceptanceStateMachineApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcceptanceStateMachineApplication.class, args);
	}

}
