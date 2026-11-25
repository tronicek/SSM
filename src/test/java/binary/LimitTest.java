package binary;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests.
 *
 * @author Zdenek Tronicek
 */
public class LimitTest {

    private void testNFABit(String text, String pattern) {
        assertEquals(NFABit.MAX_LENGTH, pattern.length());
        int nfa = NFADirect.search(text, pattern);
        int nfab = NFABit.search(text, pattern);
        assertEquals(nfa, nfab);
    }

    private void testNFABit256(String text, String pattern) {
        assertEquals(NFABit256.MAX_LENGTH, pattern.length());
        int nfa = NFADirect.search(text, pattern);
        int nfab = NFABit256.search(text, pattern);
        assertEquals(nfa, nfab);
    }

    private void testNFABit512(String text, String pattern) {
        assertEquals(NFABit512.MAX_LENGTH, pattern.length());
        int nfa = NFADirect.search(text, pattern);
        int nfab = NFABit512.search(text, pattern);
        assertEquals(nfa, nfab);
    }

    private void testShiftAnd(String text, String pattern) {
        assertEquals(ShiftAnd.MAX_LENGTH, pattern.length());
        int nfa = NFADirect.search(text, pattern);
        int sa = ShiftAnd.search(text, pattern);
        assertEquals(nfa, sa);
    }

    private void testShiftAnd256(String text, String pattern) {
        assertEquals(ShiftAnd256.MAX_LENGTH, pattern.length());
        int nfa = NFADirect.search(text, pattern);
        int sa256 = ShiftAnd256.search(text, pattern);
        assertEquals(nfa, sa256);
    }

    private void testShiftAnd512(String text, String pattern) {
        assertEquals(ShiftAnd512.MAX_LENGTH, pattern.length());
        int nfa = NFADirect.search(text, pattern);
        int sa512 = ShiftAnd512.search(text, pattern);
        assertEquals(nfa, sa512);
    }

    @Test
    public void testNFABit() {
        testNFABit("abababababababa", "aaaaaaaa");
        testNFABit("aabbaabbaabbaab", "abababab");
    }

    @Test
    public void testNFABit256() {
        testNFABit256("abababababababababababababababa", "aaaaaaaaaaaaaaaa");
        testNFABit256("aabbaabbaabbaabbaabbaabbaabbaab", "abababababababab");
    }

    @Test
    public void testNFABit512() {
        testNFABit512("abababababababababababababababababababababa", "aaaaaaaaaaaaaaaaaaaaaa");
        testNFABit512("aabbaabbaabbaabbaabbaabbaabbaabbaabbaabbaab", "ababababababababababab");
    }

    @Test
    public void testShiftAnd() {
        testShiftAnd("abababababa", "aaaaaa");
        testShiftAnd("aabbaabbaabb", "ababab");
    }

    @Test
    public void testShiftAnd256() {
        testShiftAnd256("ababababababababababa", "aaaaaaaaaaa");
        testShiftAnd256("aabbaabbaabbaabbaabbaa", "abababababa");
    }

    @Test
    public void testShiftAnd512() {
        testShiftAnd512("abababababababababababababababa", "aaaaaaaaaaaaaaaa");
        testShiftAnd512("aabbaabbaabbaabbaabbaabbaabbaabb", "abababababababab");
    }

}
