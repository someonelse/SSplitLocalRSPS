// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

public final class VarBit {

    public static VarBit[] cache;
    public int anInt648;
    public int anInt649;
    public int anInt650;
    private boolean aBoolean651;

    public static void unpackConfig(StreamLoader streamLoader) {
        Stream stream = new Stream(streamLoader.getDataForName("varbit.dat"));
        int cacheSize = stream.readUnsignedWord();

        if (cache == null) {
            cache = new VarBit[cacheSize];
        }

        for (int j = 0; j < cacheSize; j++) {
            if (cache[j] == null) {
                cache[j] = new VarBit();
            }
            cache[j].readValues(stream);
            if (cache[j].aBoolean651) {
                Varp.cache[cache[j].anInt648].aBoolean713 = true;
            }
        }

        if (stream.currentOffset != stream.buffer.length) {
            System.out.println("varbit load mismatch");
        }
    }

    private void readValues(Stream stream) {
        while (true) {
            int j = stream.readUnsignedByte();
            if (j == 0) {
                return;
            }

            switch (j) {
                case 1:
                    anInt648 = stream.readUnsignedWord();
                    anInt649 = stream.readUnsignedByte();
                    anInt650 = stream.readUnsignedByte();
                    break;
                case 2:
                    aBoolean651 = true;
                    break;
                case 3:
                    stream.readDWord(); // unused
                    break;
                case 4:
                    stream.readDWord(); // unused
                    break;
                case 10:
                    stream.readString(); // unused string
                    break;
                default:
                    System.out.println("Error unrecognised config code: " + j);
                    break;
            }
        }
    }

    private VarBit() {
        this.aBoolean651 = false;
    }
}
