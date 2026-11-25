package binary;

/**
 * Shift-And algorithm implemented on 64 bits.
 *
 * @author Zdenek Tronicek
 */
public class ShiftAnd {

    public static final int MAX_LENGTH = 6;
    private static final int ALPH = 2;

    private static long vectorQ(int m) {
        long psi = (1L << m) - 1;
        for (int i = 1; i < m; i++) {
            long lambda = (1L << (m - i)) - 1;
            psi |= lambda << (2 * m * i);
        }
        return psi;
    }

    private static long vectorQ2(int m) {
        if (m > 5) {
            throw new IllegalArgumentException("overflow");
        }
        long num = (1L << (2 * m * m + 2 * m - 1)) - (1L << (3 * m))
                + (1L << (2 * m - 1)) + (1L << m) - 1;
        long den = ((1L << (2 * m)) - 1) * ((1L << (2 * m - 1)) - 1);
        return num / den;
    }

    private static long vectorR(int m) {
        long psi = 0L;
        for (int i = 1; i < m; i++) {
            long lambda = (1L << (m - i)) - 1;
            psi |= lambda << (m * (2 * i - 1));
        }
        return psi;
    }

    private static long vectorR2(int m) {
        if (m > 5) {
            throw new IllegalArgumentException("overflow");
        }
        long num = (1L << (2 * m * m + m - 1)) - (1L << (4 * m - 1))
                + (1L << (3 * m - 1)) + (1L << (2 * m - 1)) - (1L << m);
        long den = ((1L << (2 * m - 1)) - 1) * ((1L << (2 * m)) - 1);
        return num / den;
    }

    private static long[] vectorsH(int[] pat) {
        int m = pat.length;
        long[] h = new long[ALPH];
        for (int i = 0; i < m; i++) {
            h[pat[i]] |= 1L << i;
        }
        long psi = vectorQ(m) | vectorR(m);
        for (int i = 0; i < ALPH; i++) {
            if (h[i] == 0) {
                continue;
            }
            int j = 1;
            while (j < m - 1) {
                h[i] |= h[i] << ((2 * m - 1) * j);
                j <<= 1;
            }
            h[i] &= psi;
        }
        return h;
    }

    private static long[] vectorsD(int[] pat) {
        int m = pat.length;
        long[] d = new long[ALPH];
        long rho = ((1L << (m - 1)) - 1) << m;
        for (int i = 0; i < ALPH; i++) {
            d[i] = rho;
        }
        for (int i = 1; i < m; i++) {
            int c = pat[i];
            d[c] ^= 1L << (m + i - 1);
            d[c] |= 1L << (2 * m + i - 1);
        }
        long eta = vectorR(m);
        long psi = vectorQ(m) | eta;
        for (int i = 0; i < ALPH; i++) {
            if (d[i] == rho) {
                d[i] = eta;
                continue;
            }
            int j = 1;
            while (j < m - 1) {
                d[i] |= d[i] << ((2 * m - 1) * j);
                j <<= 1;
            }
            d[i] &= psi;
        }
        return d;
    }

    private static long finalStates(int m) {
        long phi = 0L;
        int beta = m - 1;
        int delta = 2 * m - 1;
        for (int i = 0; i < m; i++) {
            phi |= 1L << beta;
            beta += delta;
        }
        return phi;
    }

    private static long finalStates2(int m) {
        long num = (1L << (2 * m * m - 1)) - (1L << (m - 1));
        long den = (1L << (2 * m - 1)) - 1;
        return num / den;
    }

    private static long vectorL(int m) {
        return vectorR(m) | finalStates(m);
    }

    private static long vectorL2(int[] pat) {
        int m = pat.length;
        return vectorR2(m) | finalStates2(m);
    }

    public static int search(String text, String pattern) {
        int m = pattern.length();
        if (m > MAX_LENGTH) {
            System.out.println("ShiftAnd: pattern too large");
            return -1;
        }
        int[] pat = new int[m];
        for (int i = 0; i < pat.length; i++) {
            pat[i] = pattern.charAt(i) - 'a';
        }
        long[] h = vectorsH(pat);
        long[] d = vectorsD(pat);
        long loop = vectorL(m);
        long v = 0L;
        for (int i = 0; i < text.length(); i++) {
            int c = text.charAt(i) - 'a';
            v = (((v << 1) | 1L) & h[c]) | ((v << m) & d[c]) | (v & loop);
        }
        long f = finalStates(m);
        long w = v & f;
        if (w == 0) {
            return m + 1;
        }
        int z = Long.numberOfTrailingZeros(w);
        return 1 + (z - m + 1) / (2 * m - 1);
    }

}
