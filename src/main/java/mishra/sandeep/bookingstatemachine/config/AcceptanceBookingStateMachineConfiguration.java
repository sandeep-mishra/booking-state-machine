package mishra.sandeep.bookingstatemachine.config;

import mishra.sandeep.bookingstatemachine.booking.InstructionEvents;
import mishra.sandeep.bookingstatemachine.booking.InstructionStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineModelConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.model.StateMachineModelFactory;
import org.springframework.statemachine.data.*;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler;

@Configuration
@EnableStateMachine
@EnableAutoConfiguration
public class AcceptanceBookingStateMachineConfiguration extends StateMachineConfigurerAdapter<InstructionStates, InstructionEvents> {

    @Autowired
    private StateRepository<? extends RepositoryState> stateRepository;

    @Autowired
    private TransitionRepository<? extends RepositoryTransition> transitionRepository;

//    @Override
//    public void configure(StateMachineModelConfigurer<InstructionStates, InstructionEvents> model) throws Exception {
//        model
//                .withModel()
//                .factory(modelFactory());
//    }

//    @Bean
//    public StateMachineModelFactory<InstructionStates, InstructionEvents> modelFactory() {
//        return (StateMachineModelFactory<InstructionStates, InstructionEvents>) new RepositoryStateMachineModelFactory(stateRepository, transitionRepository).build();
//    }
    @Override
    public void configure(StateMachineConfigurationConfigurer<InstructionStates, InstructionEvents> config)
            throws Exception {
            config
                .withConfiguration()
                .autoStartup(true)
                .listener(new StateMachineListener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<InstructionStates, InstructionEvents> states) throws Exception {
        states
                .withStates()
                .initial(InstructionStates.PENDING)
                .state(InstructionStates.ACCOUNTING_IN_PROGRESS)
                .state(InstructionStates.VALIDATED)
                .end(InstructionStates.APPROVED)
                .end(InstructionStates.REJECTED)
                .end(InstructionStates.CANCELLED);

    }

    @Override
    public void configure(StateMachineTransitionConfigurer<InstructionStates, InstructionEvents> transitions) throws Exception {
        transitions.withExternal()
                .source(InstructionStates.PENDING).target((InstructionStates.ACCOUNTING_IN_PROGRESS)).event(InstructionEvents.VALIDATE)
                .and().withExternal()
                .source(InstructionStates.ACCOUNTING_IN_PROGRESS).target(InstructionStates.APPROVED).event(InstructionEvents.APPROVE)
                .and().withExternal()
                .source(InstructionStates.ACCOUNTING_IN_PROGRESS).target(InstructionStates.CANCELLED).event(InstructionEvents.CANCEL)
                .and().withExternal()
                .source(InstructionStates.ACCOUNTING_IN_PROGRESS).target(InstructionStates.REJECTED).event(InstructionEvents.REJECT).and().withInternal()
                .source(InstructionStates.ACCOUNTING_IN_PROGRESS)
                .timerOnce(10000)
                .state(InstructionStates.CANCELLED)
                ;
    }

}