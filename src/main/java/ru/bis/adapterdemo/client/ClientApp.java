package ru.bis.adapterdemo.client;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientApp {
    private static final Logger LOG = LoggerFactory.getLogger(ClientApp.class);
    private static final Path FILE_PATH = new File("test.xml").toPath();

    public static void main(String[] args) {

        Socket socket;
        try {
            socket = new Socket("localhost", 8189);
            LOG.info("Connected to server...");
        } catch (IOException e) {
            LOG.error("e = ", e);
            return;
        }

        new Thread(() -> {
            try {
                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();

                byte[] command = {0, 1, 2, 101};
                os.write(command);
                LOG.info("Sent check command");
                waitResponse(is, command, 4);

                command[3] = 102;
                LOG.info("Sent file command");

                byte[] fileContent = Files.readAllBytes(FILE_PATH);
                int fileSize = fileContent.length;
                byte[] data = new byte[4 + 4 + fileSize];
                System.arraycopy(command,0,data,0,4);
                System.arraycopy(ByteBuffer.allocate(4).putInt(fileSize).array(),0,data,4,4);
                System.arraycopy(fileContent,0,data,8,fileSize);
                os.write(data);
                waitResponse(is, command, 4);

                LOG.info("Sent get command");
                command[3] = 103;
                os.write(command);
                data = new byte[8];
                waitResponse(is, data, 8 );
                byte[] bi = Arrays.copyOfRange(data, 4, 8);
                LOG.info("Result is " + ByteBuffer.wrap(bi).getInt());

            } catch (IOException e) {
                LOG.error("e = ", e);
            }
        }).start();

    }

    private static void waitResponse(InputStream finalIs, byte[] data, int len) throws IOException {
        int numBytes = finalIs.read(data, 0, len);
        if (Arrays.equals(data, new byte[]{0, 1, 2, 0}))
            LOG.info("Recieved OK");
        else
            LOG.info("Received " + numBytes + " bytes " + Arrays.toString(data));
    }
}
