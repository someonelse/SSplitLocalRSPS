public final class Flo {

	public static void unpackConfig(StreamLoader streamLoader) {
		Stream stream = new Stream(streamLoader.getDataForName("flo.dat"));
		int cacheSize = stream.readUnsignedWord();
		if (cache == null)
			cache = new Flo[cacheSize];
		for (int j = 0; j < cacheSize; j++) {
			if (cache[j] == null)
				cache[j] = new Flo();
			cache[j].readValues(stream);
		}
	}

	private void readValues(Stream stream) {
		while (true) {
			int i = stream.readUnsignedByte();
			boolean dummy;
			if (i == 0)
				return;
			else if (i == 1) {
				anInt390 = stream.read3Bytes();
				method262(anInt390);
			} else if (i == 2) {
				anInt391 = stream.readUnsignedByte();
			} else if (i == 3) {
				dummy = true;
			} else if (i == 5) {
				aBoolean393 = false;
			} else if (i == 6) {
				stream.readString();
			} else if (i == 7) {
				int j = anInt394;
				int k = anInt395;
				int l = anInt396;
				int i1 = anInt397;
				int j1 = stream.read3Bytes();
				method262(j1);
				anInt394 = j;
				anInt395 = k;
				anInt396 = l;
				anInt397 = i1;
				anInt398 = i1;
			} else {
				System.out.println("Error unrecognised config code: " + i);
			}
		}
	}

	private void method262(int i) {
		double d = (double) (i >> 16 & 0xff) / 256D;
		double d1 = (double) (i >> 8 & 0xff) / 256D;
		double d2 = (double) (i & 0xff) / 256D;
		double d3 = Math.min(d, Math.min(d1, d2));
		double d4 = Math.max(d, Math.max(d1, d2));
		double d5 = 0.0D;
		double d6 = 0.0D;
		double d7 = (d3 + d4) / 2D;

		if (d3 != d4) {
			if (d7 < 0.5D)
				d6 = (d4 - d3) / (d4 + d3);
			else
				d6 = (d4 - d3) / (2D - d4 - d3);

			if (d == d4)
				d5 = (d1 - d2) / (d4 - d3);
			else if (d1 == d4)
				d5 = 2D + (d2 - d) / (d4 - d3);
			else if (d2 == d4)
				d5 = 4D + (d - d1) / (d4 - d3);
		}

		d5 /= 6D;
		anInt394 = (int) (d5 * 256D);
		anInt395 = (int) (d6 * 256D);
		anInt396 = (int) (d7 * 256D);

		anInt395 = Math.max(0, Math.min(255, anInt395));
		anInt396 = Math.max(0, Math.min(255, anInt396));

		if (d7 > 0.5D)
			anInt398 = (int) ((1.0D - d7) * d6 * 512D);
		else
			anInt398 = (int) (d7 * d6 * 512D);

		if (anInt398 < 1)
			anInt398 = 1;

		anInt397 = (int) (d5 * anInt398);

		int k = anInt394 + (int) (Math.random() * 16D) - 8;
		k = Math.max(0, Math.min(255, k));

		int l = anInt395 + (int) (Math.random() * 48D) - 24;
		l = Math.max(0, Math.min(255, l));

		int i1 = anInt396 + (int) (Math.random() * 48D) - 24;
		i1 = Math.max(0, Math.min(255, i1));

		anInt399 = method263(k, l, i1);
	}

	private int method263(int i, int j, int k) {
		if (k > 179) j /= 2;
		if (k > 192) j /= 2;
		if (k > 217) j /= 2;
		if (k > 243) j /= 2;
		return (i / 4 << 10) + (j / 32 << 7) + k / 2;
	}

	private Flo() {
		anInt391 = -1;
		aBoolean393 = true;
	}

	public static Flo[] cache;
	public int anInt390;
	public int anInt391;
	public boolean aBoolean393;
	public int anInt394;
	public int anInt395;
	public int anInt396;
	public int anInt397;
	public int anInt398;
	public int anInt399;
}
