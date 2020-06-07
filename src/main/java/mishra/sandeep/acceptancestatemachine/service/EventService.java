package mishra.sandeep.acceptancestatemachine.service;

import io.grpc.stub.StreamObserver;
import mishra.sandeep.acceptancestatemachine.model.AcceptanceEvents;
import mishra.sandeep.acceptancestatemachine.config.StateMachineCollection;
import mishra.sandeep.acceptancestatemachine.proto.EventRequest;
import mishra.sandeep.acceptancestatemachine.proto.EventServiceGrpc;
import mishra.sandeep.acceptancestatemachine.proto.Response;
import org.lognet.springboot.grpc.GRpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

@Configuration
@GRpcService
public class EventService extends EventServiceGrpc.EventServiceImplBase {

    Logger logger = LoggerFactory.getLogger(EventService.class);

    @Autowired
    StateMachineCollection stateMachines;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void post(EventRequest request, StreamObserver<Response> responseObserver) {
        Boolean response = false;

        if (stateMachines.getStateMachine(request.getInstructionId()) != null) {
            response = stateMachines.getStateMachine(request.getInstructionId()).sendEvent(AcceptanceEvents.valueOf(request.getEvent()));
        }



        Response eventResponse;
        if(response == true)
            eventResponse = Response.newBuilder().setStatus("Success").build();
        else
            eventResponse = Response.newBuilder().setStatus("Failure").build();

        responseObserver.onNext(eventResponse);
        responseObserver.onCompleted();
    }
}
