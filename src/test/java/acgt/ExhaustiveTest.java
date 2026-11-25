package acgt;

import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Exhaustive unit tests.
 *
 * @author Zdenek Tronicek
 */
public class ExhaustiveTest {

    private static final int ALPH = 4;

    private void test(int tlen, int plen) {
        for (String text : generate(tlen)) {
            for (String pat : generate(plen)) {
                int nfa = NFADirect.search(text, pat);
                if (pat.length() <= NFABit.MAX_LENGTH) {
                    int nfab = NFABit.search(text, pat);
                    assertEquals(nfa, nfab);
                }
                if (pat.length() <= NFABit256.MAX_LENGTH) {
                    int nfab256 = NFABit256.search(text, pat);
                    assertEquals(nfa, nfab256);
                }
                if (pat.length() <= NFABit512.MAX_LENGTH) {
                    int nfab512 = NFABit512.search(text, pat);
                    assertEquals(nfa, nfab512);
                }
                int nfabBI = NFABitBigInteger.search(text, pat);
                assertEquals(nfa, nfabBI);
                int nfabLP = NFABitLongPattern.search(text, pat);
                assertEquals(nfa, nfabLP);
                int segE = SegE.search(text, pat);
                assertEquals(nfa, segE);
                int dp = DynamicProgramming.search(text, pat);
                assertEquals(nfa, dp);
                if (pat.length() <= ShiftAnd.MAX_LENGTH) {
                    int sa = ShiftAnd.search(text, pat);
                    assertEquals(nfa, sa);
                }
                if (pat.length() <= ShiftAnd256.MAX_LENGTH) {
                    int sa = ShiftAnd256.search(text, pat);
                    assertEquals(nfa, sa);
                }
                if (pat.length() <= ShiftAnd512.MAX_LENGTH) {
                    int sa = ShiftAnd512.search(text, pat);
                    assertEquals(nfa, sa);
                }
                int sabi = ShiftAndBigInteger.search(text, pat);
                assertEquals(nfa, sabi);
                int salp = ShiftAndLongPattern.search(text, pat);
                assertEquals(nfa, salp);
            }
        }
    }

    private Set<String> generate(int len) {
        Set<String> p = new TreeSet<>();
        int m = (1 << (2 * len));
        for (int i = 0; i < m; i++) {
            String s = toStr(len, i);
            p.add(s);
        }
        return p;
    }

    private String toStr(int len, int val) {
        String s = Integer.toString(val, ALPH);
        for (int i = s.length(); i < len; i++) {
            s = '0' + s;
        }
        s = s.replace('0', 'a')
                .replace('1', 'c')
                .replace('2', 'g')
                .replace('3', 't');
        return s;
    }

    private void testAll(int tlen) {
        for (int plen = 1; plen <= tlen; plen++) {
            test(tlen, plen);
        }
    }

    @Test
    public void test1() {
        testAll(1);
    }

    @Test
    public void test2() {
        testAll(2);
    }

    @Test
    public void test3() {
        testAll(3);
    }

    @Test
    public void test4() {
        testAll(4);
    }

}
