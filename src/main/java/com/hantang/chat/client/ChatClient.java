package com.hantang.chat.client;

import com.hantang.chat.codec.FieldLengthStringDecoder;
import com.hantang.chat.codec.FieldLengthStringEncoder;
import com.hantang.chat.codec.ImMessageDecoder;
import com.hantang.chat.codec.ImMessageEncoder;
import com.hantang.chat.model.ImMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.UUID;

/**
 * @author HuangLongcan
 * @since 2017-05-15
 */
public class ChatClient {

    public static void main(String[] args) {

        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bt = new Bootstrap();
        bt.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast("stringEncoder",new FieldLengthStringEncoder());
                pipeline.addLast("messageEncoder",new ImMessageEncoder());
                pipeline.addLast("stringDecoder",new FieldLengthStringDecoder());
                pipeline.addLast("messageDecoder",new ImMessageDecoder());
                pipeline.addLast("handler",new ClientChatChannelHandler());
            }
        }).option(ChannelOption.TCP_NODELAY,true);

        Channel client = bt.connect("localhost",10000).syncUninterruptibly().channel();
        try{
            ImMessage im = new ImMessage();
            im.setType(1);
            im.setName("NAME-"+ UUID.randomUUID().toString());
            client.writeAndFlush(im);
            client.closeFuture().syncUninterruptibly();
        }finally {
            group.shutdownGracefully();
        }

    }

}
