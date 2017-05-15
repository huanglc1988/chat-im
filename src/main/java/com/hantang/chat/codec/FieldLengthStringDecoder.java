package com.hantang.chat.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.charset.Charset;

public class FieldLengthStringDecoder extends LengthFieldBasedFrameDecoder {

    private Charset charset = Charset.forName("UTF-8");

    public FieldLengthStringDecoder(){
        super(Integer.MAX_VALUE,0,4,0,4);
    }

    protected String decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception{
        ByteBuf content = (ByteBuf) super.decode(ctx,in);
        if(content == null){
            return null;
        }
        return content.toString(charset);
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }
}
