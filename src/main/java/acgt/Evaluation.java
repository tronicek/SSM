package acgt;

import data.Input;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.TreeMap;

/**
 * Performance evaluation.
 *
 * @author Zdenek Tronicek
 */
public class Evaluation {

    static final int ALPH = 4;
    static final Map<String, Map<Integer, List<Long>>> map = new HashMap<>();
    static int warm;
    static int measure;
    static final Random rand = new Random();

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("expected arguments: properties plen");
            return;
        }
        Properties conf = new Properties();
        try ( FileReader in = new FileReader(args[0])) {
            conf.load(in);
        }
        String dataDir = conf.getProperty("dataDir");
        Input.setDataDir(dataDir);
        warm = Integer.parseInt(conf.getProperty("warm"));
        measure = Integer.parseInt(conf.getProperty("measure"));
        int plen = Integer.parseInt(args[1]);
        measurePyrococcus(plen);
        measureStaphylococcus(plen);
        measureSalmonella(plen);
        measureEColi(plen);
        measureMalassezia(plen);
        measureSaccharomyces(plen);
        measurePenicillium(plen);
        measureNeurospora(plen);
        printTimes();
        printDeltas();
    }

    static String select(String text, int plen) {
        for (int n = 10000; n > 0; n--) {
            String pat = randomString(plen);
            if (!text.contains(pat)) {
                return pat;
            }
        }
        return randomString(plen);
    }

    static String randomString(int len) {
        long m = (1L << (2 * len));
        long r = rand.nextLong(m);
        String s = Long.toString(r, ALPH);
        for (int i = s.length(); i < len; i++) {
            s = '0' + s;
        }
        s = s.replace('0', 'a')
                .replace('1', 'c')
                .replace('2', 'g')
                .replace('3', 't');
        return s;
    }

    static void measurePyrococcus(int plen) throws Exception {
        System.out.println("Pyrococcus furiosus");
        String text = Input.readPyrococcus();
        String pattern = select(text, plen);
        measureAll(text, pattern);
    }

    static void measureStaphylococcus(int plen) throws Exception {
        System.out.println("Staphylococcus aureus");
        String text = Input.readStaphylococcus();
        String pattern = select(text, plen);
        measureAll(text, pattern);
    }

    static void measureSalmonella(int plen) throws Exception {
        System.out.println("Salmonella enterica");
        String text = Input.readSalmonella();
        String pattern = select(text, plen);
        measureAll(text, pattern);
    }

    static void measureEColi(int plen) throws Exception {
        System.out.println("Escherichia coli");
        String text = Input.readEColi();
        String pattern = select(text, plen);
        measureAll(text, pattern);
    }

    static void measureMalassezia(int plen) throws Exception {
        System.out.println("Malassezia japonica");
        String text = Input.readMalassezia();
        String pattern = select(text, plen);
        measureAll(text, pattern);
    }

    static void measureSaccharomyces(int plen) throws Exception {
        System.out.println("Saccharomyces cerevisiae");
        String text = Input.readSaccharomyces();
        String pattern = select(text, plen);
        measureAll(text, pattern);
    }

    static void measurePenicillium(int plen) throws Exception {
        System.out.println("Penicillium chrysogenum");
        String text = Input.readPenicillium();
        String pattern = select(text, plen);
        measureAll(text, pattern);
    }

    static void measureNeurospora(int plen) throws Exception {
        System.out.println("Neurospora hispaniola");
        String text = Input.readNeurospora();
        String pattern = select(text, plen);
        measureAll(text, pattern);
    }

    static void measureAll(String text, String pattern) throws Exception {
        if (pattern.length() <= NFABit.MAX_LENGTH) {
            measureNFABit(text, pattern);
        }
        if (pattern.length() <= NFABit256.MAX_LENGTH) {
            measureNFABit256(text, pattern);
        }
        if (pattern.length() <= NFABit512.MAX_LENGTH) {
            measureNFABit512(text, pattern);
        }
        measureNFABitBigInteger(text, pattern);
        measureNFABitLongPattern(text, pattern);
        measureNFADirect(text, pattern);
        measureSegE(text, pattern);
        measureDynamicProgramming(text, pattern);
        if (pattern.length() <= ShiftAnd.MAX_LENGTH) {
            measureShiftAnd(text, pattern);
        }
        if (pattern.length() <= ShiftAnd256.MAX_LENGTH) {
            measureShiftAnd256(text, pattern);
        }
        if (pattern.length() <= ShiftAnd512.MAX_LENGTH) {
            measureShiftAnd512(text, pattern);
        }
        measureShiftAndBigInteger(text, pattern);
        measureShiftAndLongPattern(text, pattern);
    }

    static void printTimes() {
        System.out.println();
        System.out.println("Time in ms");
        System.out.print("     ");
        Map<Integer, List<Long>> submap = map.get("DP");
        for (int tlen : submap.keySet()) {
            System.out.printf(" %d", tlen);
        }
        System.out.println();
        printTimes("NFAB");
        printTimes("NFAB256");
        printTimes("NFAB512");
        printTimes("NFABBI");
        printTimes("NFABLP");
        printTimes("NFAD");
        printTimes("SegE");
        printTimes("DP");
        printTimes("SA");
        printTimes("SA256");
        printTimes("SA512");
        printTimes("SABI");
        printTimes("SALP");
    }

    static void printTimes(String algo) {
        Map<Integer, List<Long>> submap = map.get(algo);
        if (submap == null) {
            return;
        }
        System.out.print(algo);
        for (int tlen : submap.keySet()) {
            List<Long> times = submap.get(tlen);
            long avg = average(times);
            System.out.printf(" %d", avg / 1_000_000L);
        }
        System.out.println();
    }

    static long average(List<Long> times) {
        long sum = 0L;
        for (long time : times) {
            sum += time;
        }
        return sum / times.size();
    }

    static void printDeltas() {
        System.out.println();
        System.out.println("Delta in %");
        System.out.print("     ");
        Map<Integer, List<Long>> submap = map.get("DP");
        for (int tlen : submap.keySet()) {
            System.out.printf(" %d", tlen);
        }
        System.out.println();
        printDeltas("NFAB");
        printDeltas("NFAB256");
        printDeltas("NFAB512");
        printDeltas("NFABBI");
        printDeltas("NFABLP");
        printDeltas("NFAD");
        printDeltas("SegE");
        printDeltas("DP");
        printDeltas("SA");
        printDeltas("SA256");
        printDeltas("SA512");
        printDeltas("SABI");
        printDeltas("SALP");
    }

    static void printDeltas(String algo) {
        Map<Integer, List<Long>> submap = map.get(algo);
        if (submap == null) {
            return;
        }
        System.out.print(algo);
        for (int tlen : submap.keySet()) {
            List<Long> times = submap.get(tlen);
            long avg = average(times);
            double dt = delta(avg, times) * 100.0 / avg;
            System.out.printf(" %.0f", dt);
        }
        System.out.println();
    }

    static long delta(long avg, List<Long> times) {
        long d = 0L;
        for (long time : times) {
            long dn = Math.abs(time - avg);
            if (dn > d) {
                d = dn;
            }
        }
        return d;
    }

    static void storeTime(String algo, int tlen, long time) {
        Map<Integer, List<Long>> submap = map.get(algo);
        if (submap == null) {
            submap = new TreeMap<>();
            map.put(algo, submap);
        }
        List<Long> times = submap.get(tlen);
        if (times == null) {
            times = new ArrayList<>();
            submap.put(tlen, times);
        }
        times.add(time);
    }

    static void measureNFABit(String text, String pattern) {
        for (int i = 0; i < warm; i++) {
            long start = System.nanoTime();
            int segm = NFABit.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            long ms = time / 1_000_000L;
            System.out.printf("NFAB warming up: %d ms (%d segments)%n", ms, segm);
        }
        for (int i = 0; i < measure; i++) {
            long start = System.nanoTime();
            int segm = NFABit.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            storeTime("NFAB", text.length(), time);
            long ms = time / 1_000_000L;
            System.out.printf("NFAB: %d ms (%d segments)%n", ms, segm);
        }
    }

    static void measureNFABit256(String text, String pattern) {
        for (int i = 0; i < warm; i++) {
            long start = System.nanoTime();
            int segm = NFABit256.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            long ms = time / 1_000_000L;
            System.out.printf("NFAB256 warming up: %d ms (%d segments)%n", ms, segm);
        }
        for (int i = 0; i < measure; i++) {
            long start = System.nanoTime();
            int segm = NFABit256.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            storeTime("NFAB256", text.length(), time);
            long ms = time / 1_000_000L;
            System.out.printf("NFAB256: %d ms (%d segments)%n", ms, segm);
        }
    }

    static void measureNFABit512(String text, String pattern) {
        for (int i = 0; i < warm; i++) {
            long start = System.nanoTime();
            int segm = NFABit512.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            long ms = time / 1_000_000L;
            System.out.printf("NFAB512 warming up: %d ms (%d segments)%n", ms, segm);
        }
        for (int i = 0; i < measure; i++) {
            long start = System.nanoTime();
            int segm = NFABit512.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            storeTime("NFAB512", text.length(), time);
            long ms = time / 1_000_000L;
            System.out.printf("NFAB512: %d ms (%d segments)%n", ms, segm);
        }
    }

    static void measureNFABitBigInteger(String text, String pattern) {
        for (int i = 0; i < warm; i++) {
            long start = System.nanoTime();
            int segm = NFABitBigInteger.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            long ms = time / 1_000_000L;
            System.out.printf("NFABBI warming up: %d ms (%d segments)%n", ms, segm);
        }
        for (int i = 0; i < measure; i++) {
            long start = System.nanoTime();
            int segm = NFABitBigInteger.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            storeTime("NFABBI", text.length(), time);
            long ms = time / 1_000_000L;
            System.out.printf("NFABBI: %d ms (%d segments)%n", ms, segm);
        }
    }

    static void measureNFABitLongPattern(String text, String pattern) {
        for (int i = 0; i < warm; i++) {
            long start = System.nanoTime();
            int segm = NFABitLongPattern.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            long ms = time / 1_000_000L;
            System.out.printf("NFABLP warming up: %d ms (%d segments)%n", ms, segm);
        }
        for (int i = 0; i < measure; i++) {
            long start = System.nanoTime();
            int segm = NFABitLongPattern.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            storeTime("NFABLP", text.length(), time);
            long ms = time / 1_000_000L;
            System.out.printf("NFABLP: %d ms (%d segments)%n", ms, segm);
        }
    }

    static void measureNFADirect(String text, String pattern) {
        for (int i = 0; i < warm; i++) {
            long start = System.nanoTime();
            int segm = NFADirect.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            long ms = time / 1_000_000L;
            System.out.printf("NFAD warming up: %d ms (%d segments)%n", ms, segm);
        }
        for (int i = 0; i < measure; i++) {
            long start = System.nanoTime();
            int segm = NFADirect.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            storeTime("NFAD", text.length(), time);
            long ms = time / 1_000_000L;
            System.out.printf("NFAD: %d ms (%d segments)%n", ms, segm);
        }
    }

    static void measureSegE(String text, String pattern) {
        for (int i = 0; i < warm; i++) {
            long start = System.nanoTime();
            int segm = SegE.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            long ms = time / 1_000_000L;
            System.out.printf("SegE warming up: %d ms (%d segments)%n", ms, segm);
        }
        for (int i = 0; i < measure; i++) {
            long start = System.nanoTime();
            int segm = SegE.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            storeTime("SegE", text.length(), time);
            long ms = time / 1_000_000L;
            System.out.printf("SegE: %d ms (%d segments)%n", ms, segm);
        }
    }

    static void measureDynamicProgramming(String text, String pattern) {
        for (int i = 0; i < warm; i++) {
            long start = System.nanoTime();
            int segm = DynamicProgramming.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            long ms = time / 1_000_000L;
            System.out.printf("DP warming up: %d ms (%d segments)%n", ms, segm);
        }
        for (int i = 0; i < measure; i++) {
            long start = System.nanoTime();
            int segm = DynamicProgramming.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            storeTime("DP", text.length(), time);
            long ms = time / 1_000_000L;
            System.out.printf("DP: %d ms (%d segments)%n", ms, segm);
        }
    }

    static void measureShiftAnd(String text, String pattern) {
        for (int i = 0; i < warm; i++) {
            long start = System.nanoTime();
            int segm = ShiftAnd.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            long ms = time / 1_000_000L;
            System.out.printf("SA warming up: %d ms (%d segments)%n", ms, segm);
        }
        for (int i = 0; i < measure; i++) {
            long start = System.nanoTime();
            int segm = ShiftAnd.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            storeTime("SA", text.length(), time);
            long ms = time / 1_000_000L;
            System.out.printf("SA: %d ms (%d segments)%n", ms, segm);
        }
    }

    static void measureShiftAnd256(String text, String pattern) {
        for (int i = 0; i < warm; i++) {
            long start = System.nanoTime();
            int segm = ShiftAnd256.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            long ms = time / 1_000_000L;
            System.out.printf("SA256 warming up: %d ms (%d segments)%n", ms, segm);
        }
        for (int i = 0; i < measure; i++) {
            long start = System.nanoTime();
            int segm = ShiftAnd256.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            storeTime("SA256", text.length(), time);
            long ms = time / 1_000_000L;
            System.out.printf("SA256: %d ms (%d segments)%n", ms, segm);
        }
    }

    static void measureShiftAnd512(String text, String pattern) {
        for (int i = 0; i < warm; i++) {
            long start = System.nanoTime();
            int segm = ShiftAnd512.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            long ms = time / 1_000_000L;
            System.out.printf("SA512 warming up: %d ms (%d segments)%n", ms, segm);
        }
        for (int i = 0; i < measure; i++) {
            long start = System.nanoTime();
            int segm = ShiftAnd512.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            storeTime("SA512", text.length(), time);
            long ms = time / 1_000_000L;
            System.out.printf("SA512: %d ms (%d segments)%n", ms, segm);
        }
    }

    static void measureShiftAndBigInteger(String text, String pattern) {
        for (int i = 0; i < warm; i++) {
            long start = System.nanoTime();
            int segm = ShiftAndBigInteger.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            long ms = time / 1_000_000L;
            System.out.printf("SABI warming up: %d ms (%d segments)%n", ms, segm);
        }
        for (int i = 0; i < measure; i++) {
            long start = System.nanoTime();
            int segm = ShiftAndBigInteger.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            storeTime("SABI", text.length(), time);
            long ms = time / 1_000_000L;
            System.out.printf("SABI: %d ms (%d segments)%n", ms, segm);
        }
    }

    static void measureShiftAndLongPattern(String text, String pattern) {
        for (int i = 0; i < warm; i++) {
            long start = System.nanoTime();
            int segm = ShiftAndLongPattern.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            long ms = time / 1_000_000L;
            System.out.printf("SALP warming up: %d ms (%d segments)%n", ms, segm);
        }
        for (int i = 0; i < measure; i++) {
            long start = System.nanoTime();
            int segm = ShiftAndLongPattern.search(text, pattern);
            long end = System.nanoTime();
            long time = end - start;
            storeTime("SALP", text.length(), time);
            long ms = time / 1_000_000L;
            System.out.printf("SALP: %d ms (%d segments)%n", ms, segm);
        }
    }

}
