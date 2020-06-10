package mishra.sandeep.acceptancestatemachine.config;

import mishra.sandeep.acceptancestatemachine.model.AcceptanceEvents;
import mishra.sandeep.acceptancestatemachine.model.AcceptanceStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class StateMachineCollection {

    @Autowired
    ApplicationContext context;

    @Autowired
    StateMachineFactory<AcceptanceStates, AcceptanceEvents> factory;

    @Autowired
    AcceptanceStateMachineInterceptor acceptanceStateMachineInterceptor;

    ConcurrentHashMap<String,StateMachine<AcceptanceStates, AcceptanceEvents>> stateMachines;

    public StateMachineCollection()
    {
        stateMachines = new ConcurrentHashMap<>();
    }

    public StateMachine<AcceptanceStates, AcceptanceEvents> getStateMachine(String id)
    {
        return stateMachines.get(id);
    }

    public boolean createStateMachine( String id)
    {
        if(stateMachines.containsKey(id))
            return false;
        else {
            StateMachine<AcceptanceStates, AcceptanceEvents> stateMachine = factory.getStateMachine(id);
            stateMachine.getStateMachineAccessor().withRegion().addStateMachineInterceptor(acceptanceStateMachineInterceptor);
            stateMachines.put(id, stateMachine);
        }

        return true;
    }
}
