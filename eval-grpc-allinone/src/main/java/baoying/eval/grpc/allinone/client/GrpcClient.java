package baoying.eval.grpc.allinone.client;


import baoying.eval.grpc.allinone.HelloRequest;
import baoying.eval.grpc.allinone.HelloServiceGrpc;
import baoying.eval.grpc.allinone.HelloResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClient {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        HelloServiceGrpc.HelloServiceBlockingStub stub
                = HelloServiceGrpc.newBlockingStub(channel);

        HelloResponse helloResponse = stub.hello(HelloRequest.newBuilder()
                .setFirstName("Baoying")
                .setLastName("Wang")
                .build());

        System.out.println("Response received from server:\n" + helloResponse);

        channel.shutdown();
    }
}