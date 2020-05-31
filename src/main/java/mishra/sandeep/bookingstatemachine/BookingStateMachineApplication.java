package mishra.sandeep.bookingstatemachine;

import mishra.sandeep.bookingstatemachine.config.AcceptanceBookingStateMachineConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@EntityScan("mishra.sandeep.bookingstatemachine.booking")
public class BookingStateMachineApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingStateMachineApplication.class, args);
	}

}
