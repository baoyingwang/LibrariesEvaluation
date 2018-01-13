package perf.network.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import perf.network.Benchmarker;
import perf.network.SystemUtils;

import java.io.IOException;
import java.nio.ByteBuffer;


public class EchoTcpServer extends ChannelInboundHandlerAdapter{

    private byte[] readArray = new byte[1024 * 2];

    private final Benchmarker bench = Benchmarker.create("netty-echoserver");

    private final int msgSize;

    public EchoTcpServer() throws IOException {
        super();
        this.msgSize = SystemUtils.getInt("msgSize", 256);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        ByteBuf in = (ByteBuf) msg;
        ByteBuffer bb = in.nioBuffer();

        handleBuffer(ctx, bb, msg);
    }

    private void handleBuffer(ChannelHandlerContext ctx, ByteBuffer buf, Object msg) {

        while(buf.remaining() >= msgSize) {
            int pos = buf.position();
            int lim = buf.limit();
            buf.limit(pos + msgSize);
            handleMessage(ctx, buf, msg);
            buf.limit(lim).position(pos + msgSize);
        }
    }

    private void handleMessage(ChannelHandlerContext ctx, ByteBuffer buf, Object msg) {

        int pos = buf.position();

        long tsReceived = buf.getLong();

        buf.get(readArray, 0, buf.remaining());

        if (tsReceived > 0) {
            bench.measure(System.nanoTime() - tsReceived);
        } else if (tsReceived == -1) {
            // first message
        } else if (tsReceived == -2) {
            // last message
            ctx.close();
            printResults();
            return;
        } else if (tsReceived < 0) {
            System.err.println("Received bad timestamp: " + tsReceived);
            ctx.close();
            return;
        }

        buf.position(pos);
        ctx.writeAndFlush(msg);
    }

    private void printResults() {
        StringBuilder results = new StringBuilder();
        results.append("results=");
        results.append(bench.results());
        System.out.println(results);
    }

    public static void main(String[] args) throws Exception {

        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoTcpServer());
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(port).sync(); // (7)
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}

