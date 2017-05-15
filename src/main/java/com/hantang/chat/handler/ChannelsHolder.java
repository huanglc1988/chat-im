package com.hantang.chat.handler;

import com.hantang.chat.model.ImMessage;
import com.hantang.chat.model.User;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author HuangLongcan
 * @since 2017-05-14
 */
public class ChannelsHolder{

    public static final AtomicLong NEXT_ID = new AtomicLong();

    public final AtomicInteger loginAmount = new AtomicInteger();

    private EventExecutorGroup bizGroup;

    private ChannelGroup channels;

    public final ConcurrentMap<Long,Channel> channelsMap = new ConcurrentHashMap<>();

    public final ConcurrentMap<Channel,User> usersMap = new ConcurrentHashMap<>();

    public ChannelGroup getChannels(){
        return this.channels;
    }

    public void sendToAll(Channel self, ImMessage message){
        channels.writeAndFlush(message, channel
                -> channel != self && channel.isActive());
    }

    @PostConstruct
    public void init(){
        channels = new DefaultChannelGroup("allChannels",bizGroup != null ? bizGroup.next() : GlobalEventExecutor.INSTANCE);
    }

    @PreDestroy
    public void destroy(){
        channels.close();
    }

    public EventExecutorGroup getBizGroup() {
        return bizGroup;
    }

    public void setBizGroup(EventExecutorGroup bizGroup) {
        this.bizGroup = bizGroup;
    }
}
