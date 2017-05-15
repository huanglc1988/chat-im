package com.hantang.chat.client;

import com.alibaba.fastjson.JSON;
import com.hantang.chat.handler.ChannelsHolder;
import com.hantang.chat.model.ImMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author HuangLongcan
 * @since 2017-05-14
 */
public class ClientChatChannelHandler extends SimpleChannelInboundHandler<ImMessage> {

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ImMessage msg) throws Exception {
        System.out.println(JSON.toJSONString(msg));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }
}
