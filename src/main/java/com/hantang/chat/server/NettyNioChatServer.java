package com.hantang.chat.server;

import com.hantang.chat.codec.FieldLengthStringDecoder;
import com.hantang.chat.codec.FieldLengthStringEncoder;
import com.hantang.chat.codec.ImMessageDecoder;
import com.hantang.chat.codec.ImMessageEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;
import org.springframework.context.Lifecycle;

import java.net.InetSocketAddress;

/**
 * @author HuangLongcan
 * @since 2017-05-14
 */
public class NettyNioChatServer implements Lifecycle{

    private volatile boolean running;

    private int backLog;

    private int bossThreads = 1;

    private int workerThreads = Runtime.getRuntime().availableProcessors();

    private boolean tcpNoDelay = true;

    private boolean keepAlive = true;

    private int port = 10000;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private EventExecutorGroup bizGroup;

    private Channel serverChannel;

    private ChannelHandler loginHandler;

    private ChannelHandler chatHandler;

    public void initPipeline(ChannelPipeline pipeline){
        pipeline.addLast("stringEncoder",new FieldLengthStringEncoder());
        pipeline.addLast("messageEncoder",new ImMessageEncoder());
        pipeline.addLast("stringDecoder",new FieldLengthStringDecoder());
        pipeline.addLast("messageDecoder",new ImMessageDecoder());
        pipeline.addLast(bizGroup,"loginHandler",loginHandler);
        pipeline.addLast("chatHandler",chatHandler);
    }

    public void start() {
        ServerBootstrap sbt = new ServerBootstrap();
        if(bossThreads <= 0){
            throw new IllegalArgumentException("bossThreads lte zero !");
        }
        bossGroup = new NioEventLoopGroup(bossThreads);
        if(workerThreads > 0) {
            workerGroup = new NioEventLoopGroup(workerThreads);
        }
        if(workerGroup == null){
            sbt.group(bossGroup);
        }else {
            sbt.group(bossGroup,workerGroup);
        }
        sbt.channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        initPipeline(pipeline);
                    }
                })
                .option(ChannelOption.SO_BACKLOG,backLog)
                .childOption(ChannelOption.TCP_NODELAY,tcpNoDelay)
                .childOption(ChannelOption.SO_KEEPALIVE,keepAlive);

        try {
            serverChannel = sbt.bind(new InetSocketAddress(port)).syncUninterruptibly().channel();
            running = true;
        }finally {
            if(!running){
                closeEventExecutorGroups();
            }
        }
    }

    public void stop() {
        if(!isRunning()){
            return;
        }
        try {
            serverChannel.close().awaitUninterruptibly();
        }finally {
            closeEventExecutorGroups();
            running = false;
        }
    }

    private void closeEventExecutorGroups(){
        if(bossGroup != null && !bossGroup.isShutdown()){
            bossGroup.shutdownGracefully();
            bossGroup = null;
        }
        if(workerGroup != null && !workerGroup.isShutdown()){
            workerGroup.shutdownGracefully();
            workerGroup = null;
        }
        if(bizGroup != null && !bizGroup.isShutdown()){
            bizGroup.shutdownGracefully();
            bizGroup = null;
        }
    }

    public boolean isRunning() {
        return running;
    }

    public int getBackLog() {
        return backLog;
    }

    public void setBackLog(int backLog) {
        this.backLog = backLog;
    }

    public int getBossThreads() {
        return bossThreads;
    }

    public void setBossThreads(int bossThreads) {
        this.bossThreads = bossThreads;
    }

    public int getWorkerThreads() {
        return workerThreads;
    }

    public void setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setBizGroup(EventExecutorGroup bizGroup){
        this.bizGroup = bizGroup;
    }

    public ChannelHandler getChatHandler() {
        return chatHandler;
    }

    public void setChatHandler(ChannelHandler chatHandler) {
        this.chatHandler = chatHandler;
    }

    public ChannelHandler getLoginHandler() {
        return loginHandler;
    }

    public void setLoginHandler(ChannelHandler loginHandler) {
        this.loginHandler = loginHandler;
    }
}
