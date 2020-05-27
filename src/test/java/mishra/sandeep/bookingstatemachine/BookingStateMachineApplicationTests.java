package mishra.sandeep.bookingstatemachine;

import mishra.sandeep.bookingstatemachine.booking.InstructionEvents;
import mishra.sandeep.bookingstatemachine.booking.InstructionStates;

import mishra.sandeep.bookingstatemachine.config.AcceptanceBookingStateMachineConfiguration;
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
@ContextConfiguration(classes = {AcceptanceBookingStateMachineConfiguration.class})
class BookingStateMachineApplicationTests {
	@Test
	void contextLoads() {
	}

	@Autowired
	private StateMachine<InstructionStates, InstructionEvents> stateMachine;

	@BeforeEach
	public void setUp() {
			stateMachine.start();
		}

	@Test
	public void whenRejectedEvent_thenStateMachineStateisRejected() {
			assertTrue(stateMachine.sendEvent(InstructionEvents.VALIDATE));
			assertEquals(InstructionStates.ACCOUNTING_IN_PROGRESS, stateMachine.getState().getId());
			assertTrue(stateMachine.sendEvent(InstructionEvents.REJECT));
			assertEquals(InstructionStates.REJECTED, stateMachine.getState().getId());
		}

	@Test
	public void whenApprovedEvent_thenStateMachineStateIsApproved() {
		assertTrue(stateMachine.sendEvent(InstructionEvents.VALIDATE));
		assertEquals(InstructionStates.ACCOUNTING_IN_PROGRESS, stateMachine.getState().getId());
		assertTrue(stateMachine.sendEvent(InstructionEvents.APPROVE));
		assertEquals(InstructionStates.APPROVED, stateMachine.getState().getId());
	}

	@AfterEach
	public void tearDown() {
			stateMachine.stop();
		}
}
