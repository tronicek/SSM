package acgt;

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
        test("acgt", "a", 1);
    }

    @Test
    public void test2() {
        test("tgca", "a", 1);
    }

    @Test
    public void test3() {
        test("acgta", "aa", 2);
    }

    @Test
    public void test4() {
        test("acgta", "ac", 1);
    }

    @Test
    public void test5() {
        test("agtac", "ac", 1);
    }

    @Test
    public void test6() {
        test("agaact", "aaa", 2);
    }

    @Test
    public void test7() {
        test("cggaata", "aaa", 2);
    }

    @Test
    public void test8() {
        test("acagata", "aaaa", 4);
    }

    @Test
    public void test9() {
        test("accacaca", "aacc", 3);
    }

    @Test
    public void test10() {
        test("aagacagctt", "aatt", 2);
    }

    @Test
    public void test11() {
        test("aaaaaaaa", "aaaaaaaa", 1);
    }

    @Test
    public void test12() {
        test("aattaatt", "aattaatt", 1);
    }

    @Test
    public void test13() {
        test("aaaaaaaaaaa", "aaaaaaaaaaa", 1);
    }

    @Test
    public void test14() {
        test("aattaaatttt", "aattaaatttt", 1);
    }

    @Test
    public void test15() {
        test("tacgt", "tt", 2);
    }

    @Test
    public void test16() {
        test("tactgat", "ttt", 3);
    }

    @Test
    public void test17() {
        test("catcggctac", "cccc", 4);
    }

    @Test
    public void test18() {
        test("caagaataacaag", "cgtcg", 5);
    }

    @Test
    public void test19() {
        test("caacaagaagaataat", "ccggtt", 6);
    }

}
