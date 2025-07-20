import java.io.*;

public class FileOperations {

	public FileOperations() {
	}

	public static int TotalRead = 0;
	public static int TotalWrite = 0;
	public static int CompleteWrite = 0;

	public static final byte[] ReadFile(String s) {
		try {
			File file = new File(s);
			int length = (int) file.length();
			byte[] data = new byte[length];
			try (DataInputStream input = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
				input.readFully(data, 0, length);
			}
			TotalRead++;
			return data;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static final void WriteFile(String s, byte[] data) {
		try {
			File file = new File(s);
			File parentDir = file.getParentFile();
			if (parentDir != null) {
				parentDir.mkdirs();
			}
			try (FileOutputStream output = new FileOutputStream(file)) {
				output.write(data, 0, data.length);
			}
			TotalWrite++;
			CompleteWrite++;
		} catch (Throwable t) {
			System.out.println("Write Error: " + s);
			t.printStackTrace();
		}
	}

	public static boolean FileExists(String file) {
		File f = new File(file);
		return f.exists();
	}
}
