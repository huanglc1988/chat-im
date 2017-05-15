package com.hantang.chat.handler;

import com.hantang.chat.model.ImMessage;
import com.hantang.chat.model.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.*;

/**
 * @author HuangLongcan
 * @since 2017-05-14
 */
@ChannelHandler.Sharable
public class LoginChannelHandler extends ChannelInboundHandlerAdapter {

    private ChannelsHolder channelsHolder;

    private int maxLoginAmount = 500;

    private static ThreadLocal<Set<Channel>> connectChannels = new ThreadLocal<Set<Channel>>(){
        @Override
        protected Set<Channel> initialValue() {
            return new HashSet<>();
        }
    };

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        if(channelsHolder.loginAmount.get() >= maxLoginAmount){ //超过允许在线人员，不准登录
            channel.close();
            return;
        }
        connectChannels.get().add(channel);
        super.channelActive(ctx);
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel channel = ctx.channel();
        Set<Channel> connectSet = connectChannels.get();
        if(connectSet.contains(channel)){ //连接建立后第一条消息必须是登录消息，不然断开连接
            if(!(msg instanceof ImMessage
                    && ((ImMessage)msg).getType() == 1)){
                channel.close();
            }else {
                replyAndNotifyWhenLogin(channel,(ImMessage)msg);
            }
            connectSet.remove(channel);
            return;
        }
        ctx.fireChannelRead(msg);
    }

    private void replyAndNotifyWhenLogin(Channel channel,ImMessage im){
        User user = new User(ChannelsHolder.NEXT_ID.incrementAndGet(),im.getName());
        channelsHolder.getChannels().add(channel);
        List<User> list = new ArrayList<>(0);
        list.addAll(channelsHolder.usersMap.values());
        channelsHolder.channelsMap.put(user.getId(),channel);
        channelsHolder.usersMap.put(channel,user);
        channelsHolder.loginAmount.incrementAndGet();
        im.setFrom(user.getId());
        channel.writeAndFlush(im); //登录成功响应
        im = new ImMessage();
        im.setType(4);
        im.setUsers(list);
        channel.writeAndFlush(im); //给登陆发送所有在线人员列表
        im = new ImMessage();
        im.setType(5);
        im.setUsers(Arrays.asList(user));
        channelsHolder.sendToAll(channel,im); //给所有在线用户发送新增的登录人员
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        connectChannels.get().remove(channel);
        /*if(channelsHolder.getChannels().remove(channel)){
            channelsHolder.loginAmount.decrementAndGet();
            User user = channelsHolder.usersMap.remove(channel);
            if(user != null){
                channelsHolder.channelsMap.remove(user.getId());
                ImMessage im = new ImMessage();
                im.setType(6);
                channelsHolder.sendToAll(null,im);
            }
        }*/
        User user;
        if((user =  channelsHolder.usersMap.remove(channel)) != null){
            channelsHolder.loginAmount.decrementAndGet();
            channelsHolder.channelsMap.remove(user.getId());
            ImMessage im = new ImMessage();
            im.setType(6);
            channelsHolder.sendToAll(null,im); //给所有在线用户发送退出的人员
        }
        super.channelInactive(ctx);
    }

    public void setChannelsHolder(ChannelsHolder channelsHolder) {
        this.channelsHolder = channelsHolder;
    }

    public int getMaxLoginAmount() {
        return maxLoginAmount;
    }

    public void setMaxLoginAmount(int maxLoginAmount) {
        this.maxLoginAmount = maxLoginAmount;
    }
}
