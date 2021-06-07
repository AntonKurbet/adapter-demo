package ru.bis.adapterdemo.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerApp {
    private static final Logger LOG = LoggerFactory.getLogger(ServerApp.class);

    public ServerApp() {

        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) {
                            channel.pipeline().addLast("handler",
                                    new MessagesHandler());
                        }
                    });
            ChannelFuture future = bootstrap.bind(8189).sync();
            LOG.info("server started on PORT = 8189!");
            future.channel().closeFuture().sync(); // block
        } catch (InterruptedException e) {
            LOG.error("e=", e);
        } finally {
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new ServerApp();
    }
}
