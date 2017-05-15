package com.hantang.chat.codec;

import com.alibaba.fastjson.JSON;
import com.hantang.chat.model.ImMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author HuangLongcan
 * @since 2017-05-14
 */
public class ImMessageEncoder extends MessageToMessageEncoder<ImMessage>{

    protected void encode(ChannelHandlerContext ctx, ImMessage msg, List<Object> out) throws Exception {
        out.add(JSON.toJSONString(msg));
    }

}
