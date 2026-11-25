package binary;

/**
 * Nondeterministic finite automaton. A bit-parallel implementation on 64 bits.
 *
 * @author Zdenek Tronicek
 */
public class NFABit {

    public static final int MAX_LENGTH = 8;
    private static final int ALPH = 2;

    public static int search(String text, String pattern) {
        int m = pattern.length();
        if (m > MAX_LENGTH) {
            System.out.println("NFABit: pattern too large");
            return -1;
        }
        int[] pat = new int[m];
        for (int i = 0; i < pat.length; i++) {
            pat[i] = pattern.charAt(i) - 'a';
        }
        long[] h = new long[ALPH];
        long a = (1L << m) - 1;
        for (int i = 0; i < pat.length; i++) {
            h[pat[i]] |= a;
            a <<= m;
        }
        long[] hn = new long[ALPH];
        for (int i = 0; i < hn.length; i++) {
            hn[i] = ~h[i];
        }
        long f = ((1L << m) - 1) << (m * (m - 1));
        long v = 0, w = 0;
        for (int i = 0; i < text.length(); i++) {
            int c = text.charAt(i) - 'a';
            long v2 = (((v << m) | 1L) & h[c]) | (w & h[c]) | (v & f);
            w |= ((v << (m + 1)) & hn[c]);
            v = v2;
        }
        long e = (v >>> (m * (m - 1)));
        if (e == 0) {
            return m + 1;
        }
        return 1 + Long.numberOfTrailingZeros(e);
    }

}
