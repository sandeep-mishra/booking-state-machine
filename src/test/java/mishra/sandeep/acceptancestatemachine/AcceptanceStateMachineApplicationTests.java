package mishra.sandeep.acceptancestatemachine;

import mishra.sandeep.acceptancestatemachine.model.AcceptanceEvents;
import mishra.sandeep.acceptancestatemachine.model.AcceptanceStates;

import mishra.sandeep.acceptancestatemachine.config.AcceptanceStateMachineConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.context.ContextConfiguration;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

@SpringBootTest
@ContextConfiguration(classes = {AcceptanceStateMachineConfiguration.class})
class AcceptanceStateMachineApplicationTests {
	@Test
	void contextLoads() {
	}

	@Autowired
	private StateMachine<AcceptanceStates, AcceptanceEvents> stateMachine;


	@BeforeEach
	public void setUp() {
			stateMachine.start();
		}

	@Test
	public void whenRejectedEvent_thenStateMachineStateisRejected() {
			assertTrue(stateMachine.sendEvent(AcceptanceEvents.VALIDATE));
			assertEquals(AcceptanceStates.ACCOUNTING_IN_PROGRESS, stateMachine.getState().getId());
			assertTrue(stateMachine.sendEvent(AcceptanceEvents.REJECT));
			assertEquals(AcceptanceStates.REJECTED, stateMachine.getState().getId());
		}

	@Test
	public void whenApprovedEvent_thenStateMachineStateIsApproved() {
		assertTrue(stateMachine.sendEvent(AcceptanceEvents.VALIDATE));
		assertEquals(AcceptanceStates.ACCOUNTING_IN_PROGRESS, stateMachine.getState().getId());
		assertTrue(stateMachine.sendEvent(AcceptanceEvents.APPROVE));
		assertEquals(AcceptanceStates.APPROVED, stateMachine.getState().getId());
	}

	@AfterEach
	public void tearDown() {
			stateMachine.stop();
		}
}
