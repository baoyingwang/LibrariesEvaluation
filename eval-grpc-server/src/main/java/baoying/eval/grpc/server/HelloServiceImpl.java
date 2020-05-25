package baoying.eval.grpc.server;

import baoying.eval.grpc.proto.HelloRequest;
import baoying.eval.grpc.proto.HelloResponse;
import baoying.eval.grpc.proto.HelloServiceGrpc.HelloServiceImplBase;
import io.grpc.stub.StreamObserver;

public class HelloServiceImpl extends HelloServiceImplBase {

    @Override
    public void hello(
            HelloRequest request, StreamObserver<HelloResponse> responseObserver) {
        System.out.println("Request received from client:\n" + request);

        String greeting = new StringBuilder().append("Hello, ")
                .append(request.getFirstName())
                .append(" ")
                .append(request.getLastName())
                .toString();

        HelloResponse response = HelloResponse.newBuilder()
                .setGreeting(greeting)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}