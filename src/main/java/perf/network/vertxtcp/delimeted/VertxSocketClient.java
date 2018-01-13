package perf.network.vertxtcp.delimeted;

import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.parsetools.RecordParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import perf.network.Benchmarker;
import perf.network.SystemUtils;
import perf.network.vertxtcp.VertxUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class VertxSocketClient {

    private final static Logger log = LoggerFactory.getLogger(VertxSocketClient.class);

    int _msgSize = SystemUtils.getInt("msgSize", 256);

    final Benchmarker downBench = Benchmarker.create("client:downstream");
    final Benchmarker e2eBench = Benchmarker.create("client:e2e-round");

    public void intensiveSend(int total){

        System.out.println("client msg size:" + _msgSize);

        Vertx vertx = Vertx.vertx();

        NetClientOptions options = new NetClientOptions()
                .setConnectTimeout(10000);
                //.setTcpNoDelay(true);
        NetClient client = vertx.createNetClient(options);

        

        AtomicInteger counter = new AtomicInteger(0);


        client.connect(10005, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("Connected!");
                NetSocket socket = res.result();

                //http://vertx.io/docs/vertx-core/java/#_record_parser
                final RecordParser parser = RecordParser.newDelimited(SystemUtils.delimiter(), buffer -> {

                    long svrSendAckTime =  buffer.getLong(0);
                    long clientSendTime =  buffer.getLong(8);

                    if(svrSendAckTime > 0 ){

                        long recvSvrAckTime = System.nanoTime();
                        downBench.measure(recvSvrAckTime-svrSendAckTime);
                        e2eBench.measure(recvSvrAckTime-clientSendTime);

                        if(e2eBench.counter.get() < total) {
                            socket.write(VertxUtil.getB(System.nanoTime(), _msgSize, SystemUtils.delimiter()));
                        }else{
                            socket.write(VertxUtil.getB(-2, _msgSize, SystemUtils.delimiter()));
                        }


                    }else {

                        StringBuilder results = new StringBuilder();
                        results.append(Thread.currentThread().getName()).append(": downBench");
                        results.append("results=");
                        results.append(downBench.results());
                        results.append("\n");

                        results.append(Thread.currentThread().getName()).append(": e2eBench");
                        results.append("results=");
                        results.append(e2eBench.results());
                        results.append("\n");

                        System.out.println(results);


                    }
                });

                socket.handler(buffer -> {
                    parser.handle(buffer);
                });

                Buffer b = VertxUtil.getB(System.nanoTime(), _msgSize, SystemUtils.delimiter());
                socket.write(b);

            } else {
                log.error("Failed to connect, reason:{} ", res.cause().getMessage());
                System.exit(-1);
            }
        });

    }


    public static void main(String[] args){


        new VertxSocketClient().intensiveSend(SystemUtils.getInt("messages"
                , 1000*1000));

    }
}
