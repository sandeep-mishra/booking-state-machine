package mishra.sandeep.acceptancestatemachine.service;

import io.grpc.stub.StreamObserver;
import mishra.sandeep.acceptancestatemachine.model.Acceptance;
import mishra.sandeep.acceptancestatemachine.model.AcceptanceStates;
import mishra.sandeep.acceptancestatemachine.config.StateMachineCollection;
import mishra.sandeep.acceptancestatemachine.proto.AcceptanceServiceGrpc;
import mishra.sandeep.acceptancestatemachine.proto.Response;
import mishra.sandeep.acceptancestatemachine.proto.StatusSearchRequest;
import mishra.sandeep.acceptancestatemachine.repository.AcceptanceRepository;
import org.lognet.springboot.grpc.GRpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@GRpcService
public class AcceptanceService extends AcceptanceServiceGrpc.AcceptanceServiceImplBase {
    @Autowired
    AcceptanceRepository acceptanceRepository;

    @Autowired
    StateMachineCollection stateMachines;


    @Override
    @Transactional
    public void createAcceptance(mishra.sandeep.acceptancestatemachine.proto.Acceptance request, StreamObserver<Response> responseObserver) {
        Acceptance acceptance = new Acceptance(request.getId());
        acceptance.setFaId(request.getFaId());
        acceptance.setCurrency(request.getCurrency());
        acceptance.setTimestamp(Instant.now().toString());
        acceptanceRepository.save(acceptance);

        stateMachines.createStateMachine(acceptance.getId());
        acceptance.setState(AcceptanceStates.valueOf(stateMachines.getStateMachine(acceptance.getId()).getState().getId().name()));
        acceptanceRepository.save(acceptance);

        Response response = Response.newBuilder().setStatus("Success").build();
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
        super.getAcceptancesByStatus(request, responseObserver);
    }
}
