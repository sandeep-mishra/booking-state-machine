package mishra.sandeep.acceptancestatemachine.config;

import mishra.sandeep.acceptancestatemachine.model.Acceptance;
import mishra.sandeep.acceptancestatemachine.model.AcceptanceStates;
import mishra.sandeep.acceptancestatemachine.repository.AcceptanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;

@Configuration
@EnableAutoConfiguration
public class AcceptanceStateMachineInterceptor extends StateMachineInterceptorAdapter {
    private static final Logger logger = LoggerFactory.getLogger(AcceptanceStateMachineInterceptor.class);

    @Autowired
    AcceptanceRepository acceptanceRepository;

    @Override
    public Message preEvent(Message message, StateMachine stateMachine) {

        logger.info("Received event {} for id {}", message.getPayload(), stateMachine.getId());

        return super.preEvent(message, stateMachine);
    }

    @Override
    public void postStateChange(State state, Message message, Transition transition, StateMachine stateMachine) {
        super.postStateChange(state, message, transition, stateMachine);
        logger.info("Transition for id {} from {} to {}", stateMachine.getId(), transition.getSource().getId(), transition.getTarget().getId());

    }

    @Override
    public StateContext preTransition(StateContext stateContext) {
        return super.preTransition(stateContext);

    }

    @Override
    public StateContext postTransition(StateContext stateContext) {
        logger.info("Transition for id {} from {} to {}", stateContext.getStateMachine().getId(), stateContext.getSource().getId(), stateContext.getTarget().getId());
        Acceptance acceptance = acceptanceRepository.getOne(stateContext.getStateMachine().getId());
        acceptance.setState((AcceptanceStates) stateContext.getTarget().getId());
        acceptanceRepository.save(acceptance);

        return super.postTransition(stateContext);
    }

    @Override
    public Exception stateMachineError(StateMachine stateMachine, Exception exception) {
        return super.stateMachineError(stateMachine, exception);
    }
}
