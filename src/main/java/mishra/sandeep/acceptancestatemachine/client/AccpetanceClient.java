package mishra.sandeep.acceptancestatemachine.client;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import mishra.sandeep.acceptancestatemachine.proto.EventRequest;
import mishra.sandeep.acceptancestatemachine.proto.EventServiceGrpc;
import mishra.sandeep.acceptancestatemachine.proto.Response;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccpetanceClient {
    private static final Logger logger = Logger.getLogger(AccpetanceClient.class.getName());

    private final EventServiceGrpc.EventServiceBlockingStub blockingStub;

    public AccpetanceClient(Channel channel) {
        // 'channel' here is a Channel, not a ManagedChannel, so it is not this code's responsibility to
        // shut it down.

        // Passing Channels to code makes code easier to test and makes it easier to reuse Channels.
        blockingStub = EventServiceGrpc.newBlockingStub(channel);
    }

    /**
     * Greet server. If provided, the first element of {@code args} is the name to use in the
     * greeting. The second argument is the target server.
     */
    public static void main(String[] args) throws Exception {
        String instructionId = "1";
        String event = "";
        // Access a service running on the local machine on port 50051
        String target = "localhost:6565";
        // Allow passing in the user and target strings as command line arguments
        if (args.length > 0) {
            if ("--help".equals(args[0])) {
                System.err.println("Usage: [instructionId event]");
                System.err.println("");
                System.exit(1);
            }
            instructionId = args[0];
        }
        if (args.length > 1) {
            event = args[1];
        }

        // Create a communication channel to the server, known as a Channel. Channels are thread-safe
        // and reusable. It is common to create channels at the beginning of your application and reuse
        // them until the application shuts down.
        ManagedChannel channel = ManagedChannelBuilder.forTarget(target)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext()
                .build();
        try {
            AccpetanceClient client = new AccpetanceClient(channel);
            client.post(instructionId, event);
        } finally {
            // ManagedChannels use resources like threads and TCP connections. To prevent leaking these
            // resources the channel should be shut down when it will no longer be used. If it may be used
            // again leave it running.
            channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    public void post(String instructionId, String event) {
        logger.info("Will try to post event " + event + " ...");
        EventRequest request = EventRequest.newBuilder().setInstructionId("1").setEvent(event).build();
        Response response;
        try {
            response = blockingStub.post(request);
        } catch (StatusRuntimeException e) {
            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
            return;
        }
        logger.info("Status: " + response.getStatus());
    }
}