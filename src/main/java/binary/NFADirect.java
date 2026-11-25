package binary;

/**
 * A direct implementation of the nondeterministic finite automaton.
 *
 * @author Zdenek Tronicek
 */
public class NFADirect {

    public static int search(String text, String pattern) {
        int m = pattern.length();
        int[] count = new int[m + 1];
        boolean[] gap = new boolean[m];
        count[0] = 1;
        for (int j = 1; j <= m; j++) {
            count[j] = m + 1;
        }
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (pattern.charAt(m - 1) == c && count[m - 1] < count[m]) {
                count[m] = count[m - 1];
                if (gap[m - 1]) {
                    count[m]++;
                }
            }
            for (int j = m - 2; j >= 0; j--) {
                int k = j + 1;
                if (pattern.charAt(j) != c) {
                    gap[k] = true;
                } else {
                    if (count[j] == count[k]) {
                        gap[k] = gap[j];
                    } else if (count[j] < count[k]) {
                        count[k] = count[j];
                        if (gap[j]) {
                            count[k]++;
                        }
                        gap[k] = false;
                    }
                }
            }
        }
        return count[m];
    }

}
