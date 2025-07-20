// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

final class TextInput {

    private static final boolean aBoolean630 = true;
    private static final char[] aCharArray631 = new char[100];
    private static final Stream stream = new Stream(new byte[100]);

    private static final char[] validChars = {
        ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r',
        'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p',
        'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2',
        '3', '4', '5', '6', '7', '8', '9', ' ', '!', '?',
        '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\',
        '\'', '@', '#', '+', '=', '\243', '$', '%', '"', '[',
        ']'
    };

    public static String method525(int length, Stream stream) {
        int j = 0;
        int k = -1;

        for (int l = 0; l < length; l++) {
            int i1 = stream.readUnsignedByte();
            int j1 = (i1 >> 4) & 0xf;

            if (k == -1) {
                if (j1 < 13)
                    aCharArray631[j++] = validChars[j1];
                else
                    k = j1;
            } else {
                aCharArray631[j++] = validChars[((k << 4) + j1) - 195];
                k = -1;
            }

            j1 = i1 & 0xf;

            if (k == -1) {
                if (j1 < 13)
                    aCharArray631[j++] = validChars[j1];
                else
                    k = j1;
            } else {
                aCharArray631[j++] = validChars[((k << 4) + j1) - 195];
                k = -1;
            }
        }

        boolean capitalizeNext = true;
        for (int i = 0; i < j; i++) {
            char c = aCharArray631[i];
            if (capitalizeNext && c >= 'a' && c <= 'z') {
                aCharArray631[i] = (char) (c + '\uFFE0');
                capitalizeNext = false;
            }
            if (c == '.' || c == '!' || c == '?')
                capitalizeNext = true;
        }

        return new String(aCharArray631, 0, j);
    }

    public static void method526(String input, Stream stream) {
        if (input.length() > 80)
            input = input.substring(0, 80);
        
        input = input.toLowerCase();
        int previousIndex = -1;

        for (int j = 0; j < input.length(); j++) {
            char currentChar = input.charAt(j);
            int charIndex = 0;

            for (int l = 0; l < validChars.length; l++) {
                if (currentChar == validChars[l]) {
                    charIndex = l;
                    break;
                }
            }

            if (charIndex > 12)
                charIndex += 195;

            if (previousIndex == -1) {
                if (charIndex < 13)
                    previousIndex = charIndex;
                else
                    stream.writeWordBigEndian(charIndex);
            } else {
                if (charIndex < 13) {
                    stream.writeWordBigEndian((previousIndex << 4) + charIndex);
                    previousIndex = -1;
                } else {
                    stream.writeWordBigEndian((previousIndex << 4) + (charIndex >> 4));
                    previousIndex = charIndex & 0xf;
                }
            }
        }

        if (previousIndex != -1)
            stream.writeWordBigEndian(previousIndex << 4);
    }

    public static String processText(String input) {
        stream.currentOffset = 0;
        method526(input, stream);
        int length = stream.currentOffset;
        stream.currentOffset = 0;
        return method525(length, stream);
    }
}
