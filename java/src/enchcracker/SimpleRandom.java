package enchcracker;

public class SimpleRandom {
    // more efficient implementation of java's random class for this specific use-case
    private static long multiplier = 0x5DEECE66DL;
    private static long mask = (1L << 48) - 1;
    private long seed = 0;
    public void setSeed(long seed) {
        this.seed = (seed ^ multiplier) & mask;
    }

    // Always next(31) - inlined
    private int next() {
        seed = (seed * multiplier + 0xBL) & mask;
        return (int)(seed >>> 17);
    }

    public int nextInt(int bound) {
        int r = next();
        int m = bound - 1;
        if ((bound & m) == 0)  // i.e., bound is a power of 2
            r = (int)((bound * (long)r) >> 31);
        else {
            int u = r;
            while (u - (r = u % bound) + m < 0) u = next();
        }
        return r;
    }
}