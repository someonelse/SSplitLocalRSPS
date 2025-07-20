package server.util;

import server.Config;

/**
 * A byte stream buffer for network packet reading and writing.
 */
public class Stream {

    public byte[] buffer;
    public int currentOffset = 0;
    public int bitPosition = 0;
    public ISAACRandomGen packetEncryption = null;

    private static final int FRAME_STACK_SIZE = 10;
    private int frameStackPtr = -1;
    private final int[] frameStack = new int[FRAME_STACK_SIZE];

    public static final int[] bitMaskOut = new int[32];
    static {
        for (int i = 0; i < 32; i++)
            bitMaskOut[i] = (1 << i) - 1;
    }

    public Stream() {
        this(Config.BUFFER_SIZE);
    }

    public Stream(int capacity) {
        this.buffer = new byte[capacity];
    }

    public Stream(byte[] buffer) {
        this.buffer = buffer;
        this.currentOffset = 0;
    }

    // --- Basic read/write methods (truncated, see your original for all overloads) ---

    public void writeByte(int i) {
        ensureCapacity(1);
        buffer[currentOffset++] = (byte) i;
    }

    public int readUnsignedByte() {
        return buffer[currentOffset++] & 0xff;
    }

    public byte readSignedByte() {
        return buffer[currentOffset++];
    }

    public void writeWord(int i) {
        ensureCapacity(2);
        buffer[currentOffset++] = (byte) (i >> 8);
        buffer[currentOffset++] = (byte) i;
    }

    public int readUnsignedWord() {
        currentOffset += 2;
        return ((buffer[currentOffset - 2] & 0xff) << 8) + (buffer[currentOffset - 1] & 0xff);
    }

    public String readString() {
        int start = currentOffset;
        while (buffer[currentOffset++] != 10) ;
        return new String(buffer, start, currentOffset - start - 1);
    }

    public void writeString(String s) {
        ensureCapacity(s.length() + 1);
        byte[] strBytes = s.getBytes();
        System.arraycopy(strBytes, 0, buffer, currentOffset, strBytes.length);
        currentOffset += strBytes.length;
        buffer[currentOffset++] = 10;
    }

    // --- Variable frame/packet methods (unchanged logic, modernized formatting) ---

    public void createFrame(int id) {
        ensureCapacity(1);
        buffer[currentOffset++] = (byte) (id + packetEncryption.getNextKey());
    }

    public void createFrameVarSize(int id) {
        ensureCapacity(3);
        buffer[currentOffset++] = (byte) (id + packetEncryption.getNextKey());
        buffer[currentOffset++] = 0;
        if (frameStackPtr >= FRAME_STACK_SIZE - 1) {
            throw new RuntimeException("Stack overflow");
        } else {
            frameStack[++frameStackPtr] = currentOffset;
        }
    }

    public void endFrameVarSize() {
        if (frameStackPtr < 0) throw new RuntimeException("Stack empty");
        else writeFrameSize(currentOffset - frameStack[frameStackPtr--]);
    }

    public void writeFrameSize(int i) {
        buffer[currentOffset - i - 1] = (byte) i;
    }

    // --- Bit access ---

    public void initBitAccess() {
        bitPosition = currentOffset * 8;
    }

    public void writeBits(int numBits, int value) {
        ensureCapacity((int) Math.ceil(numBits / 8.0));
        int bytePos = bitPosition >> 3;
        int bitOffset = 8 - (bitPosition & 7);
        bitPosition += numBits;

        for (; numBits > bitOffset; bitOffset = 8) {
            buffer[bytePos] &= ~bitMaskOut[bitOffset];
            buffer[bytePos++] |= (value >> (numBits - bitOffset)) & bitMaskOut[bitOffset];
            numBits -= bitOffset;
        }
        if (numBits == bitOffset) {
            buffer[bytePos] &= ~bitMaskOut[bitOffset];
            buffer[bytePos] |= value & bitMaskOut[bitOffset];
        } else {
            buffer[bytePos] &= ~(bitMaskOut[numBits] << (bitOffset - numBits));
            buffer[bytePos] |= (value & bitMaskOut[numBits]) << (bitOffset - numBits);
        }
    }

    public void finishBitAccess() {
        currentOffset = (bitPosition + 7) / 8;
    }

    // --- Buffer management ---

    /**
     * Ensures that the buffer has enough room for the next write operation.
     */
    public void ensureCapacity(int len) {
        if (buffer == null) {
            buffer = new byte[Math.max(Config.BUFFER_SIZE, len)];
        } else if ((currentOffset + len + 1) >= buffer.length) {
            int newLength = Math.max(buffer.length * 2, buffer.length + len + 1);
            byte[] newBuffer = new byte[newLength];
            System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
            buffer = newBuffer;
        }
    }

    public void reset() {
        if (currentOffset > Config.BUFFER_SIZE) {
            byte[] oldBuffer = buffer;
            buffer = new byte[Config.BUFFER_SIZE];
            System.arraycopy(oldBuffer, 0, buffer, 0, Math.min(currentOffset, Config.BUFFER_SIZE));
        }
        currentOffset = 0;
    }

    // --- You can add more modern convenience methods here as needed ---
}
