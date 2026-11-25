package binary;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests.
 *
 * @author Zdenek Tronicek
 */
public class SearchTest {

    private void test(String text, String pattern, int expected) {
        testNFADirect(text, pattern, expected);
        if (pattern.length() <= NFABit.MAX_LENGTH) {
            testNFABit(text, pattern, expected);
        }
        if (pattern.length() <= NFABit256.MAX_LENGTH) {
            testNFABit256(text, pattern, expected);
        }
        if (pattern.length() <= NFABit512.MAX_LENGTH) {
            testNFABit512(text, pattern, expected);
        }
        testNFABitBigInteger(text, pattern, expected);
        testNFABitLongPattern(text, pattern, expected);
        testSegE(text, pattern, expected);
        testDynamicProgramming(text, pattern, expected);
        if (pattern.length() <= ShiftAnd.MAX_LENGTH) {
            testShiftAnd(text, pattern, expected);
        }
        if (pattern.length() <= ShiftAnd256.MAX_LENGTH) {
            testShiftAnd256(text, pattern, expected);
        }
        if (pattern.length() <= ShiftAnd512.MAX_LENGTH) {
            testShiftAnd512(text, pattern, expected);
        }
        testShiftAndBigInteger(text, pattern, expected);
        testShiftAndLongPattern(text, pattern, expected);
    }

    private void testNFADirect(String text, String pattern, int expected) {
        int result = NFADirect.search(text, pattern);
        assertEquals(expected, result);
    }

    private void testNFABit(String text, String pattern, int expected) {
        int result = NFABit.search(text, pattern);
        assertEquals(expected, result);
    }

    private void testNFABit256(String text, String pattern, int expected) {
        int result = NFABit256.search(text, pattern);
        assertEquals(expected, result);
    }

    private void testNFABit512(String text, String pattern, int expected) {
        int result = NFABit512.search(text, pattern);
        assertEquals(expected, result);
    }

    private void testNFABitBigInteger(String text, String pattern, int expected) {
        int result = NFABitBigInteger.search(text, pattern);
        assertEquals(expected, result);
    }

    private void testNFABitLongPattern(String text, String pattern, int expected) {
        int result = NFABitLongPattern.search(text, pattern);
        assertEquals(expected, result);
    }

    private void testSegE(String text, String pattern, int expected) {
        int result = SegE.search(text, pattern);
        assertEquals(expected, result);
    }

    private void testDynamicProgramming(String text, String pattern, int expected) {
        int result = DynamicProgramming.search(text, pattern);
        assertEquals(expected, result);
    }

    private void testShiftAnd(String text, String pattern, int expected) {
        int result = ShiftAnd.search(text, pattern);
        assertEquals(expected, result);
    }

    private void testShiftAnd256(String text, String pattern, int expected) {
        int result = ShiftAnd256.search(text, pattern);
        assertEquals(expected, result);
    }

    private void testShiftAnd512(String text, String pattern, int expected) {
        int result = ShiftAnd512.search(text, pattern);
        assertEquals(expected, result);
    }

    private void testShiftAndBigInteger(String text, String pattern, int expected) {
        int result = ShiftAndBigInteger.search(text, pattern);
        assertEquals(expected, result);
    }

    private void testShiftAndLongPattern(String text, String pattern, int expected) {
        int result = ShiftAndLongPattern.search(text, pattern);
        assertEquals(expected, result);
    }

    @Test
    public void test1() {
        test("abbb", "a", 1);
    }

    @Test
    public void test2() {
        test("bbba", "a", 1);
    }

    @Test
    public void test3() {
        test("abbba", "aa", 2);
    }

    @Test
    public void test4() {
        test("abbb", "ab", 1);
    }

    @Test
    public void test5() {
        test("aaab", "ab", 1);
    }

    @Test
    public void test6() {
        test("abaabb", "aaa", 2);
    }

    @Test
    public void test7() {
        test("bbaaba", "aaa", 2);
    }

    @Test
    public void test8() {
        test("abababa", "aaaa", 4);
    }

    @Test
    public void test9() {
        test("abbababa", "aabb", 3);
    }

    @Test
    public void test10() {
        test("aababababb", "aabb", 2);
    }

    @Test
    public void test11() {
        test("aaaaaaaa", "aaaaaaaa", 1);
    }

    @Test
    public void test12() {
        test("aabbaabb", "aabbaabb", 1);
    }

    @Test
    public void test13() {
        test("aaaaaaaaaaa", "aaaaaaaaaaa", 1);
    }

    @Test
    public void test14() {
        test("aabbaaabbbb", "aabbaaabbbb", 1);
    }

    @Test
    public void test15() {
        test("baab", "bb", 2);
    }

    @Test
    public void test16() {
        test("baabaab", "bbb", 3);
    }

    @Test
    public void test17() {
        test("baabaabaab", "bbbb", 4);
    }

    @Test
    public void test18() {
        test("baabaabaabaab", "bbbbb", 5);
    }

    @Test
    public void test19() {
        test("baabaabaabaabaab", "bbbbbb", 6);
    }

}
