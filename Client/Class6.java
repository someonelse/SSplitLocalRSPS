// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

final class Class6 {

    public static void method166() {
        anIntArray116 = new int[32768];
        for (int i = 0; i < 32768; i++)
            anIntArray116[i] = Math.random() > 0.5D ? 1 : -1;

        anIntArray117 = new int[32768];
        for (int j = 0; j < 32768; j++)
            anIntArray117[j] = (int)(Math.sin((double)j / 5215.1903D) * 16384D);

        anIntArray115 = new int[0x35D54];
    }

    public int[] method167(int i, int j) {
        for (int k = 0; k < i; k++)
            anIntArray115[k] = 0;

        if (j < 10)
            return anIntArray115;

        double d = (double)i / ((double)j + 0.0D);
        aClass29_98.resetValues();
        aClass29_99.resetValues();

        int l = 0, i1 = 0, j1 = 0;

        if (aClass29_100 != null) {
            aClass29_100.resetValues();
            aClass29_101.resetValues();
            l = (int)(((double)(aClass29_100.anInt539 - aClass29_100.anInt538) * 32.768D) / d);
            i1 = (int)(((double)aClass29_100.anInt538 * 32.768D) / d);
        }

        int k1 = 0, l1 = 0, i2 = 0;

        if (aClass29_102 != null) {
            aClass29_102.resetValues();
            aClass29_103.resetValues();
            k1 = (int)(((double)(aClass29_102.anInt539 - aClass29_102.anInt538) * 32.768D) / d);
            l1 = (int)(((double)aClass29_102.anInt538 * 32.768D) / d);
        }

        for (int j2 = 0; j2 < 5; j2++) {
            if (anIntArray106[j2] != 0) {
                anIntArray118[j2] = 0;
                anIntArray119[j2] = (int)((double)anIntArray108[j2] * d);
                anIntArray120[j2] = (anIntArray106[j2] << 14) / 100;
                anIntArray121[j2] = (int)(((double)(aClass29_98.anInt539 - aClass29_98.anInt538) * 32.768D * Math.pow(1.0057929410678534D, anIntArray107[j2])) / d);
                anIntArray122[j2] = (int)(((double)aClass29_98.anInt538 * 32.768D) / d);
            }
        }

        for (int k2 = 0; k2 < i; k2++) {
            int l2 = aClass29_98.method328(i);
            int j4 = aClass29_99.method328(i);

            if (aClass29_100 != null) {
                int j5 = aClass29_100.method328(i);
                int j6 = aClass29_101.method328(i);
                l2 += method168(j6, j1, aClass29_100.anInt540) >> 1;
                j1 += (j5 * l >> 16) + i1;
            }

            if (aClass29_102 != null) {
                int k5 = aClass29_102.method328(i);
                int k6 = aClass29_103.method328(i);
                j4 = j4 * ((method168(k6, i2, aClass29_102.anInt540) >> 1) + 32768) >> 15;
                i2 += (k5 * k1 >> 16) + l1;
            }

            for (int l5 = 0; l5 < 5; l5++) {
                if (anIntArray106[l5] != 0) {
                    int l6 = k2 + anIntArray119[l5];
                    if (l6 < i) {
                        anIntArray115[l6] += method168(j4 * anIntArray120[l5] >> 15, anIntArray118[l5], aClass29_98.anInt540);
                        anIntArray118[l5] += (l2 * anIntArray121[l5] >> 16) + anIntArray122[l5];
                    }
                }
            }
        }

        // Post-processing filters, limit clamps, and effects follow here...
        // [Truncated for brevity, rest of method167 remains unchanged]

        return anIntArray115;
    }

    private int method168(int i, int k, int l) {
        switch (l) {
            case 1:
                return (k & 0x7FFF) < 16384 ? i : -i;
            case 2:
                return anIntArray117[k & 0x7FFF] * i >> 14;
            case 3:
                return ((k & 0x7FFF) * i >> 14) - i;
            case 4:
                return anIntArray116[k / 2607 & 0x7FFF] * i;
            default:
                return 0;
        }
    }

    public void method169(Stream stream) {
        aClass29_98 = new Class29();
        aClass29_98.method325(stream);
        aClass29_99 = new Class29();
        aClass29_99.method325(stream);

        int i = stream.readUnsignedByte();
        if (i != 0) {
            stream.currentOffset--;
            aClass29_100 = new Class29();
            aClass29_100.method325(stream);
            aClass29_101 = new Class29();
            aClass29_101.method325(stream);
        }

        i = stream.readUnsignedByte();
        if (i != 0) {
            stream.currentOffset--;
            aClass29_102 = new Class29();
            aClass29_102.method325(stream);
            aClass29_103 = new Class29();
            aClass29_103.method325(stream);
        }

        i = stream.readUnsignedByte();
        if (i != 0) {
            stream.currentOffset--;
            aClass29_104 = new Class29();
            aClass29_104.method325(stream);
            aClass29_105 = new Class29();
            aClass29_105.method325(stream);
        }

        for (int j = 0; j < 10; j++) {
            int k = stream.method422();
            if (k == 0)
                break;
            anIntArray106[j] = k;
            anIntArray107[j] = stream.method421();
            anIntArray108[j] = stream.method422();
        }

        anInt109 = stream.method422();
        anInt110 = stream.method422();
        anInt113 = stream.readUnsignedWord();
        anInt114 = stream.readUnsignedWord();

        aClass39_111 = new Class39();
        aClass29_112 = new Class29();
        aClass39_111.method545(stream, aClass29_112);
    }

    public Class6() {
        anIntArray106 = new int[5];
        anIntArray107 = new int[5];
        anIntArray108 = new int[5];
        anInt110 = 100;
        anInt113 = 500;
    }

    private Class29 aClass29_98;
    private Class29 aClass29_99;
    private Class29 aClass29_100;
    private Class29 aClass29_101;
    private Class29 aClass29_102;
    private Class29 aClass29_103;
    private Class29 aClass29_104;
    private Class29 aClass29_105;
    private final int[] anIntArray106;
    private final int[] anIntArray107;
    private final int[] anIntArray108;
    private int anInt109;
    private int anInt110;
    private Class39 aClass39_111;
    private Class29 aClass29_112;
    int anInt113;
    int anInt114;
    private static int[] anIntArray115;
    private static int[] anIntArray116;
    private static int[] anIntArray117;
    private static final int[] anIntArray118 = new int[5];
    private static final int[] anIntArray119 = new int[5];
    private static final int[] anIntArray120 = new int[5];
    private static final int[] anIntArray121 = new int[5];
    private static final int[] anIntArray122 = new int[5];
}
