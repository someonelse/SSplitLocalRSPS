package server.net;

/**
 * A mutable sequence of bytes used to construct immutable <code>Packet</code> objects.
 * By default, methods use big endian byte ordering.
 */
public class StaticPacketBuilder implements PacketBuilder {
    /** Default capacity */
    private static final int DEFAULT_SIZE = 32;

    /** The payload buffer */
    private byte[] payload;

    /** Current number of bytes used in the buffer */
    private int curLength;

    /** ID of the packet */
    private int id;

    /** Current index into the buffer by bits */
    private int bitPosition = 0;

    private Packet.Size size = Packet.Size.Fixed;

    /** Whether this packet does not use the standard packet header */
    private boolean bare = false;

    /** Bitmasks for <code>addBits()</code> */
    private static final int[] bitmasks = {
        0, 0x1, 0x3, 0x7,
        0xf, 0x1f, 0x3f, 0x7f,
        0xff, 0x1ff, 0x3ff, 0x7ff,
        0xfff, 0x1fff, 0x3fff, 0x7fff,
        0xffff, 0x1ffff, 0x3ffff, 0x7ffff,
        0xfffff, 0x1fffff, 0x3fffff, 0x7fffff,
        0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff,
        0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff,
        -1
    };

    /** Constructs a packet builder with no data and default capacity. */
    public StaticPacketBuilder() {
        this(DEFAULT_SIZE);
    }

    /** Constructs a packet builder with specified capacity. */
    public StaticPacketBuilder(int capacity) {
        payload = new byte[capacity];
    }

    public byte[] getPayload() {
        return payload;
    }

    /**
     * Ensures that the buffer is at least <code>minimumCapacity</code> bytes.
     */
    private void ensureCapacity(int minimumCapacity) {
        if (minimumCapacity >= payload.length) {
            expandCapacity(minimumCapacity);
        }
    }

    /**
     * Expands the buffer to at least the specified size.
     */
    private void expandCapacity(int minimumCapacity) {
        int newCapacity = (payload.length + 1) * 2;
        if (newCapacity < 0) {
            newCapacity = Integer.MAX_VALUE;
        } else if (minimumCapacity > newCapacity) {
            newCapacity = minimumCapacity;
        }
        byte[] newPayload = new byte[newCapacity];
        try {
            while (curLength > payload.length)
                curLength--;
            System.arraycopy(payload, 0, newPayload, 0, curLength);
        } catch (Exception ignored) {}
        payload = newPayload;
    }

    /**
     * Sets this packet as bare (no header).
     */
    public StaticPacketBuilder setBare(boolean bare) {
        this.bare = bare;
        return this;
    }

    /**
     * Sets the ID for this packet.
     */
    public StaticPacketBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public StaticPacketBuilder setSize(Packet.Size s) {
        size = s;
        return this;
    }

    public StaticPacketBuilder initBitAccess() {
        bitPosition = curLength * 8;
        return this;
    }

    public StaticPacketBuilder finishBitAccess() {
        curLength = (bitPosition + 7) / 8;
        return this;
    }

    /** Adds an arbitrary number of bits to the packet. */
    public StaticPacketBuilder addBits(int numBits, int value) {
        int bytePos = bitPosition >> 3;
        int bitOffset = 8 - (bitPosition & 7);
        bitPosition += numBits;
        curLength = (bitPosition + 7) / 8;
        ensureCapacity(curLength);
        for (; numBits > bitOffset; bitOffset = 8) {
            payload[bytePos] &= ~bitmasks[bitOffset];
            payload[bytePos++] |= (value >> (numBits - bitOffset)) & bitmasks[bitOffset];
            numBits -= bitOffset;
        }
        if (numBits == bitOffset) {
            payload[bytePos] &= ~bitmasks[bitOffset];
            payload[bytePos] |= value & bitmasks[bitOffset];
        } else {
            payload[bytePos] &= ~(bitmasks[numBits] << (bitOffset - numBits));
            payload[bytePos] |= (value & bitmasks[numBits]) << (bitOffset - numBits);
        }
        return this;
    }

