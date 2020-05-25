package mishra.sandeep.bookingstatemachine.config;

import mishra.sandeep.bookingstatemachine.booking.InstructionEvents;
import mishra.sandeep.bookingstatemachine.booking.InstructionStates;
import mishra.sandeep.bookingstatemachine.booking.Persist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler;

@Configuration
@EnableStateMachine
public class AcceptanceBookingStateMachineConfiguration extends StateMachineConfigurerAdapter<InstructionStates, InstructionEvents> {

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

    @Lazy
    @Configuration
    static class PersistHandlerConfig {

        @Autowired
        private StateMachine<String, String> stateMachine;

        @Bean
        public Persist persist() {
            return new Persist(persistStateMachineHandler());
        }

        @Bean
        public PersistStateMachineHandler persistStateMachineHandler() {
            return new PersistStateMachineHandler(stateMachine);
        }

    }
}