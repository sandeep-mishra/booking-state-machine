package mishra.sandeep.acceptancestatemachine.service;

import io.grpc.stub.StreamObserver;
import mishra.sandeep.acceptancestatemachine.model.Acceptance;
import mishra.sandeep.acceptancestatemachine.model.AcceptanceEvents;
import mishra.sandeep.acceptancestatemachine.model.AcceptanceStates;
import mishra.sandeep.acceptancestatemachine.proto.AcceptanceServiceGrpc;
import mishra.sandeep.acceptancestatemachine.proto.Response;
import mishra.sandeep.acceptancestatemachine.proto.StatusSearchRequest;
import mishra.sandeep.acceptancestatemachine.repository.AcceptanceRepository;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.data.StateMachineRepository;
import org.springframework.statemachine.service.DefaultStateMachineService;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@GRpcService
public class AcceptanceService extends AcceptanceServiceGrpc.AcceptanceServiceImplBase {

    @Autowired
    AcceptanceRepository acceptanceRepository;

    @Autowired
    StateMachineRepository stateMachineRepository;

    @Autowired
    private DefaultStateMachineService<AcceptanceStates, AcceptanceEvents> stateMachineService;

    @Override
    @Transactional
    public void createAcceptance(mishra.sandeep.acceptancestatemachine.proto.Acceptance request, StreamObserver<Response> responseObserver) {
        String machineId = request.getId();
        Response response = null;

        if (stateMachineService.hasStateMachine(machineId) || stateMachineRepository.findById(machineId).isPresent()) {
            //StateMachine is regisered in service and active OR
            //StateMachine is  available in persistance
            //hence creation fails.

            response = Response.newBuilder().setStatus("Failure").build();
        } else {

            Acceptance acceptance = new Acceptance(request.getId());
            acceptance.setFaId(request.getFaId());
            acceptance.setCurrency(request.getCurrency());
            acceptance.setTimestamp(Instant.now().toString());
            acceptanceRepository.save(acceptance);

            StateMachine<AcceptanceStates, AcceptanceEvents> stateMachine = null;
            try {
                stateMachine = stateMachineService.acquireStateMachine(machineId, true);

                //update status in acceptance record
                acceptance.setState(AcceptanceStates.valueOf(stateMachine.getState().getId().name()));
                acceptanceRepository.save(acceptance);

                response = Response.newBuilder().setStatus("Success").build();
            } catch (Exception e) {
                response = Response.newBuilder().setStatus("Failure").build();
            }
        }
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getAcceptance(mishra.sandeep.acceptancestatemachine.proto.Acceptance request, StreamObserver<mishra.sandeep.acceptancestatemachine.proto.Acceptance> responseObserver) {
        Optional<Acceptance> instruction = acceptanceRepository.findById(request.getId());

        mishra.sandeep.acceptancestatemachine.proto.Acceptance acceptance = null;

        if(instruction.isPresent()) {
            Acceptance i = instruction.get();
            acceptance = mishra.sandeep.acceptancestatemachine.proto.Acceptance.newBuilder().setId(i.getId()).setCurrency("sgd").setFaId("fa_id").setStatus(i.getState().name()).build();
        }
        responseObserver.onNext(acceptance);
        responseObserver.onCompleted();
    }

    @Override
    public void getAcceptancesByStatus(StatusSearchRequest request, StreamObserver<mishra.sandeep.acceptancestatemachine.proto.Acceptance> responseObserver) {
        List<Acceptance> acceptances = acceptanceRepository.findByState(AcceptanceStates.valueOf(request.getStatus(0)));

        mishra.sandeep.acceptancestatemachine.proto.Acceptance acceptance = null;

        Iterator<Acceptance> iterator = acceptances.listIterator();
        while(iterator.hasNext())
        {
            Acceptance i = iterator.next();
            responseObserver.onNext(mishra.sandeep.acceptancestatemachine.proto.Acceptance.newBuilder().setId(i.getId()).setCurrency("sgd").setFaId("fa_id").setStatus(i.getState().name()).build());
        }
        responseObserver.onCompleted();
    }
}
