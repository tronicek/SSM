package acgt;

/**
 * Nondeterministic finite automaton. A bit-parallel implementation on 256 bits.
 *
 * @author Zdenek Tronicek
 */
public class NFABit256 {

    public static final int MAX_LENGTH = 16;
    private static final int ALPH = 4;
    private static final int SLOTS = 4;

    public static int search(String text, String pattern) {
        int m = pattern.length();
        if (m > MAX_LENGTH) {
            System.out.println("NFABit256: pattern too large");
            return -1;
        }
        int[] pat = new int[m];
        for (int i = 0; i < pat.length; i++) {
            pat[i] = charToInt(pattern.charAt(i));
        }
        long[][] h = new long[ALPH][SLOTS];
        long[] a = new long[SLOTS];
        a[0] = 1L;
        leftShift(a, m);
        subtractOne(a);
        for (int i = 0; i < pat.length; i++) {
            or(h[pat[i]], a);
            leftShift(a, m);
        }
        long[][] hn = new long[ALPH][SLOTS];
        for (int i = 0; i < hn.length; i++) {
            assignNeg(hn[i], h[i]);
        }
        long[] ones = new long[SLOTS];
        for (int i = 0; i < m; i++) {
            int s = (m - 1) + m * i;
            setBit(ones, s);
        }
        long[] f = new long[SLOTS];
        f[0] = 1L;
        leftShift(f, m);
        subtractOne(f);
        leftShift(f, m * (m - 1));
        long[] v = new long[SLOTS];
        long[] w = new long[SLOTS];
        long[] v2 = new long[SLOTS];
        long[] v3 = new long[SLOTS];
        long[] v4 = new long[SLOTS];
        long[] v5 = new long[SLOTS];
        for (int i = 0; i < text.length(); i++) {
            int c = charToInt(text.charAt(i));
            assign(v2, v);
            leftShift(v2, m);
            v2[0] |= 1L;
            and(v2, h[c]);
            assign(v3, w);
            and(v3, h[c]);
            assign(v4, v);
            and(v4, f);
            or(v2, v3);
            or(v2, v4);
            assign(v5, v);
            leftShift(v5, m + 1);
            and(v5, hn[c]);
            or(w, v5);
            assign(v, v2);
        }
        rightShift(v, m * (m - 1));
        if (equals0(v)) {
            return m + 1;
        }
        return 1 + numberOfTrailingZeros(v);
    }

    private static int charToInt(char c) {
        return switch (c) {
            case 'a' ->
                0;
            case 'c' ->
                1;
            case 'g' ->
                2;
            case 't' ->
                3;
            default ->
                throw new AssertionError("unexpected character: " + c);
        };
    }

    private static void assign(long[] to, long[] from) {
        to[0] = from[0];
        to[1] = from[1];
        to[2] = from[2];
        to[3] = from[3];
    }

    private static void assignNeg(long[] to, long[] from) {
        to[0] = ~from[0];
        to[1] = ~from[1];
        to[2] = ~from[2];
        to[3] = ~from[3];
    }

    private static void setBit(long[] words, int pos) {
        int i = pos / 64;
        int j = pos % 64;
        words[i] |= (1L << j);
    }

    private static void leftShift(long[] words, int shift) {
        int i = shift / 64;
        assert i < SLOTS;
        switch (i) {
            case 1 -> {
                words[3] = words[2];
                words[2] = words[1];
                words[1] = words[0];
                words[0] = 0L;
            }
            case 2 -> {
                words[3] = words[1];
                words[2] = words[0];
                words[1] = words[0] = 0L;
            }
            case 3 -> {
                words[3] = words[0];
                words[2] = words[1] = words[0] = 0L;
            }
        }
        int j = shift % 64;
        if (j > 0) {
            words[3] = (words[3] << j) | (words[2] >>> (64 - j));
            words[2] = (words[2] << j) | (words[1] >>> (64 - j));
            words[1] = (words[1] << j) | (words[0] >>> (64 - j));
            words[0] <<= j;
        }
    }

    private static void rightShift(long[] words, int shift) {
        int i = shift / 64;
        assert i < SLOTS;
        switch (i) {
            case 1 -> {
                words[0] = words[1];
                words[1] = words[2];
                words[2] = words[3];
                words[3] = 0L;
            }
            case 2 -> {
                words[0] = words[2];
                words[1] = words[3];
                words[2] = words[3] = 0L;
            }
            case 3 -> {
                words[0] = words[3];
                words[1] = words[2] = words[3] = 0L;
            }
        }
        int j = shift % 64;
        if (j > 0) {
            words[0] = (words[0] >>> j) | (words[1] << (64 - j));
            words[1] = (words[1] >>> j) | (words[2] << (64 - j));
            words[2] = (words[2] >>> j) | (words[3] << (64 - j));
            words[3] >>>= j;
        }
    }

    private static void and(long[] words, long[] words2) {
        words[0] &= words2[0];
        words[1] &= words2[1];
        words[2] &= words2[2];
        words[3] &= words2[3];
    }

    private static void or(long[] words, long[] words2) {
        words[0] |= words2[0];
        words[1] |= words2[1];
        words[2] |= words2[2];
        words[3] |= words2[3];
    }

    private static void subtractOne(long[] words) {
        int i = 0;
        if (words[0] == 0) {
            words[0] = ~0L;
            i = 1;
            if (words[1] == 0) {
                words[1] = ~0L;
                i = 2;
                if (words[2] == 0) {
                    words[2] = ~0L;
                    i = 3;
                }
            }
        }
        words[i]--;
    }

    private static boolean equals0(long[] words) {
        return (words[0] | words[1] | words[2] | words[3]) == 0;
    }

    private static int numberOfTrailingZeros(long[] words) {
        for (int i = 0; i < SLOTS; i++) {
            if (words[i] != 0) {
                int z = Long.numberOfTrailingZeros(words[i]);
                return 64 * i + z;
            }
        }
        return 64 * SLOTS;
    }

}
