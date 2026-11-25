package acgt;

/**
 * The algorithm from the paper "Subsequence Pattern Matching with Segment
 * Number Constraints" by Yuki Yonemoto, Takuya Mieno, Shunsuke Inenaga, Ryo
 * Yoshinaka, and Ayumi Shinohara.
 *
 * @author Zdenek Tronicek
 */
public class SegE {

    public static int search(String text, String pattern) {
        int tlen = text.length();
        int plen = pattern.length();
        int[] d = new int[plen + 1];
        int[] e = new int[plen + 1];
        for (int j = 1; j <= plen; j++) {
            d[j] = e[j] = plen + 1;
        }
        int seg = plen;
        for (int i = 0; i < tlen; i++) {
            char c = text.charAt(i);
            int[] prevD = d;
            int[] prevE = e;
            d = new int[plen + 1];
            e = new int[plen + 1];
            for (int j = 1; j <= plen; j++) {
                d[j] = min(prevD[j], prevE[j] + 1);
            }
            for (int j = 1; j <= plen; j++) {
                if (pattern.charAt(j - 1) == c) {
                    e[j] = min(prevE[j - 1], d[j]);
                } else {
                    e[j] = d[j];
                }
            }
            if (e[plen] < seg) {
                seg = e[plen];
            }
        }
        return seg + 1;
    }

    private static int min(int a, int b) {
        return (a < b) ? a : b;
    }

}
