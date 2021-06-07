package ru.bis.adapterdemo.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.*;
import java.util.Arrays;


public class MessagesHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final Logger LOG = LoggerFactory.getLogger(MessagesHandler.class);
    private static final Path FILE_PATH = new File("test2.xml").toPath();
    private static final byte[] RESP_OK = {0, 1, 2, 0};
    private static final byte[] RESP_ERR = {0, 1, 2, -100};

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
        try {
            byte[] bytes = new byte[msg.readableBytes()];
            msg.readBytes(bytes);

            if (!checkHeader(bytes)) return;

            LOG.info("Received command " + bytes[3]);
            switch (bytes[3]) {
                case 101:
                    writeResponse(ctx, msg, true);
                    break;
                case 102:
                    writeFile(bytes);
                    writeResponse(ctx, msg, true);
                    break;
                case 103:
                    writeResponse(ctx, msg, createResponse(processXML()));
                    break;
            }
        } catch (Exception e) {
            LOG.error(e.toString());
            writeResponse(ctx, msg, false);
        }
    }

    private boolean checkHeader(byte[] bytes) {
        return bytes[0] == 0 && bytes[1] == 1 && bytes[2] == 2;
    }

    private byte[] createResponse(int value) {
        byte[] response = new byte[8];
        System.arraycopy(ByteBuffer.allocate(4).putInt(4).array(), 0, response, 0, 4);
        System.arraycopy(ByteBuffer.allocate(4).putInt(value).array(), 0, response, 4, 4);
        return response;
    }

    private void writeFile(byte[] bytes) throws IOException {
        byte[] bi = Arrays.copyOfRange(bytes, 4, 8);
        int size = ByteBuffer.wrap(bi).getInt();
        byte[] contents = Arrays.copyOfRange(bytes, 8, 8 + size);
        Files.deleteIfExists(FILE_PATH);
        Files.write(FILE_PATH, contents, StandardOpenOption.CREATE_NEW);
        LOG.info("File created");
    }

    private int processXML() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Envelope.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        File file = new File(FILE_PATH.toUri());
        Envelope envelope = (Envelope) unmarshaller.unmarshal(file);
        return envelope.code;
    }

    private void writeResponse(ChannelHandlerContext ctx, ByteBuf msg, boolean isOk) {
        byte[] b = isOk ? RESP_OK : RESP_ERR;
        msg.writeBytes(b);
        ctx.writeAndFlush(msg);
        LOG.info("Sent " + (isOk ? "OK" : "Error"));
    }

    private void writeResponse(ChannelHandlerContext ctx, ByteBuf msg, byte[] bytes) {
        msg.writeBytes(bytes);
        ctx.writeAndFlush(msg);
        LOG.info("Sent " + Arrays.toString(bytes));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {//
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        //
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        //
    }
}
