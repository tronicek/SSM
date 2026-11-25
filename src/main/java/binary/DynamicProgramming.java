package binary;

/**
 * Dynamic Programming.
 *
 * @author Zdenek Tronicek
 */
public class DynamicProgramming {

    public static int search(String text, String pattern) {
        int m = pattern.length();
        int[] v = new int[m + 1];
        for (int i = 1; i < v.length; i++) {
            v[i] = m;
        }
        int[] w = new int[m];
        for (int i = 1; i < w.length; i++) {
            w[i] = m;
        }
        int seg = m;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            for (int j = m; j > 1; j--) {
                if (c == pattern.charAt(j - 1)) {
                    v[j] = min(v[j - 1], w[j - 1] + 1);
                } else {
                    v[j] = m;
                    w[j - 1] = min(v[j - 1], w[j - 1]);
                }
            }
            if (c == pattern.charAt(0)) {
                v[1] = v[0];
            } else {
                v[1] = m;
            }
            if (v[m] < seg) {
                seg = v[m];
            }
        }
        return seg + 1;
    }

    private static int min(int a, int b) {
        return (a < b) ? a : b;
    }

}
