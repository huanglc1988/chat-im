package com.hantang.chat.codec;

import com.alibaba.fastjson.JSON;
import com.hantang.chat.model.ImMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @author HuangLongcan
 * @since 2017-05-14
 */
public class ImMessageDecoder extends MessageToMessageDecoder<String> {

    protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        ImMessage imMessage = JSON.parseObject(msg,ImMessage.class);
        out.add(imMessage);
    }
}
