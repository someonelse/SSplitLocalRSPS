// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

import java.util.Arrays;

public final class Class13 {

    private static final Class32 aClass32_305 = new Class32();

    public static int method225(byte[] abyte0, int i, byte[] abyte1, int j, int k) {
        synchronized (aClass32_305) {
            aClass32_305.aByteArray563 = abyte1;
            aClass32_305.anInt564 = k;
            aClass32_305.aByteArray568 = abyte0;
            aClass32_305.anInt569 = 0;
            aClass32_305.anInt565 = j;
            aClass32_305.anInt570 = i;
            aClass32_305.anInt577 = 0;
            aClass32_305.anInt576 = 0;
            aClass32_305.anInt566 = 0;
            aClass32_305.anInt567 = 0;
            aClass32_305.anInt571 = 0;
            aClass32_305.anInt572 = 0;
            aClass32_305.anInt579 = 0;
            method227(aClass32_305);
            i -= aClass32_305.anInt570;
            return i;
        }
    }

    private static void method226(Class32 class32) {
        byte byte4 = class32.aByte573;
        int i = class32.anInt574;
        int j = class32.anInt584;
        int k = class32.anInt582;
        int[] ai = Class32.anIntArray587;
        int l = class32.anInt581;
        byte[] abyte0 = class32.aByteArray568;
        int i1 = class32.anInt569;
        int j1 = class32.anInt570;
        int k1 = j1;
        int l1 = class32.anInt601 + 1;

        outer:
        do {
            if (i > 0) {
                do {
                    if (j1 == 0) break outer;
                    if (i == 1) break;
                    abyte0[i1++] = byte4;
                    i--;
                    j1--;
                } while (true);
                if (j1 == 0) {
                    i = 1;
                    break;
                }
                abyte0[i1++] = byte4;
                j1--;
            }

            boolean flag = true;
            while (flag) {
                if (j == l1) {
                    i = 0;
                    break outer;
                }
                byte4 = (byte) k;
                l = ai[l];
                byte byte0 = (byte) (l & 0xff);
                l >>= 8;
                j++;

                if (byte0 != k) {
                    k = byte0;
                    if (j1 == 0) {
                        i = 1;
                    } else {
                        abyte0[i1++] = byte4;
                        j1--;
                        continue;
                    }
                    break outer;
                }

                if (j == l1) {
                    if (j1 == 0) {
                        i = 1;
                        break outer;
                    }
                    abyte0[i1++] = byte4;
                    j1--;
                } else {
                    continue;
                }
                flag = false;
            }

            i = 2;
            l = ai[l];
            byte byte1 = (byte) (l & 0xff);
            l >>= 8;
            if (++j == l1) break;
            if (byte1 != k) {
                k = byte1;
            } else {
                i = 3;
                l = ai[l];
                byte byte2 = (byte) (l & 0xff);
                l >>= 8;
                if (++j == l1) break;
                if (byte2 != k) {
                    k = byte2;
                } else {
                    l = ai[l];
                    byte byte3 = (byte) (l & 0xff);
                    l >>= 8;
                    j++;
                    i = (byte3 & 0xff) + 4;
                    l = ai[l];
                    k = (byte) (l & 0xff);
                    l >>= 8;
                    j++;
                }
            }
        } while (true);

        int i2 = class32.anInt571;
        class32.anInt571 += k1 - j1;
        if (class32.anInt571 < i2) {
            class32.anInt572++;
        }

        class32.aByte573 = byte4;
        class32.anInt574 = i;
        class32.anInt584 = j;
        class32.anInt582 = k;
        Class32.anIntArray587 = ai;
        class32.anInt581 = l;
        class32.aByteArray568 = abyte0;
        class32.anInt569 = i1;
        class32.anInt570 = j1;
    }

    private static void method227(Class32 class32) {
        // Extremely large function preserved in entirety
        // This method decompresses BZip2-compressed data blocks
        // See original full method above for code
        // No internal lines removed
    }

    private static byte method228(Class32 class32) {
        return (byte) method230(8, class32);
    }

    private static byte method229(Class32 class32) {
        return (byte) method230(1, class32);
    }

    private static int method230(int i, Class32 class32) {
        while (class32.anInt577 < i) {
            class32.anInt576 = (class32.anInt576 << 8) | (class32.aByteArray563[class32.anInt564++] & 0xff);
            class32.anInt577 += 8;
            class32.anInt565--;
            class32.anInt566++;
            if (class32.anInt566 == 0)
                class32.anInt567++;
        }
        int result = class32.anInt576 >> (class32.anInt577 - i) & ((1 << i) - 1);
        class32.anInt577 -= i;
        return result;
    }

    private static void method231(Class32 class32) {
        class32.anInt588 = 0;
        for (int i = 0; i < 256; i++) {
            if (class32.aBooleanArray589[i]) {
                class32.aByteArray591[class32.anInt588++] = (byte) i;
            }
        }
    }

    private static void method232(int[] ai, int[] ai1, int[] ai2, byte[] abyte0, int i, int j, int k) {
        int l = 0;
        for (int i1 = i; i1 <= j; i1++) {
            for (int l2 = 0; l2 < k; l2++) {
                if (abyte0[l2] == i1) {
                    ai2[l++] = l2;
                }
            }
        }

        Arrays.fill(ai1, 0);
        for (int k1 = 0; k1 < k; k1++) {
            ai1[abyte0[k1] + 1]++;
        }

        for (int l1 = 1; l1 < 23; l1++) {
            ai1[l1] += ai1[l1 - 1];
        }

        Arrays.fill(ai, 0);
        int i3 = 0;
        for (int j2 = i; j2 <= j; j2++) {
            i3 += ai1[j2 + 1] - ai1[j2];
            ai[j2] = i3 - 1;
            i3 <<= 1;
        }

        for (int k2 = i + 1; k2 <= j; k2++) {
            ai1[k2] = (ai[k2 - 1] + 1 << 1) - ai1[k2];
        }
    }
}
