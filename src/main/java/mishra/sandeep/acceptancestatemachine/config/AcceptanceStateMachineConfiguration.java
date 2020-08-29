package mishra.sandeep.acceptancestatemachine.config;

import mishra.sandeep.acceptancestatemachine.model.AcceptanceEvents;
import mishra.sandeep.acceptancestatemachine.model.AcceptanceStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.data.RepositoryState;
import org.springframework.statemachine.data.RepositoryTransition;
import org.springframework.statemachine.data.StateRepository;
import org.springframework.statemachine.data.TransitionRepository;
import org.springframework.statemachine.data.jpa.JpaPersistingStateMachineInterceptor;
import org.springframework.statemachine.data.jpa.JpaStateMachineRepository;
import org.springframework.statemachine.persist.StateMachineRuntimePersister;
import org.springframework.statemachine.service.DefaultStateMachineService;

@Configuration
@Scope(scopeName = "prototype")
@EnableAutoConfiguration
@EnableStateMachineFactory
public class AcceptanceStateMachineConfiguration extends StateMachineConfigurerAdapter<AcceptanceStates, AcceptanceEvents> {

    @Autowired
    StateMachineRuntimePersister<AcceptanceStates, AcceptanceEvents, String> stateMachineRuntimePersister;
    @Autowired
    private StateRepository<? extends RepositoryState> stateRepository;
    @Autowired
    private TransitionRepository<? extends RepositoryTransition> transitionRepository;

    @Override
    public void configure(StateMachineConfigurationConfigurer<AcceptanceStates, AcceptanceEvents> config)
            throws Exception {
        config
                .withPersistence()
                .runtimePersister(stateMachineRuntimePersister)
                .and()
                .withConfiguration()
                .listener(new StateMachineListener())

        ;
    }

    @Override
    public void configure(StateMachineStateConfigurer<AcceptanceStates, AcceptanceEvents> states) throws Exception {
        states
                .withStates()
                .initial(AcceptanceStates.PENDING)
                .state(AcceptanceStates.ACCOUNTING_IN_PROGRESS)
                .end(AcceptanceStates.APPROVED)
                .end(AcceptanceStates.REJECTED)
                .end(AcceptanceStates.CANCELLED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<AcceptanceStates, AcceptanceEvents> transitions) throws Exception {
        transitions.withExternal()
                .source(AcceptanceStates.PENDING).target((AcceptanceStates.ACCOUNTING_IN_PROGRESS)).event(AcceptanceEvents.VALIDATE)
                .and().withExternal()
                .source(AcceptanceStates.ACCOUNTING_IN_PROGRESS).target(AcceptanceStates.APPROVED).event(AcceptanceEvents.APPROVE)
                .and().withExternal()
                .source(AcceptanceStates.ACCOUNTING_IN_PROGRESS).target(AcceptanceStates.CANCELLED).event(AcceptanceEvents.CANCEL)
                .and().withExternal()
                .source(AcceptanceStates.ACCOUNTING_IN_PROGRESS).target(AcceptanceStates.REJECTED).event(AcceptanceEvents.REJECT).and().withInternal()
                .source(AcceptanceStates.ACCOUNTING_IN_PROGRESS)
                .timerOnce(10000)
                .state(AcceptanceStates.CANCELLED)
        ;


    }

    @Configuration
    public static class JpaPersisterConfig {

        @Bean
        public StateMachineRuntimePersister<AcceptanceStates, AcceptanceEvents, String> stateMachineRuntimePersister(
                JpaStateMachineRepository jpaStateMachineRepository) {
            return new JpaPersistingStateMachineInterceptor<>(jpaStateMachineRepository);
        }
    }

    @Configuration
    public static class StateMachineServiceConfig {
        @Bean
        public DefaultStateMachineService<AcceptanceStates, AcceptanceEvents> stateMachineService(
                StateMachineFactory<AcceptanceStates, AcceptanceEvents> stateMachineFactory,
                StateMachineRuntimePersister<AcceptanceStates, AcceptanceEvents, String> stateMachineRuntimePersister) {
            return new DefaultStateMachineService<>(stateMachineFactory, stateMachineRuntimePersister);
        }
    }

}