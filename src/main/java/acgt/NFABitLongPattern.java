package acgt;

/**
 * Nondeterministic finite automaton. A bit-parallel implementation on multiple
 * computer words.
 *
 * @author Zdenek Tronicek
 */
public class NFABitLongPattern {

    private static final int ALPH = 4;

    public static int search(String text, String pattern) {
        int m = pattern.length();
        int[] pat = new int[m];
        for (int i = 0; i < pat.length; i++) {
            pat[i] = charToInt(pattern.charAt(i));
        }
        int slots = (m * m + 63) / 64;
        long[][] h = new long[ALPH][slots];
        long[] a = new long[slots];
        setBits(a, 0, m);
        for (int i = 0; i < pat.length; i++) {
            or(h[pat[i]], a);
            leftShift(a, m);
        }
        long[][] hn = new long[ALPH][slots];
        for (int i = 0; i < hn.length; i++) {
            assignNeg(hn[i], h[i]);
        }
        long[] ones = new long[slots];
        for (int i = 0; i < m; i++) {
            int s = (m - 1) + m * i;
            setBit(ones, s);
        }
        long[] f = new long[slots];
        setBits(f, m * (m - 1), m);
        long[] v = new long[slots];
        long[] w = new long[slots];
        long[] v2 = new long[slots];
        long[] v3 = new long[slots];
        long[] v4 = new long[slots];
        long[] v5 = new long[slots];
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
        System.arraycopy(from, 0, to, 0, from.length);
    }

    private static void assignNeg(long[] to, long[] from) {
        for (int i = 0; i < to.length; i++) {
            to[i] = ~from[i];
        }
    }

    private static void setBit(long[] words, int pos) {
        int i = pos / 64;
        int j = pos % 64;
        words[i] |= (1L << j);
    }

    private static void setBits(long[] words, int pos, int count) {
        words[0] = 1L;
        leftShift(words, count);
        subtractOne(words);
        leftShift(words, pos);
    }

    private static void leftShift(long[] words, int shift) {
        int i = shift / 64;
        if (i > 0) {
            for (int k = words.length - 1; k >= i; k--) {
                words[k] = words[k - i];
            }
            for (int k = i - 1; k >= 0; k--) {
                words[k] = 0L;
            }
        }
        int j = shift % 64;
        if (j > 0) {
            for (int k = words.length - 1; k > 0; k--) {
                words[k] = (words[k] << j) | (words[k - 1] >>> (64 - j));
            }
            words[0] <<= j;
        }
    }

    private static void rightShift(long[] words, int shift) {
        int i = shift / 64;
        for (int k = 0; k < words.length - i; k++) {
            words[k] = words[k + i];
        }
        for (int k = words.length - i; k < words.length; k++) {
            words[k] = 0L;
        }
        int j = shift % 64;
        if (j > 0) {
            for (int k = 0; k < words.length - 1; k++) {
                words[k] = (words[k] >>> j) | (words[k + 1] << (64 - j));
            }
            words[words.length - 1] >>>= j;
        }
    }

    private static void and(long[] words, long[] words2) {
        for (int i = 0; i < words.length; i++) {
            words[i] &= words2[i];
        }
    }

    private static void or(long[] words, long[] words2) {
        for (int i = 0; i < words.length; i++) {
            words[i] |= words2[i];
        }
    }

    private static void subtractOne(long[] words) {
        for (int i = 0; i < words.length; i++) {
            if (words[i] != 0) {
                words[i]--;
                break;
            }
            words[i] = ~0L;
        }
    }

    private static boolean equals0(long[] words) {
        for (int i = 0; i < words.length; i++) {
            if (words[i] != 0) {
                return false;
            }
        }
        return true;
    }

    private static int numberOfTrailingZeros(long[] words) {
        for (int i = 0; i < words.length; i++) {
            if (words[i] != 0) {
                int z = Long.numberOfTrailingZeros(words[i]);
                return 64 * i + z;
            }
        }
        return 64 * words.length;
    }

}
