// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

public final class Varp {

    public static Varp[] cache;
    private static int anInt702;
    private static int[] anIntArray703;
    public int anInt709;
    public boolean aBoolean713;

    public static void unpackConfig(StreamLoader streamLoader) {
        Stream stream = new Stream(streamLoader.getDataForName("varp.dat"));
        anInt702 = 0;
        int cacheSize = stream.readUnsignedWord();

        if (cache == null) {
            cache = new Varp[cacheSize];
        }
        if (anIntArray703 == null) {
            anIntArray703 = new int[cacheSize];
        }

        for (int j = 0; j < cacheSize; j++) {
            if (cache[j] == null) {
                cache[j] = new Varp();
            }
            cache[j].readValues(stream, j);
        }

        if (stream.currentOffset != stream.buffer.length) {
            System.out.println("varptype load mismatch");
        }
    }

    private void readValues(Stream stream, int i) {
        while (true) {
            int j = stream.readUnsignedByte();
            if (j == 0) {
                return;
            }

            switch (j) {
                case 1:
                    stream.readUnsignedByte(); // possibly read a type flag
                    break;
                case 2:
                    stream.readUnsignedByte(); // possibly unused
                    break;
                case 3:
                    anIntArray703[anInt702++] = i;
                    break;
                case 4:
                    int dummy4 = 2; // ignored
                    break;
                case 5:
                    anInt709 = stream.readUnsignedWord();
                    break;
                case 6:
                    int dummy6 = 2; // ignored
                    break;
                case 7:
                    stream.readDWord();
                    break;
                case 8:
                    aBoolean713 = true;
                    break;
                case 10:
                    stream.readString();
                    break;
                case 11:
                    aBoolean713 = true;
                    break;
                case 12:
                    stream.readDWord();
                    break;
                case 13:
                    int dummy13 = 2; // ignored
                    break;
                default:
                    System.out.println("Error unrecognised config code: " + j);
                    break;
            }
        }
    }

    private Varp() {
        this.aBoolean713 = false;
    }
}
