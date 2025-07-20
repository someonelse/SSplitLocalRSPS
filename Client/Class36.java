// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)

final class Class36 {

    public static void method528(byte[] data) {
        Stream stream = new Stream(data);
        stream.currentOffset = data.length - 8;
        int baseOffset = stream.readUnsignedWord();
        int dataOffset = stream.readUnsignedWord();
        int instructionDataLength = stream.readUnsignedWord();
        int skinDataLength = stream.readUnsignedWord();

        int offset = 0;
        Stream instructionStream = new Stream(data);
        instructionStream.currentOffset = offset;
        offset += baseOffset + 2;

        Stream skinStream = new Stream(data);
        skinStream.currentOffset = offset;
        offset += dataOffset;

        Stream instructionDataStream = new Stream(data);
        instructionDataStream.currentOffset = offset;
        offset += instructionDataLength;

        Stream skinDataStream = new Stream(data);
        skinDataStream.currentOffset = offset;

        Class18[] animationSkins = new Class18[500];
        for (int j = 0; j < 500; j++) {
            animationSkins[j] = new Class18();
        }

        int animationsCount = instructionStream.readUnsignedWord();
        for (int i = 0; i < animationsCount; i++) {
            int animationId = instructionStream.readUnsignedWord();
            Class36 animation = aClass36Array637[animationId] = new Class36();
            animation.aClass18_637 = animationSkins[instructionStream.readUnsignedByte()];

            int instructionsCount = instructionStream.readUnsignedByte();
            int lastOpcode = -1;
            int usedInstructions = 0;

            for (int instructionIndex = 0; instructionIndex < instructionsCount; instructionIndex++) {
                int opcode = skinStream.readUnsignedByte();
                if (opcode > 0) {
                    if (animation.anIntArray639[instructionIndex] != 0) {
                        // System.out.println("Warning: non-zero instruction index slot reused");
                    }

                    animation.anIntArray638[usedInstructions] = instructionIndex;

                    int defaultValue = 0;
                    if ((opcode & 1) != 0) {
                        defaultValue = instructionDataStream.readUnsignedWord();
                    }

                    int defaultValue1 = 0;
                    if ((opcode & 2) != 0) {
                        defaultValue1 = instructionDataStream.readUnsignedWord();
                    }

                    int defaultValue2 = 0;
                    if ((opcode & 4) != 0) {
                        defaultValue2 = instructionDataStream.readUnsignedWord();
                    }

                    animation.anIntArray639[usedInstructions] = opcode;
                    animation.anIntArray640[usedInstructions] = defaultValue;
                    animation.anIntArray641[usedInstructions] = defaultValue1;
                    animation.anIntArray642[usedInstructions] = defaultValue2;
                    usedInstructions++;
                }
            }

            animation.anInt643 = usedInstructions;
            animation.anIntArray638 = new int[usedInstructions];
            animation.anIntArray639 = new int[usedInstructions];
            animation.anIntArray640 = new int[usedInstructions];
            animation.anIntArray641 = new int[usedInstructions];
            animation.anIntArray642 = new int[usedInstructions];

            for (int k = 0; k < usedInstructions; k++) {
                animation.anIntArray638[k] = animation.anIntArray638[k];
                animation.anIntArray639[k] = animation.anIntArray639[k];
                animation.anIntArray640[k] = animation.anIntArray640[k];
                animation.anIntArray641[k] = animation.anIntArray641[k];
                animation.anIntArray642[k] = animation.anIntArray642[k];
            }
        }
    }

    public static void nullLoader() {
        aClass36Array637 = null;
    }

    private Class36() {
        // Empty constructor
    }

    static Class36[] aClass36Array637 = new Class36[4000];
    int anInt643;
    int[] anIntArray638 = new int[500];
    int[] anIntArray639 = new int[500];
    int[] anIntArray640 = new int[500];
    int[] anIntArray641 = new int[500];
    int[] anIntArray642 = new int[500];
    Class18 aClass18_637;
}