    /** Adds a byte array to the packet. */
    public StaticPacketBuilder addBytes(byte[] data) {
        return addBytes(data, 0, data.length);
    }

    /** Adds a portion of a byte array to the packet. */
    public StaticPacketBuilder addBytes(byte[] data, int offset, int len) {
        int newLength = curLength + len;
        ensureCapacity(newLength);
        System.arraycopy(data, offset, payload, curLength, len);
        curLength = newLength;
        return this;
    }

    public StaticPacketBuilder addLEShortA(int i) {
        ensureCapacity(curLength + 2);
        addByte((byte) (i + 128), false);
        addByte((byte) (i >> 8), false);
        return this;
    }

    public StaticPacketBuilder addShortA(int i) {
        ensureCapacity(curLength + 2);
        addByte((byte) (i >> 8), false);
        addByte((byte) (i + 128), false);
        return this;
    }

    public StaticPacketBuilder addByte(byte val) {
        return addByte(val, true);
    }

    public StaticPacketBuilder addByteA(int i) {
        return addByte((byte) (i + 128), true);
    }

    private StaticPacketBuilder addByte(byte val, boolean checkCapacity) {
        if (checkCapacity)
            ensureCapacity(curLength + 1);
        payload[curLength++] = val;
        return this;
    }

    public StaticPacketBuilder addShort(int val) {
        ensureCapacity(curLength + 2);
        addByte((byte) (val >> 8), false);
        addByte((byte) val, false);
        return this;
    }

    public StaticPacketBuilder addLEShort(int val) {
        ensureCapacity(curLength + 2);
        addByte((byte) val, false);
        addByte((byte) (val >> 8), false);
        return this;
    }

    public StaticPacketBuilder setShort(int val, int offset) {
        payload[offset++] = (byte) (val >> 8);
        payload[offset++] = (byte) val;
        if (curLength < offset + 2) {
            curLength += 2;
        }
        return this;
    }

    public StaticPacketBuilder addInt(int val) {
        ensureCapacity(curLength + 4);
        addByte((byte) (val >> 24), false);
        addByte((byte) (val >> 16), false);
        addByte((byte) (val >> 8), false);
        addByte((byte) val, false);
        return this;
    }

    public StaticPacketBuilder addInt1(int val) {
        ensureCapacity(curLength + 4);
        addByte((byte) (val >> 8), false);
        addByte((byte) val, false);
        addByte((byte) (val >> 24), false);
        addByte((byte) (val >> 16), false);
        return this;
    }

    public StaticPacketBuilder addInt2(int val) {
        ensureCapacity(curLength + 4);
        addByte((byte) (val >> 16), false);
        addByte((byte) (val >> 24), false);
        addByte((byte) val, false);
        addByte((byte) (val >> 8), false);
        return this;
    }

    public StaticPacketBuilder addLEInt(int val) {
        ensureCapacity(curLength + 4);
        addByte((byte) val, false);
        addByte((byte) (val >> 8), false);
        addByte((byte) (val >> 16), false);
        addByte((byte) (val >> 24), false);
        return this;
    }

    public StaticPacketBuilder addLong(long val) {
        addInt((int) (val >> 32));
        addInt((int) (val & -1L));
        return this;
    }

    public StaticPacketBuilder addLELong(long val) {
        addLEInt((int) (val & -1L));
        addLEInt((int) (val >> 32));
        return this;
    }

    @SuppressWarnings("deprecation")
    public StaticPacketBuilder addString(String s) {
        ensureCapacity(curLength + s.length() + 1);
        s.getBytes(0, s.length(), payload, curLength);
        curLength += s.length();
        payload[curLength++] = 0;
        return this;
    }

    public int getLength() {
        return curLength;
    }

    /**
     * Returns a <code>Packet</code> object for the data contained in this builder.
     */
    public Packet toPacket() {
        byte[] data = new byte[curLength];
        System.arraycopy(payload, 0, data, 0, curLength);
        return new Packet(null, id, data, bare, size);
    }

    public StaticPacketBuilder addByteC(int val) {
        addByte((byte) -val);
        return this;
    }

    public StaticPacketBuilder addByteS(int val) {
        addByte((byte) (128 - val));
        return this;
    }
}
