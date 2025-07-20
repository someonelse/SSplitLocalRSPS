package server.net;

import org.apache.mina.common.IoSession;

/**
 * Immutable packet object.
 * @author Graham
 */
public final class Packet {
	
	public static enum Size { Fixed, VariableByte, VariableShort };
	
	private IoSession session;
	private int pID;
	private int pLength;
	private byte[] pData;
	private int caret = 0;
	private boolean bare;
	private Size size = Size.Fixed;
	
	public Packet(IoSession session, int pID, byte[] pData, boolean bare, Size s) {
		this.session = session;
		this.pID = pID;
		this.pData = pData;
		this.pLength = pData.length;
		this.bare = bare;
		this.size = s;
	}

	/**
	 * Creates a new packet with the specified parameters.
	 */
	public Packet(IoSession session, int pID, byte[] pData, boolean bare) {
		this(session, pID, pData, bare, Size.Fixed);
	}

	/**
	 * Creates a new packet with the specified parameters. The packet
	 * is considered not to be a bare packet.
	 */
	public Packet(IoSession session, int pID, byte[] pData) {
		this(session, pID, pData, false);
	}

	/** Returns the IO session associated with the packet, if any. */
	public IoSession getSession() {
		return session;
	}

	/** Whether this packet is a bare packet. */
	public boolean isBare() {
		return bare;
	}

	public Size getSize() {
		return size;
	}

	/** Returns the packet ID. */
	public int getId() {
		return pID;
	}

	/** Returns the length of the payload of this packet. */
	public int getLength() {
		return pLength;
	}

	/** Returns the entire payload data of this packet. */
	public byte[] getData() {
		return pData;
	}

	/** Returns the remaining payload data (not yet read). */
	public byte[] getRemainingData() {
		byte[] data = new byte[pLength - caret];
		for (int i = 0; i < data.length; i++) {
			data[i] = pData[i + caret];
		}
		caret += data.length;
		return data;
	}

	/** Reads the next byte from the payload. */
	public byte readByte() {
		return pData[caret++];
	}

	/** Reads the next short from the payload (big endian). */
	public short readShort() {
		return (short) (((pData[caret++] & 0xff) << 8) | (pData[caret++] & 0xff));
	}
	
	public int readLEShortA() {
		int i = ((pData[caret++] - 128 & 0xff)) + ((pData[caret++] & 0xff) << 8);
		if (i > 32767)
			i -= 0x10000;
		return i;
	}
	
	public int readLEShort() {
		int i = ((pData[caret++] & 0xff)) + ((pData[caret++] & 0xff) << 8);
		if (i > 32767)
			i -= 0x10000;
		return i;
	}

	/** Reads the next int from the payload (big endian). */
	public int readInt() {
		return ((pData[caret++] & 0xff) << 24)
			 | ((pData[caret++] & 0xff) << 16)
			 | ((pData[caret++] & 0xff) << 8)
			 | (pData[caret++] & 0xff);
	}
	
	public int readLEInt() {
		return (pData[caret++] & 0xff)
			 | ((pData[caret++] & 0xff) << 8)
			 | ((pData[caret++] & 0xff) << 16)
			 | ((pData[caret++] & 0xff) << 24);
	}

	/** Reads the next long from the payload. */
	public long readLong() {
		return ((long) (pData[caret++] & 0xff) << 56)
			 | ((long) (pData[caret++] & 0xff) << 48)
			 | ((long) (pData[caret++] & 0xff) << 40)
			 | ((long) (pData[caret++] & 0xff) << 32)
			 | ((long) (pData[caret++] & 0xff) << 24)
			 | ((long) (pData[caret++] & 0xff) << 16)
			 | ((long) (pData[caret++] & 0xff) << 8)
			 | ((pData[caret++] & 0xff));
	}

	/** Reads a string using the unread portion of the payload. */
	public String readString() {
		return readString(pLength - caret);
	}

	/** Reads a RuneScape-terminated string (ends with byte 0). */
	public String readRS2String() {
		int start = caret;
		while (pData[caret++] != 0);
		return new String(pData, start, caret - start - 1);
	}
	
	/** Reads bytes into a buffer. */
	public void readBytes(byte[] buf, int off, int len) {
		for (int i = 0; i < len; i++) {
			buf[off + i] = pData[caret++];
		}
	}

	/** Reads a string of the specified length from the payload. */
	public String readString(int length) {
		String rv = new String(pData, caret, length);
		caret += length;
		return rv;
	}

	/** Skips the specified number of bytes in the payload. */
	public void skip(int x) {
		caret += x;
	}

	public int remaining() {
		return pData.length - caret;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[id=").append(pID)
		  .append(",len=").append(pLength)
		  .append(",data=0x");
		for (int x = 0; x < pLength; x++) {
			sb.append(byteToHex(pData[x], true));
		}
		sb.append("]");
		return sb.toString();
	}
	
	private static String byteToHex(byte b, boolean forceLeadingZero) {
		StringBuilder out = new StringBuilder();
		int ub = b & 0xff;
		if (ub / 16 > 0 || forceLeadingZero)
			out.append(hex[ub / 16]);
		out.append(hex[ub % 16]);
		return out.toString();
	}
	
	private static final char[] hex = "0123456789ABCDEF".toCharArray();

	public int readShortA() {
		caret += 2;
		return ((pData[caret - 2] & 0xFF) << 8) + (pData[caret - 1] - 128 & 0xFF);
	}

	public byte readByteC() {
		return (byte) -readByte();
	}

	public byte readByteS() {
		return (byte) (128 - readByte());
	}
}
