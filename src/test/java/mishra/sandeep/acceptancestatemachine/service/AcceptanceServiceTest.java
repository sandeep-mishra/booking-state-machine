package mishra.sandeep.acceptancestatemachine.service;

import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import mishra.sandeep.acceptancestatemachine.config.StateMachineCollection;
import mishra.sandeep.acceptancestatemachine.model.AcceptanceEvents;
import mishra.sandeep.acceptancestatemachine.model.AcceptanceStates;
import mishra.sandeep.acceptancestatemachine.proto.Acceptance;
import mishra.sandeep.acceptancestatemachine.proto.AcceptanceServiceGrpc;
import mishra.sandeep.acceptancestatemachine.proto.Response;
import mishra.sandeep.acceptancestatemachine.repository.AcceptanceRepository;
import org.junit.Rule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import io.grpc.testing.GrpcCleanupRule;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.statemachine.StateMachine;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
class AcceptanceServiceTest {

    @InjectMocks
    AcceptanceService acceptanceService;

    @Mock
    AcceptanceRepository acceptanceRepository;

    @Mock
    StateMachineCollection stateMachines;

    @Mock
    StateMachine<AcceptanceStates, AcceptanceEvents> stateMachine;

    @Rule
    public final GrpcCleanupRule grpcCleanup = new GrpcCleanupRule();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        mishra.sandeep.acceptancestatemachine.model.Acceptance acceptance = new mishra.sandeep.acceptancestatemachine.model.Acceptance();
        acceptance.setId("1");
        acceptance.setState(AcceptanceStates.PENDING);
        acceptance.setCurrency("SGD");

        Mockito.when(acceptanceRepository.save(acceptance)).thenReturn(acceptance);
        Mockito.when(stateMachines.getStateMachine("1")).thenReturn(stateMachine);
        //Mockito.when(stateMachine.toString()).thenReturn();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testCreateAcceptance() throws IOException {
        // Generate a unique in-process server name.
        String serverName = InProcessServerBuilder.generateName();

        // Create a server, add service, start, and register for automatic graceful shutdown.
        grpcCleanup.register(InProcessServerBuilder
                .forName(serverName).directExecutor().addService(acceptanceService).build().start());

        AcceptanceServiceGrpc.AcceptanceServiceBlockingStub blockingStub = AcceptanceServiceGrpc.newBlockingStub(
                // Create a client channel and register for automatic graceful shutdown.
                grpcCleanup.register(InProcessChannelBuilder.forName(serverName).directExecutor().build()));


        Response response =
                blockingStub.createAcceptance(Acceptance.newBuilder().setId("1").setFaId("FA_ID").setCurrency("SGD").build());

        assertEquals("Success", response.getStatus());
    }

    @Test
    void getAcceptance() {
    }

    @Test
    void getAcceptancesByStatus() {
    }
}