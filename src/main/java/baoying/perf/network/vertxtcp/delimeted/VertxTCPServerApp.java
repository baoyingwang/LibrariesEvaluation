package baoying.perf.network.vertxtcp.delimeted;


import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.parsetools.RecordParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import baoying.perf.network.Benchmarker;
import baoying.perf.network.SystemUtils;
import baoying.perf.network.vertxtcp.VertxUtil;


public class VertxTCPServerApp {

    private final static Logger log = LoggerFactory.getLogger(VertxTCPServerApp.class);
    int _msgSize = SystemUtils.getInt("msgSize", 256);

    private final Benchmarker bench = Benchmarker.create("svr:upstream");

    public void start(){

        System.out.println("svr msg size:" + _msgSize);

        Vertx vertx = Vertx.vertx();
        NetServerOptions options = new NetServerOptions()
                .setTcpNoDelay(true)
                .setAcceptBacklog(128)
                .setTcpKeepAlive(true);
        NetServer server = vertx.createNetServer(options);

        server.connectHandler(socket -> {

            //http://vertx.io/docs/vertx-core/java/#_record_parser
            final RecordParser parser = RecordParser.newDelimited(SystemUtils.delimiter(), buffer -> {

                long clientSendTime =  buffer.getLong(0);
                if(clientSendTime > 0 ){

                    long svrTime = System.nanoTime();

                    bench.measure(svrTime-clientSendTime);

                    Buffer svrB = VertxUtil.getB(svrTime,clientSendTime, _msgSize, SystemUtils.delimiter());
                    socket.write(svrB);

                }else if(clientSendTime == -1){
                    //first, ignore
                }else if(clientSendTime == -2){

                    Buffer svrB = VertxUtil.getB(-1,_msgSize, SystemUtils.delimiter());
                    socket.write(svrB);

                    StringBuilder results = new StringBuilder();
                    results.append(Thread.currentThread().getName()).append(":");
                    results.append("results=");
                    results.append(bench.results());
                    System.out.println(results);
                }

            });

            socket.handler(buffer -> {
                parser.handle(buffer);
            });
        });
        server.listen(10005, "localhost", res -> {
            if (res.succeeded()) {
                System.out.println("Server is now listening!");
            } else {
                System.out.println("Failed to bind!");
            }
        });

    }

    public static void main(String[] args) {

        new VertxTCPServerApp().start();
    }


}

