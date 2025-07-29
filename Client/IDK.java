public final class IDK {

    public static int length;
    public static IDK[] cache;

    public int anInt657;
    public boolean aBoolean662;

    private int[] anIntArray658;
    private final int[] anIntArray659;
    private final int[] anIntArray660;
    private final int[] anIntArray661 = { -1, -1, -1, -1, -1 };

    private IDK() {
        anInt657 = -1;
        anIntArray659 = new int[6];
        anIntArray660 = new int[6];
        aBoolean662 = false;
    }

    public static void unpackConfig(StreamLoader streamLoader) {
        Stream stream = new Stream(streamLoader.getDataForName("idk.dat"));
        length = stream.readUnsignedWord();
        if (cache == null)
            cache = new IDK[length];

        for (int j = 0; j < length; j++) {
            if (cache[j] == null)
                cache[j] = new IDK();
            cache[j].readValues(stream);
        }
    }

    private void readValues(Stream stream) {
        while (true) {
            int i = stream.readUnsignedByte();
            if (i == 0)
                return;

            if (i == 1) {
                anInt657 = stream.readUnsignedByte();
            } else if (i == 2) {
                int j = stream.readUnsignedByte();
                anIntArray658 = new int[j];
                for (int k = 0; k < j; k++)
                    anIntArray658[k] = stream.readUnsignedWord();
            } else if (i == 3) {
                aBoolean662 = true;
            } else if (i >= 40 && i < 50) {
                anIntArray659[i - 40] = stream.readUnsignedWord();
            } else if (i >= 50 && i < 60) {
                anIntArray660[i - 50] = stream.readUnsignedWord();
            } else if (i >= 60 && i < 70) {
                anIntArray661[i - 60] = stream.readUnsignedWord();
            } else {
                System.out.println("Error unrecognised config code: " + i);
            }
        }
    }

    public boolean method537() {
        if (anIntArray658 == null)
            return true;
        for (int id : anIntArray658) {
            if (!Model.method463(id))
                return false;
        }
        return true;
    }

    public Model method538() {
        if (anIntArray658 == null)
            return null;

        Model[] models = new Model[anIntArray658.length];
        for (int i = 0; i < anIntArray658.length; i++)
            models[i] = Model.method462(anIntArray658[i]);

        Model model = (models.length == 1) ? models[0] : new Model(models.length, models);

        for (int j = 0; j < 6; j++) {
            if (anIntArray659[j] == 0)
                break;
            model.method476(anIntArray659[j], anIntArray660[j]);
        }

        return model;
    }

    public boolean method539() {
        for (int id : anIntArray661) {
            if (id != -1 && !Model.method463(id))
                return false;
        }
        return true;
    }

    public Model method540() {
        Model[] models = new Model[5];
        int count = 0;

        for (int id : anIntArray661) {
            if (id != -1)
                models[count++] = Model.method462(id);
        }

        Model model = new Model(count, models);

        for (int j = 0; j < 6; j++) {
            if (anIntArray659[j] == 0)
                break;
            model.method476(anIntArray659[j], anIntArray660[j]);
        }

        return model;
    }
}
