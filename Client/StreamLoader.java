import java.io.InputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

final class StreamLoader {

    private final byte[] aByteArray726;
    private final int dataSize;
    private final int[] anIntArray728;
    private final int[] anIntArray729;
    private final int[] anIntArray730;
    private final int[] anIntArray731;
    private final boolean aBoolean732;

    public StreamLoader(byte[] abyte0) {
        Stream stream = new Stream(abyte0);
        int i = stream.read3Bytes();
        int j = stream.read3Bytes();

        if (j != i) {
            byte[] abyte1 = new byte[i];
            Class13.method225(abyte1, i, abyte0, j, 6);
            this.aByteArray726 = abyte1;
            stream = new Stream(aByteArray726);
            this.aBoolean732 = true;
        } else {
            this.aByteArray726 = abyte0;
            this.aBoolean732 = false;
        }

        this.dataSize = stream.readUnsignedWord();
        this.anIntArray728 = new int[dataSize];
        this.anIntArray729 = new int[dataSize];
        this.anIntArray730 = new int[dataSize];
        this.anIntArray731 = new int[dataSize];

        int k = stream.currentOffset + dataSize * 10;

        for (int l = 0; l < dataSize; l++) {
            anIntArray728[l] = stream.readDWord();
            anIntArray729[l] = stream.read3Bytes();
            anIntArray730[l] = stream.read3Bytes();
            anIntArray731[l] = k;
            k += anIntArray730[l];
        }
    }

    public byte[] getDataForName(String s) {
        byte[] abyte0 = null;
        int i = 0;
        s = s.toUpperCase();

        for (int j = 0; j < s.length(); j++) {
            i = (i * 61 + s.charAt(j)) - 32;
        }

        if (s.equalsIgnoreCase("NPC.DAT") || s.equalsIgnoreCase("NPC.IDX")) {
            System.out.println(s + " : " + i);
        }

        for (int k = 0; k < dataSize; k++) {
            if (anIntArray728[k] == i) {
                if (abyte0 == null) {
                    abyte0 = new byte[anIntArray729[k]];
                }
                if (!aBoolean732) {
                    Class13.method225(abyte0, anIntArray729[k], aByteArray726, anIntArray730[k], anIntArray731[k]);
                    if (s.equalsIgnoreCase("NPC.DAT") || s.equalsIgnoreCase("NPC.IDX")) {
                        System.out.println("Called method225");
                    }
                } else {
                    System.arraycopy(aByteArray726, anIntArray731[k], abyte0, 0, anIntArray729[k]);
                }
                return abyte0;
            }
        }
        return null;
    }

    public byte[] getBytesFromFile(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            long length = file.length();

            if (length > Integer.MAX_VALUE) {
                throw new IOException("File is too large: " + file.getName());
            }

            byte[] bytes = new byte[(int) length];
            int offset = 0;
            int numRead;

            while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }

            return bytes;
        }
    }
}
