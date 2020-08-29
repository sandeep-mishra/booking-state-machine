package mishra.sandeep.acceptancestatemachine.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;


@Configuration
@Scope(scopeName = "prototype")
public class StateMachineListener extends StateMachineListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(StateMachineListener.class);
    private static String id;

    public StateMachineListener() {
    }

    public static StateMachineListener createStateMachineListener() {
        return new StateMachineListener();
    }

    @Override
    public void stateChanged(State from, State to) {
        logger.info("Transitioned from {} to {}", from == null ? "none" : from.getId(), to.getId());
    }
}
