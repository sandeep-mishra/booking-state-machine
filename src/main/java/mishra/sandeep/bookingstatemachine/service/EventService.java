package mishra.sandeep.bookingstatemachine.service;

import io.grpc.stub.StreamObserver;
import mishra.sandeep.bookingstatemachine.booking.InstructionEvents;
import mishra.sandeep.bookingstatemachine.booking.InstructionStates;
import mishra.sandeep.bookingstatemachine.proto.EventRequest;
import mishra.sandeep.bookingstatemachine.proto.EventResponse;
import mishra.sandeep.bookingstatemachine.proto.EventServiceGrpc;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;

@Configuration
@GRpcService
public class EventService extends EventServiceGrpc.EventServiceImplBase {

    @Autowired
    private StateMachine<InstructionStates, InstructionEvents> stateMachine;

    @Override
    public void post(EventRequest request, StreamObserver<EventResponse> responseObserver) {
        stateMachine.sendEvent(InstructionEvents.valueOf(request.getEvent()));

        EventResponse eventResponse = EventResponse.newBuilder().setStatus("Success").build();
        responseObserver.onNext(eventResponse);
        responseObserver.onCompleted();

    }
}
