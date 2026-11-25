package acgt;

import java.math.BigInteger;

/**
 * Nondeterministic finite automaton. A bit-parallel implementation on
 * BigInteger.
 *
 * @author Zdenek Tronicek
 */
public class NFABitBigInteger {

    private static final int ALPH = 4;

    public static int search(String text, String pattern) {
        int m = pattern.length();
        int[] pat = new int[m];
        for (int i = 0; i < pat.length; i++) {
            pat[i] = charToInt(pattern.charAt(i));
        }
        BigInteger[] h = new BigInteger[ALPH];
        for (int i = 0; i < h.length; i++) {
            h[i] = BigInteger.ZERO;
        }
        BigInteger a = BigInteger.ONE
                .shiftLeft(m)
                .subtract(BigInteger.ONE);
        for (int i = 0; i < pat.length; i++) {
            h[pat[i]] = h[pat[i]].or(a);
            a = a.shiftLeft(m);
        }
        BigInteger[] hn = new BigInteger[ALPH];
        for (int i = 0; i < hn.length; i++) {
            hn[i] = h[i].not();
        }
        BigInteger ones = BigInteger.ZERO;
        for (int i = 0; i < m; i++) {
            int s = (m - 1) + m * i;
            ones = ones.setBit(s);
        }
        BigInteger f = BigInteger.ONE
                .shiftLeft(m)
                .subtract(BigInteger.ONE)
                .shiftLeft(m * (m - 1));
        BigInteger v = BigInteger.ZERO;
        BigInteger w = BigInteger.ZERO;
        for (int i = 0; i < text.length(); i++) {
            int c = charToInt(text.charAt(i));
            BigInteger v2 = v.shiftLeft(m)
                    .or(BigInteger.ONE)
                    .and(h[c])
                    .or(w.and(h[c]))
                    .or(v.and(f));
            BigInteger v3 = v.shiftLeft(m + 1)
                    .and(hn[c]);
            w = w.or(v3);
            v = v2;
        }
        v = v.shiftRight(m * (m - 1));
        if (v.equals(BigInteger.ZERO)) {
            return m + 1;
        }
        return 1 + v.getLowestSetBit();
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
