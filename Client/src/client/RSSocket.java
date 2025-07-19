package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public final class RSSocket {

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private byte[] inputBuffer = new byte[5000];
    private int readIndex = 0;
    private int writeIndex = 0;

    public RSSocket(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = socket.getInputStream();
        this.outputStream = socket.getOutputStream();
    }

    public void queueBytes(int length, byte[] data) throws IOException {
        outputStream.write(data, 0, length);
        outputStream.flush();
    }

    public int read() throws IOException {
        if (readIndex == writeIndex) {
            readIndex = 0;
            writeIndex = inputStream.read(inputBuffer, 0, inputBuffer.length);
            if (writeIndex <= 0) return -1;
        }
        return inputBuffer[readIndex++] & 0xff;
    }

    public void flushInputStream(byte[] buffer, int length) throws IOException {
        int offset = 0;
        while (offset < length) {
            int readCount = inputStream.read(buffer, offset, length - offset);
            if (readCount <= 0)
                throw new IOException("Unable to read from input stream.");
            offset += readCount;
        }
    }

    public void close() {
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}