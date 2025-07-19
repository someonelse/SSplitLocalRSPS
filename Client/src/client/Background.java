package client;

public final class Background extends DrawingArea {

    /** Delegate-style constructor: new Background("mod_icons", i) */
    public Background(String archiveName, int id) {
        this(client.streamLoader, archiveName, id);
    }

    /** Unpacks a specific image from a cached archive/index pair */
    public Background(StreamLoader streamLoader, String name, int index) {
        Stream dataStream = new Stream(streamLoader.getDataForName(name + ".dat"));
        Stream indexStream = new Stream(streamLoader.getDataForName("index.dat"));

        indexStream.currentOffset = dataStream.readUnsignedWord();
        anInt1456 = indexStream.readUnsignedWord();
        anInt1457 = indexStream.readUnsignedWord();

        int paletteSize = indexStream.readUnsignedByte();
        anIntArray1451 = new int[paletteSize];
        for (int i = 0; i < paletteSize - 1; i++)
            anIntArray1451[i + 1] = indexStream.read3Bytes();

        // skip to the desired image index
        for (int i = 0; i < index; i++) {
            indexStream.currentOffset += 2;
            int w = indexStream.readUnsignedWord();
            int h = indexStream.readUnsignedWord();
            dataStream.currentOffset += w * h;
            indexStream.currentOffset++;
        }

        anInt1454 = indexStream.readUnsignedByte();
        anInt1455 = indexStream.readUnsignedByte();
        anInt1452 = indexStream.readUnsignedWord();
        anInt1453 = indexStream.readUnsignedWord();
        int compression = indexStream.readUnsignedByte();
        aByteArray1450 = new byte[anInt1452 * anInt1453];

        if (compression == 0) {
            for (int i = 0; i < aByteArray1450.length; i++)
                aByteArray1450[i] = dataStream.readSignedByte();
        } else if (compression == 1) {
            for (int x = 0; x < anInt1452; x++) {
                for (int y = 0; y < anInt1453; y++)
                    aByteArray1450[x + y * anInt1452] = dataStream.readSignedByte();
            }
        }
    }

    /** Downscales the image for low-memory mode */
    public void downscale() {
        anInt1456 /= 2;
        anInt1457 /= 2;
        byte[] temp = new byte[anInt1456 * anInt1457];
        int idx = 0;

        for (int y = 0; y < anInt1453; y++) {
            for (int x = 0; x < anInt1452; x++) {
                int newX = (x + anInt1454) >> 1;
                int newY = (y + anInt1455) >> 1;
                temp[newX + newY * anInt1456] = aByteArray1450[idx++];
            }
        }

        aByteArray1450 = temp;
        anInt1452 = anInt1456;
        anInt1453 = anInt1457;
        anInt1454 = 0;
        anInt1455 = 0;
    }

    /** Expands image to fill the full drawing area */
    public void expandToFullSize() {
        if (anInt1452 == anInt1456 && anInt1453 == anInt1457)
            return;

        byte[] temp = new byte[anInt1456 * anInt1457];
        int idx = 0;

        for (int y = 0; y < anInt1453; y++) {
            for (int x = 0; x < anInt1452; x++) {
                int newX = x + anInt1454;
                int newY = y + anInt1455;
                temp[newX + newY * anInt1456] = aByteArray1450[idx++];
            }
        }

        aByteArray1450 = temp;
        anInt1452 = anInt1456;
        anInt1453 = anInt1457;
        anInt1454 = 0;
        anInt1455 = 0;
    }

    /** Flips image horizontally */
    public void flipHorizontally() {
        byte[] temp = new byte[anInt1452 * anInt1453];
        int idx = 0;
        for (int y = 0; y < anInt1453; y++) {
            for (int x = anInt1452 - 1; x >= 0; x--)
                temp[idx++] = aByteArray1450[x + y * anInt1452];
        }
        aByteArray1450 = temp;
        anInt1454 = anInt1456 - anInt1452 - anInt1454;
    }

    /** Flips image vertically */
    public void flipVertically() {
        byte[] temp = new byte[anInt1452 * anInt1453];
        int idx = 0;
        for (int y = anInt1453 - 1; y >= 0; y--) {
            for (int x = 0; x < anInt1452; x++)
                temp[idx++] = aByteArray1450[x + y * anInt1452];
        }
        aByteArray1450 = temp;
        anInt1455 = anInt1457 - anInt1453 - anInt1455;
    }

    /** Applies RGB offset to the palette */
    public void adjustPalette(int rOffset, int gOffset, int bOffset) {
        for (int i = 0; i < anIntArray1451.length; i++) {
            int r = (anIntArray1451[i] >> 16) & 0xff;
            int g = (anIntArray1451[i] >> 8) & 0xff;
            int b = anIntArray1451[i] & 0xff;

            r = Math.min(255, Math.max(0, r + rOffset));
            g = Math.min(255, Math.max(0, g + gOffset));
            b = Math.min(255, Math.max(0, b + bOffset));

            anIntArray1451[i] = (r << 16) | (g << 8) | b;
        }
    }

    // ─── Fields ─────────────────────────────────────────────
    public byte[] aByteArray1450;
    public final int[] anIntArray1451;
    public int anInt1452, anInt1453;
    public int anInt1454, anInt1455;
    public int anInt1456;
    private int anInt1457;
}