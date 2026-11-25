package acgt;

/**
 * Shift-And algorithm implemented on 512 bits.
 *
 * @author Zdenek Tronicek
 */
public class ShiftAnd512 {

    public static final int MAX_LENGTH = 16;
    private static final int ALPH = 4;
    private static final int SLOTS = 8;

    private static long[] vectorQ(int m) {
        long[] q = new long[SLOTS];
        setBits(q, 0, m);
        for (int i = 1; i < m; i++) {
            int pos = 2 * m * i;
            int count = m - i;
            setBits(q, pos, count);
        }
        return q;
    }

    private static long[] vectorR(int m) {
        long[] r = new long[SLOTS];
        for (int i = 1; i < m; i++) {
            int pos = m * (2 * i - 1);
            int count = m - i;
            setBits(r, pos, count);
        }
        return r;
    }

    private static long[][] vectorsH(int[] pat) {
        int m = pat.length;
        long[][] h = new long[ALPH][SLOTS];
        for (int i = 0; i < m; i++) {
            setBit(h[pat[i]], i);
        }
        long[] psi = vectorQ(pat.length);
        or(psi, vectorR(pat.length));
        int delta = 2 * m - 1;
        for (int i = 0; i < ALPH; i++) {
            if (equals0(h[i])) {
                continue;
            }
            int j = 1;
            while (j < m - 1) {
                long[] p = new long[SLOTS];
                assign(p, h[i]);
                leftShift(p, delta * j);
                or(h[i], p);
                j <<= 1;
            }
            and(h[i], psi);
        }
        return h;
    }

    private static long[][] vectorsD(int[] pat) {
        int m = pat.length;
        long[][] d = new long[ALPH][SLOTS];
        long[] rho = new long[SLOTS];
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
        long[] eta = vectorR(m);
        long[] psi = vectorQ(m);
        or(psi, eta);
        for (int i = 0; i < ALPH; i++) {
            if (equals(d[i], rho)) {
                assign(d[i], eta);
                continue;
            }
            int j = 1;
            while (j < m - 1) {
                long[] p = new long[SLOTS];
                assign(p, d[i]);
                leftShift(p, delta * j);
                or(d[i], p);
                j <<= 1;
            }
            and(d[i], psi);
        }
        return d;
    }

    private static long[] finalStates(int m) {
        long[] phi = new long[SLOTS];
        int beta = m - 1;
        int delta = 2 * m - 1;
        for (int i = 0; i < m; i++) {
            setBit(phi, beta);
            beta += delta;
        }
        return phi;
    }

    private static long[] vectorL(int m) {
        long[] r = vectorR(m);
        long[] f = finalStates(m);
        or(r, f);
        return r;
    }

    public static int search(String text, String pattern) {
        int m = pattern.length();
        if (m > MAX_LENGTH) {
            System.out.println("ShiftAnd512: pattern too large");
            return -1;
        }
        int[] pat = new int[m];
        for (int i = 0; i < pat.length; i++) {
            pat[i] = charToInt(pattern.charAt(i));
        }
        long[][] h = vectorsH(pat);
        long[][] d = vectorsD(pat);
        long[] loop = vectorL(m);
        long[] v = new long[SLOTS];
        long[] v2 = new long[SLOTS];
        long[] v3 = new long[SLOTS];
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
        long[] f = finalStates(m);
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
        System.arraycopy(from, 0, to, 0, SLOTS);
    }

    private static void setBit(long[] words, int pos) {
        int i = pos / 64;
        int j = pos % 64;
        words[i] |= (1L << j);
    }

    private static void setBits(long[] words, int pos, int count) {
        long[] p = new long[SLOTS];
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
        assert i < SLOTS;
        if (i > 0) {
            for (int k = SLOTS - 1; k >= i; k--) {
                words[k] = words[k - i];
            }
            for (int k = i - 1; k >= 0; k--) {
                words[k] = 0L;
            }
        }
        int j = shift % 64;
        if (j > 0) {
            for (int k = SLOTS - 1; k > 0; k--) {
                words[k] = (words[k] << j) | (words[k - 1] >>> (64 - j));
            }
            words[0] <<= j;
        }
    }

    private static void and(long[] words, long[] words2) {
        for (int i = 0; i < SLOTS; i++) {
            words[i] &= words2[i];
        }
    }

    private static void or(long[] words, long[] words2) {
        for (int i = 0; i < SLOTS; i++) {
            words[i] |= words2[i];
        }
    }

    private static void subtractOne(long[] words) {
        for (int i = 0; i < SLOTS; i++) {
            if (words[i] != 0) {
                words[i]--;
                break;
            }
            words[i] = ~0L;
        }
    }

    private static boolean equals0(long[] words) {
        for (int i = 0; i < SLOTS; i++) {
            if (words[i] != 0) {
                return false;
            }
        }
        return true;
    }

    private static boolean equals(long[] words, long[] words2) {
        for (int i = 0; i < SLOTS; i++) {
            if (words[i] != words2[i]) {
                return false;
            }
        }
        return true;
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
