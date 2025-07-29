package server.util;

/**
 * ISAAC (Indirection, Shift, Accumulate, Add, and Count) random number generator.
 * This is a Java implementation based on the C reference.
 * Suitable for cryptographic-quality random numbers.
 */
public class ISAACRandomGen {
    private static final int SIZEL = 8; // Log2 of size of rsl[] and mem[]
    private static final int SIZE = 1 << SIZEL; // Size of rsl[] and mem[]
    private static final int MASK = (SIZE - 1) << 2; // For pseudorandom lookup

    private int count; // Position in rsl[]
    private int[] rsl; // Results to the user
    private int[] mem; // Internal state

    private int a; // Accumulator
    private int b; // Last result
    private int c; // Counter, guarantees cycle is at least 2^40

    /**
     * Default constructor: creates and initializes a new instance with no seed.
     */
    public ISAACRandomGen() {
        mem = new int[SIZE];
        rsl = new int[SIZE];
        init(false);
    }

    /**
     * Seeded constructor: creates and initializes a new instance using a user-provided seed.
     * @param seed The integer array to seed with.
     */
    public ISAACRandomGen(int[] seed) {
        mem = new int[SIZE];
        rsl = new int[SIZE];
        System.arraycopy(seed, 0, rsl, 0, Math.min(seed.length, rsl.length));
        init(true);
    }

    /**
     * Get the next random integer.
     */
    public final int getNextKey() {
        if (0 == count--) {
            isaac();
            count = SIZE - 1;
        }
        return rsl[count];
    }

    /**
     * (Re)initialize this instance.
     * @param flag If true, use the seed (rsl[]) for initialization.
     */
    private void init(boolean flag) {
        int i;
        int a, b, c, d, e, f, g, h;
        a = b = c = d = e = f = g = h = 0x9e3779b9; // Golden ratio

        for (i = 0; i < 4; ++i) {
            a ^= b << 11; d += a; b += c;
            b ^= c >>> 2; e += b; c += d;
            c ^= d << 8; f += c; d += e;
            d ^= e >>> 16; g += d; e += f;
            e ^= f << 10; h += e; f += g;
            f ^= g >>> 4; a += f; g += h;
            g ^= h << 8; b += g; h += a;
            h ^= a >>> 9; c += h; a += b;
        }

        for (i = 0; i < SIZE; i += 8) {
            if (flag) {
                a += rsl[i];
                b += rsl[i + 1];
                c += rsl[i + 2];
                d += rsl[i + 3];
                e += rsl[i + 4];
                f += rsl[i + 5];
                g += rsl[i + 6];
                h += rsl[i + 7];
            }
            a ^= b << 11; d += a; b += c;
            b ^= c >>> 2; e += b; c += d;
            c ^= d << 8; f += c; d += e;
            d ^= e >>> 16; g += d; e += f;
            e ^= f << 10; h += e; f += g;
            f ^= g >>> 4; a += f; g += h;
            g ^= h << 8; b += g; h += a;
            h ^= a >>> 9; c += h; a += b;
            mem[i]     = a;
            mem[i + 1] = b;
            mem[i + 2] = c;
            mem[i + 3] = d;
            mem[i + 4] = e;
            mem[i + 5] = f;
            mem[i + 6] = g;
            mem[i + 7] = h;
        }

        if (flag) {
            for (i = 0; i < SIZE; i += 8) {
                a += mem[i];
                b += mem[i + 1];
                c += mem[i + 2];
                d += mem[i + 3];
                e += mem[i + 4];
                f += mem[i + 5];
                g += mem[i + 6];
                h += mem[i + 7];
                a ^= b << 11; d += a; b += c;
                b ^= c >>> 2; e += b; c += d;
                c ^= d << 8; f += c; d += e;
                d ^= e >>> 16; g += d; e += f;
                e ^= f << 10; h += e; f += g;
                f ^= g >>> 4; a += f; g += h;
                g ^= h << 8; b += g; h += a;
                h ^= a >>> 9; c += h; a += b;
                mem[i]     = a;
                mem[i + 1] = b;
                mem[i + 2] = c;
                mem[i + 3] = d;
                mem[i + 4] = e;
                mem[i + 5] = f;
                mem[i + 6] = g;
                mem[i + 7] = h;
            }
        }

        isaac();
        count = SIZE;
    }

    /**
     * Generate 256 pseudorandom results and store in rsl[].
     */
    private void isaac() {
        int i, x, y;
        b += ++c;
        for (i = 0; i < SIZE; ++i) {
            x = mem[i];
            switch (i & 3) {
                case 0: a ^= a << 13; break;
                case 1: a ^= a >>> 6; break;
                case 2: a ^= a << 2; break;
                case 3: a ^= a >>> 16; break;
            }
            a += mem[(i + SIZE / 2) & (SIZE - 1)];
            mem[i] = y = mem[(x & MASK) >> 2] + a + b;
            rsl[i] = b = mem[((y >> SIZEL) & MASK) >> 2] + x;
        }
    }

    /**
     * Reseed this generator (supplementary: XORs the new seed into state).
     * @param seed An integer array containing the seed.
     */
    public void supplementSeed(int[] seed) {
        for (int i = 0; i < seed.length; i++)
            mem[i % mem.length] ^= seed[i];
    }
}
