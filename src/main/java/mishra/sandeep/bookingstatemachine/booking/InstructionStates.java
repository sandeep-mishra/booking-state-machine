package mishra.sandeep.bookingstatemachine.booking;

public enum InstructionStates {
    PENDING,
    ACCOUNTING_IN_PROGRESS,
    VALIDATED,
    APPROVED,
    REJECTED,
    CANCELLED
}
