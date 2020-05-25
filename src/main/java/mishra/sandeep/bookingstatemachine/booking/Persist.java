package mishra.sandeep.bookingstatemachine.booking;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler.PersistStateChangeListener;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;



public class Persist {

    private final PersistStateMachineHandler handler;

    //tag::snippetA[]
    @Autowired
    private JdbcTemplate jdbcTemplate;
//end::snippetA[]

    private final PersistStateChangeListener listener = new LocalPersistStateChangeListener();

    public Persist(PersistStateMachineHandler handler) {
        this.handler = handler;
        this.handler.addPersistStateChangeListener(listener);
    }

    public String listDbEntries() {
        List<Instruction> Instructions = jdbcTemplate.query(
                "select id, type, state from instructions",
                new RowMapper<Instruction>() {
                    public Instruction mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new Instruction(rs.getString("id"), rs.getString("type"), InstructionStates.valueOf(rs.getString("state")));
                    }
                });
        StringBuilder buf = new StringBuilder();
        for (Instruction Instruction : Instructions) {
            buf.append(Instruction);
            buf.append("\n");
        }
        return buf.toString();
    }

    //tag::snippetB[]
    public void change(String Instruction, String event) {
        Instruction o = jdbcTemplate.queryForObject("select id, type, state from Instructions where id = ?", new Object[] { Instruction },
                new RowMapper<Instruction>() {
                    public Instruction mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new Instruction(rs.getString("id"), rs.getString("type"), InstructionStates.valueOf(rs.getString("state")));
                    }
                });

        handler.handleEventWithState(MessageBuilder
                .withPayload(event).setHeader("Instruction", Instruction).build(), o.state.name());
    }
    //end::snippetB[]

    //tag::snippetC[]
    private class LocalPersistStateChangeListener implements PersistStateChangeListener {

        @Override
        public void onPersist(State<String, String> state, Message<String> message,
                              Transition<String, String> transition, StateMachine<String, String> stateMachine) {
            if (message != null && message.getHeaders().containsKey("Instruction")) {
                String Instruction = message.getHeaders().get("Instruction", String.class);
                jdbcTemplate.update("update Instructions set state = ? where id = ?", state.getId(), Instruction);
            }
        }
    }
//end::snippetC[]

}