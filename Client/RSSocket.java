// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

final class RSSocket implements Runnable {

	public RSSocket(RSApplet rsApplet, Socket socket) throws IOException {
		this.closed = false;
		this.isWriter = false;
		this.hasIOError = false;
		this.rsApplet = rsApplet;
		this.socket = socket;

		socket.setSoTimeout(30000);
		socket.setTcpNoDelay(true);
		this.inputStream = socket.getInputStream();
		this.outputStream = socket.getOutputStream();
	}

	public void close() {
		closed = true;
		try {
			if (inputStream != null)
				inputStream.close();
			if (outputStream != null)
				outputStream.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			System.out.println("Error closing stream");
		}
		isWriter = false;
		synchronized (this) {
			notify();
		}
		buffer = null;
	}

	public int read() throws IOException {
		if (closed)
			return 0;
		return inputStream.read();
	}

	public int available() throws IOException {
		if (closed)
			return 0;
		return inputStream.available();
	}

	public void flushInputStream(byte[] abyte0, int j) throws IOException {
		int i = 0; // was parameter
		if (closed)
			return;
		int k;
		while (j > 0) {
			k = inputStream.read(abyte0, i, j);
			if (k <= 0)
				throw new IOException("EOF");
			i += k;
			j -= k;
		}
	}

	public void queueBytes(int i, byte[] abyte0) throws IOException {
		if (closed)
			return;
		if (hasIOError) {
			hasIOError = false;
			throw new IOException("Error in writer thread");
		}
		if (buffer == null)
			buffer = new byte[5000];
		synchronized (this) {
			for (int l = 0; l < i; l++) {
				buffer[buffIndex] = abyte0[l];
				buffIndex = (buffIndex + 1) % 5000;
				if (buffIndex == (writeIndex + 4900) % 5000)
					throw new IOException("buffer overflow");
			}
			if (!isWriter) {
				isWriter = true;
				rsApplet.startRunnable(this, 3);
			}
			notify();
		}
	}

	@Override
	public void run() {
		while (isWriter) {
			int i;
			int j;
			synchronized (this) {
				if (buffIndex == writeIndex) {
					try {
						wait();
					} catch (InterruptedException ignored) {
					}
				}
				if (!isWriter)
					return;
				j = writeIndex;
				if (buffIndex >= writeIndex)
					i = buffIndex - writeIndex;
				else
					i = 5000 - writeIndex;
			}
			if (i > 0) {
				try {
					outputStream.write(buffer, j, i);
				} catch (IOException e) {
					hasIOError = true;
				}
				writeIndex = (writeIndex + i) % 5000;
				try {
					if (buffIndex == writeIndex)
						outputStream.flush();
				} catch (IOException e) {
					hasIOError = true;
				}
			}
		}
	}

	public void printDebug() {
		System.out.println("dummy:" + closed);
		System.out.println("tcycl:" + writeIndex);
		System.out.println("tnum:" + buffIndex);
		System.out.println("writer:" + isWriter);
		System.out.println("ioerror:" + hasIOError);
		try {
			System.out.println("available:" + available());
		} catch (IOException ignored) {
		}
	}

	private final InputStream inputStream;
	private final OutputStream outputStream;
	private final Socket socket;
	private final RSApplet rsApplet;

	private byte[] buffer;
	private int writeIndex;
	private int buffIndex;
	private boolean closed;
	private boolean isWriter;
	private boolean hasIOError;
}
