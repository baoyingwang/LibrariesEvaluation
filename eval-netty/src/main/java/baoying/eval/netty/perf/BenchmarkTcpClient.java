package baoying.eval.netty.perf;

import baoying.eval.perf.SystemUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.IOException;
import java.nio.ByteBuffer;


public class BenchmarkTcpClient extends ChannelInboundHandlerAdapter {

    private int count = 0;
    private boolean warmingUp = false;
    private boolean benchmarking = false;
    private long tsSent;
    private final byte[] readArray;
    private final int messages;
    private final int msgSize;

    public BenchmarkTcpClient() throws IOException {
        super();

        this.msgSize = SystemUtils.getInt("msgSize", 256);
        this.messages = SystemUtils.getInt("messages", 1000000);
        this.readArray = new byte[msgSize];
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {

        this.warmingUp = true;
        this.benchmarking = false;
        this.count = 0;

        sendMsg(-1, ctx); // very first message, so the other side knows we are starting...
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {

        ByteBuf in = (ByteBuf) msg;
        ByteBuffer bb = in.nioBuffer();

        handleBuffer(ctx, bb, msg);

        in.release(); // netty uses reference count...
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

        long tsReceived = buf.getLong();

        buf.get(readArray, 0, buf.remaining()); // read fully

        if (tsReceived != tsSent) {
            System.err.println("Bad timestap received: tsSent=" + tsSent + " tsReceived=" + tsReceived);
            ctx.close();
            return;
        }

        if (warmingUp) {

            if (++count == messages) { // done warming up...

                System.out.println("Finished warming up! messages=" + count);

                this.warmingUp = false;
                this.benchmarking = true;
                this.count = 0;

                sendMsg(System.nanoTime(), ctx); // first testing message

            } else {

                sendMsg(0, ctx);
            }

        } else if (benchmarking) {

            if (++count == messages) {

                System.out.println("Finished sending messages! messages=" + count);

                // send the last message to tell the client we are done...
                sendMsg(-2, ctx);
                ctx.close();

            } else {

                sendMsg(System.nanoTime(), ctx);
            }
        }
    }

    private final void sendMsg(long value, ChannelHandlerContext ctx) {

        ByteBuf tsMsg = ctx.alloc().directBuffer(msgSize);

        tsMsg.writeLong(value);

        for(int i = 0; i < msgSize - 8; i++) {
            tsMsg.writeByte((byte) 'x');
        }

        ctx.writeAndFlush(tsMsg);

        tsSent = value; // save to check echo msg...
    }

    public static void main(String[] args) throws Exception {

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new BenchmarkTcpClient());
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync(); // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}