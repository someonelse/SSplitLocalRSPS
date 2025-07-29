public final class ISAACRandomGen {

    private int count;
    private final int[] results;
    private final int[] memory;
    private int accumulator;
    private int lastResult;
    private int counter;

    public ISAACRandomGen(int[] seed) {
        memory = new int[256];
        results = new int[256];
        System.arraycopy(seed, 0, results, 0, seed.length);
        initializeKeySet();
    }

    public int getNextKey() {
        if (count-- == 0) {
            isaac();
            count = 255;
        }
        return results[count];
    }

    private void isaac() {
        lastResult += ++counter;

        for (int i = 0; i < 256; i++) {
            int j = memory[i];
            switch (i & 3) {
                case 0 -> accumulator ^= accumulator << 13;
                case 1 -> accumulator ^= accumulator >>> 6;
                case 2 -> accumulator ^= accumulator << 2;
                case 3 -> accumulator ^= accumulator >>> 16;
            }
            accumulator += memory[(i + 128) & 0xff];
            int k = memory[(j & 0x3fc) >> 2] + accumulator + lastResult;
            memory[i] = k;
            results[i] = lastResult = memory[(k >> 8 & 0x3fc) >> 2] + j;
        }
    }

    private void initializeKeySet() {
        int l = 0x9e3779b9;
        int i1 = l, j1 = l, k1 = l, l1 = l, i2 = l, j2 = l, k2 = l;

        for (int i = 0; i < 4; i++) {
            l ^= i1 << 11; k1 += l;   i1 += j1;
            i1 ^= j1 >>> 2; l1 += i1; j1 += k1;
            j1 ^= k1 << 8;  i2 += j1; k1 += l1;
            k1 ^= l1 >>> 16; j2 += k1; l1 += i2;
            l1 ^= i2 << 10; k2 += l1; i2 += j2;
            i2 ^= j2 >>> 4; l += i2;  j2 += k2;
            j2 ^= k2 << 8;  i1 += j2; k2 += l;
            k2 ^= l >>> 9;  j1 += k2; l += i1;
        }

        for (int j = 0; j < 256; j += 8) {
            l += results[j];     i1 += results[j + 1];
            j1 += results[j + 2]; k1 += results[j + 3];
            l1 += results[j + 4]; i2 += results[j + 5];
            j2 += results[j + 6]; k2 += results[j + 7];

            l ^= i1 << 11; k1 += l;   i1 += j1;
            i1 ^= j1 >>> 2; l1 += i1; j1 += k1;
            j1 ^= k1 << 8;  i2 += j1; k1 += l1;
            k1 ^= l1 >>> 16; j2 += k1; l1 += i2;
            l1 ^= i2 << 10; k2 += l1; i2 += j2;
            i2 ^= j2 >>> 4; l += i2;  j2 += k2;
            j2 ^= k2 << 8;  i1 += j2; k2 += l;
            k2 ^= l >>> 9;  j1 += k2; l += i1;

            memory[j] = l;        memory[j + 1] = i1;
            memory[j + 2] = j1;   memory[j + 3] = k1;
            memory[j + 4] = l1;   memory[j + 5] = i2;
            memory[j + 6] = j2;   memory[j + 7] = k2;
        }

        for (int k = 0; k < 256; k += 8) {
            l += memory[k];       i1 += memory[k + 1];
            j1 += memory[k + 2];  k1 += memory[k + 3];
            l1 += memory[k + 4];  i2 += memory[k + 5];
            j2 += memory[k + 6];  k2 += memory[k + 7];

            l ^= i1 << 11; k1 += l;   i1 += j1;
            i1 ^= j1 >>> 2; l1 += i1; j1 += k1;
            j1 ^= k1 << 8;  i2 += j1; k1 += l1;
            k1 ^= l1 >>> 16; j2 += k1; l1 += i2;
            l1 ^= i2 << 10; k2 += l1; i2 += j2;
            i2 ^= j2 >>> 4; l += i2;  j2 += k2;
            j2 ^= k2 << 8;  i1 += j2; k2 += l;
            k2 ^= l >>> 9;  j1 += k2; l += i1;

            memory[k] = l;        memory[k + 1] = i1;
            memory[k + 2] = j1;   memory[k + 3] = k1;
            memory[k + 4] = l1;   memory[k + 5] = i2;
            memory[k + 6] = j2;   memory[k + 7] = k2;
        }

        isaac();
        count = 256;
    }
}
