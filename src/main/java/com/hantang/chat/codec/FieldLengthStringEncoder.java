package com.hantang.chat.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.charset.Charset;
import java.util.List;

public class FieldLengthStringEncoder extends MessageToMessageEncoder<CharSequence> {

    private Charset charset = Charset.forName("UTF-8");

    @Override
    protected void encode(ChannelHandlerContext ctx, CharSequence msg, List<Object> out) throws Exception {
        if(msg == null || msg.length() == 0){
            return;
        }
        byte[] content = msg.toString().getBytes(charset);
        ByteArrayOutputStream bos = new ByteArrayOutputStream(4 + content.length);
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeInt(content.length);
        dos.write(content);
        content = bos.toByteArray();
        ByteBuf dst = ctx.alloc().buffer(content.length).writeBytes(content);
        out.add(dst);
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }
}
