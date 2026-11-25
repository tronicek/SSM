package acgt;

import java.math.BigInteger;

/**
 * Shift-And algorithm implemented on BigInteger.
 *
 * @author Zdenek Tronicek
 */
public class ShiftAndBigInteger {

    private static final int ALPH = 4;

    private static BigInteger[] vectorsH(int[] pat) {
        int m = pat.length;
        BigInteger[] h = new BigInteger[ALPH];
        for (int i = 0; i < h.length; i++) {
            h[i] = BigInteger.ZERO;
        }
        h[pat[0]] = BigInteger.ONE;
        for (int i = 1; i < m; i++) {
            for (int j = 0; j < i; j++) {
                int s = i + (2 * m - 1) * j;
                h[pat[i]] = h[pat[i]].setBit(s);
            }
        }
        return h;
    }

    private static BigInteger[] vectorsD(int[] pat) {
        int m = pat.length;
        int bits = m * m + (m - 1) * (m - 1);
        BigInteger p = BigInteger.ZERO
                .setBit(bits)
                .subtract(BigInteger.ONE);
        for (int i = 0; i < m; i++) {
            p = p.clearBit(i);
        }
        int s = 2 * m - 1;
        for (int i = 1; i < m; i++) {
            for (int j = 0; j < i; j++) {
                p = p.clearBit(s * i + j);
            }
        }
        BigInteger[] d = new BigInteger[ALPH];
        for (int i = 0; i < d.length; i++) {
            d[i] = BigInteger.ZERO;
        }
        for (int i = 1; i < m; i++) {
            for (int j = 0; j < ALPH; j++) {
                int k = m + i - 1;
                if (pat[i] == j) {
                    d[j] = d[j].setBit(k + m);
                } else {
                    d[j] = d[j].setBit(k);
                }
            }
        }
        for (int i = 0; i < ALPH; i++) {
            for (int j = 2; j < m; j++) {
                d[i] = d[i].or(d[i].shiftLeft(s));
            }
            d[i] = d[i].and(p);
        }
        return d;
    }

    private static BigInteger vectorL(int[] pat) {
        int m = pat.length;
        BigInteger loop = BigInteger.ZERO;
        int s = 2 * m - 1;
        for (int i = 1; i < m; i++) {
            int t = s * (i - 1) + m - 1;
            for (int j = i; j < m; j++) {
                loop = loop.setBit(t + j);
            }
        }
        for (int i = 0; i < m; i++) {
            int t = s * i + m - 1;
            loop = loop.setBit(t);
        }
        return loop;
    }

    private static BigInteger finalStates(int[] pat) {
        int m = pat.length;
        BigInteger f = BigInteger.ZERO;
        int s = 2 * m - 1;
        for (int i = 0; i < m; i++) {
            int t = s * i + m - 1;
            f = f.setBit(t);
        }
        return f;
    }

    public static int search(String text, String pattern) {
        int m = pattern.length();
        int[] pat = new int[m];
        for (int i = 0; i < pat.length; i++) {
            pat[i] = charToInt(pattern.charAt(i));
        }
        BigInteger[] h = vectorsH(pat);
        BigInteger[] d = vectorsD(pat);
        BigInteger loop = vectorL(pat);
        BigInteger v = BigInteger.ZERO;
        for (int i = 0; i < text.length(); i++) {
            int c = charToInt(text.charAt(i));
            v = v.shiftLeft(1)
                    .setBit(0)
                    .and(h[c])
                    .or(v.shiftLeft(m).and(d[c]))
                    .or(v.and(loop));
        }
        BigInteger f = finalStates(pat);
        v = v.and(f);
        if (v.equals(BigInteger.ZERO)) {
            return m + 1;
        }
        int z = v.getLowestSetBit();
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

}
