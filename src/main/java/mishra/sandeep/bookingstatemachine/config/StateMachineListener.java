package mishra.sandeep.bookingstatemachine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.logging.Logger;

@Configuration
public class StateMachineListener extends StateMachineListenerAdapter {

    private static final Logger LOGGER = Logger.getLogger(StateMachineListener.class.getName());

    @Override
    public void stateChanged(State from, State to) {
        LOGGER.info(() -> String.format("Transitioned from %s to %s%n", from == null ? "none" : from.getId(), to.getId()));
    }
}
