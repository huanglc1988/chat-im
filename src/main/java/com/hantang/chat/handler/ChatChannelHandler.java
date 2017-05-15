package com.hantang.chat.handler;

import com.alibaba.fastjson.JSONException;
import com.hantang.chat.model.ImMessage;
import com.hantang.chat.model.User;
import io.netty.channel.*;

/**
 * @author HuangLongcan
 * @since 2017-05-14
 */
@ChannelHandler.Sharable
public class ChatChannelHandler extends SimpleChannelInboundHandler<ImMessage> {

    private ChannelsHolder channelsHolder;

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        if(cause instanceof JSONException){

            return;
        }
        super.exceptionCaught(ctx, cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ImMessage msg) throws Exception {
        final Channel channel = ctx.channel();
        switch (msg.getType()){
            case 2: //对单个人发送消息
                Channel target = channelsHolder.channelsMap.get(msg.getTo());
                if(target == null){
                    User user = channelsHolder.usersMap.get(channel);
                    msg.setFrom(user.getId());
                    target.writeAndFlush(msg);
                }
                break;
            case 3: //对其他所有用户发送消息
                channelsHolder.sendToAll(channel,msg); //对其他所有在线用户发送信息
                break;
        }
    }

    public void setChannelsHolder(ChannelsHolder channelsHolder) {
        this.channelsHolder = channelsHolder;
    }
}
