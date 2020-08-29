package mishra.sandeep.acceptancestatemachine.service;

import io.grpc.stub.StreamObserver;
import mishra.sandeep.acceptancestatemachine.model.AcceptanceEvents;
import mishra.sandeep.acceptancestatemachine.model.AcceptanceStates;
import mishra.sandeep.acceptancestatemachine.proto.EventRequest;
import mishra.sandeep.acceptancestatemachine.proto.EventServiceGrpc;
import mishra.sandeep.acceptancestatemachine.proto.Response;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.service.DefaultStateMachineService;

@Configuration
@GRpcService
public class EventService extends EventServiceGrpc.EventServiceImplBase {

    Logger logger = LoggerFactory.getLogger(EventService.class);

    @Autowired
    private DefaultStateMachineService<AcceptanceStates, AcceptanceEvents> stateMachineService;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void post(EventRequest request, StreamObserver<Response> responseObserver) {
        Boolean response = false;
        try {
            StateMachine<AcceptanceStates, AcceptanceEvents> currentStateMachine = stateMachineService.acquireStateMachine(request.getInstructionId());
            response = currentStateMachine.sendEvent(AcceptanceEvents.valueOf(request.getEvent()));
        } catch (Exception e) {
            logger.error("error occurred in posting", e);
        }

        Response eventResponse;
        if (response == true) {
            eventResponse = Response.newBuilder().setStatus("Success").build();
        } else {
            logger.error("error occurred in posting");
            eventResponse = Response.newBuilder().setStatus("Failure").build();
        }

        responseObserver.onNext(eventResponse);
        responseObserver.onCompleted();
    }
}
