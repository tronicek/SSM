package acgt;

/**
 * Shift-And algorithm implemented on multiple computer words.
 *
 * @author Zdenek Tronicek
 */
public class ShiftAndLongPattern {

    private static final int ALPH = 4;

    private static long[] vectorQ(int m, int slots) {
        long[] q = new long[slots];
        setBits(q, 0, m);
        for (int i = 1; i < m; i++) {
            int pos = 2 * m * i;
            int count = m - i;
            setBits(q, pos, count);
        }
        return q;
    }

    private static long[] vectorR(int m, int slots) {
        long[] r = new long[slots];
        for (int i = 1; i < m; i++) {
            int pos = m * (2 * i - 1);
            int count = m - i;
            setBits(r, pos, count);
        }
        return r;
    }

    private static long[][] vectorsH(int[] pat, int slots) {
        int m = pat.length;
        long[][] h = new long[ALPH][slots];
        for (int i = 0; i < m; i++) {
            setBit(h[pat[i]], i);
        }
        long[] psi = vectorQ(pat.length, slots);
        or(psi, vectorR(pat.length, slots));
        int delta = 2 * m - 1;
        for (int i = 0; i < ALPH; i++) {
            if (equals0(h[i])) {
                continue;
            }
            int j = 1;
            while (j < m - 1) {
                long[] p = new long[slots];
                assign(p, h[i]);
                leftShift(p, delta * j);
                or(h[i], p);
                j <<= 1;
            }
            and(h[i], psi);
        }
        return h;
    }

    private static long[][] vectorsD(int[] pat, int slots) {
        int m = pat.length;
        long[][] d = new long[ALPH][slots];
        long[] rho = new long[slots];
        setBits(rho, m, m - 1);
        int delta = 2 * m - 1;
        for (int i = 0; i < ALPH; i++) {
            assign(d[i], rho);
        }
        for (int i = 1; i < m; i++) {
            int c = pat[i];
            clearBit(d[c], m + i - 1);
            setBit(d[c], delta + i);
        }
        long[] eta = vectorR(m, slots);
        long[] psi = vectorQ(m, slots);
        or(psi, eta);
        for (int i = 0; i < ALPH; i++) {
            if (equals(d[i], rho)) {
                assign(d[i], eta);
                continue;
            }
            int j = 1;
            while (j < m - 1) {
                long[] p = new long[slots];
                assign(p, d[i]);
                leftShift(p, delta * j);
                or(d[i], p);
                j <<= 1;
            }
            and(d[i], psi);
        }
        return d;
    }

    private static long[] finalStates(int m, int slots) {
        long[] phi = new long[slots];
        int beta = m - 1;
        int delta = 2 * m - 1;
        for (int i = 0; i < m; i++) {
            setBit(phi, beta);
            beta += delta;
        }
        return phi;
    }

    private static long[] vectorL(int m, int slots) {
        long[] r = vectorR(m, slots);
        long[] f = finalStates(m, slots);
        or(r, f);
        return r;
    }

    public static int search(String text, String pattern) {
        int m = pattern.length();
        int[] pat = new int[m];
        for (int i = 0; i < pat.length; i++) {
            pat[i] = charToInt(pattern.charAt(i));
        }
        int bits = m * m + (m - 1) * (m - 1);
        int slots = bits / 64 + 1;
        long[][] h = vectorsH(pat, slots);
        long[][] d = vectorsD(pat, slots);
        long[] loop = vectorL(m, slots);
        long[] v = new long[slots];
        long[] v2 = new long[slots];
        long[] v3 = new long[slots];
        for (int i = 0; i < text.length(); i++) {
            int c = charToInt(text.charAt(i));
            assign(v2, v);
            leftShift(v2, 1);
            v2[0] |= 1L;
            and(v2, h[c]);
            assign(v3, v);
            leftShift(v3, m);
            and(v3, d[c]);
            and(v, loop);
            or(v, v2);
            or(v, v3);
        }
        long[] f = finalStates(m, slots);
        and(v, f);
        if (equals0(v)) {
            return m + 1;
        }
        int z = numberOfTrailingZeros(v);
        return 1 + (z - m + 1) / (2 * m - 1);
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
        System.arraycopy(from, 0, to, 0, to.length);
    }

    private static void setBit(long[] words, int pos) {
        int i = pos / 64;
        int j = pos % 64;
        words[i] |= (1L << j);
    }

    private static void setBits(long[] words, int pos, int count) {
        long[] p = new long[words.length];
        p[0] = 1L;
        leftShift(p, count);
        subtractOne(p);
        leftShift(p, pos);
        or(words, p);
    }

    private static void clearBit(long[] words, int pos) {
        int i = pos / 64;
        int j = pos % 64;
        words[i] &= ~(1L << j);
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

    private static void and(long[] words, long[] words2) {
        assert words.length == words2.length;
        for (int i = 0; i < words.length; i++) {
            words[i] &= words2[i];
        }
    }

    private static void or(long[] words, long[] words2) {
        assert words.length == words2.length;
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

    private static boolean equals(long[] words, long[] words2) {
        for (int i = 0; i < words.length; i++) {
            if (words[i] != words2[i]) {
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
