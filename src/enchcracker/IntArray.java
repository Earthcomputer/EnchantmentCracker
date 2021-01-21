package enchcracker;

public class IntArray {
    // IntArray that consists of pooled lists to avoid memory hogging when copying to a new array
    // The old implementation that used a single large array effectively needed double the array size for any allocation
    
    private static final int BLOCKSIZE = 1000000;

    private int[][] lists;
    private int size;

    private void addToList(int[] toAdd, int start, int len) {
        int curBlock = size / BLOCKSIZE;
        int avail = BLOCKSIZE - (size % BLOCKSIZE);
        int addPos = start;
        len += start;
        while (addPos < len) {
            int rem = len - addPos;
            if (rem <= avail) {
                System.arraycopy(toAdd, addPos, lists[curBlock], size%BLOCKSIZE, rem);
                size += rem;
                if (size % BLOCKSIZE == 0 && lists[curBlock+1] == null) lists[curBlock+1] = new int[BLOCKSIZE];
                addPos += rem;
            }
            else {
                System.arraycopy(toAdd, addPos, lists[curBlock], size%BLOCKSIZE, avail);
                curBlock++;
                if (lists[curBlock] == null) lists[curBlock] = new int[BLOCKSIZE];
                addPos += avail;
                size += avail;
                avail = BLOCKSIZE;
            }
        }
    }

    public IntArray() { this(false); }

    public IntArray(boolean isMainData) {
        lists = new int[250][]; // not set to 101 just in case something changes
        lists[0] = new int[BLOCKSIZE];
        if (isMainData && System.getProperty("sun.arch.data.model").equals("32")) {
            Log.info("32-bit java detected, pre-allocating IntArray.");
            for (int a = 1; a <= 101; a++) lists[a] = new int[BLOCKSIZE]; // maximum possible seeds is 100.x mil in 1.16
        }
    }

    public void clear() {
        size = 0;
    }

    public void add(int i) {
        int id = size / BLOCKSIZE;
        int pos = size % BLOCKSIZE;
        lists[id][pos] = i;
        size++;
        if (size % BLOCKSIZE == 0 && lists[size / BLOCKSIZE] == null) lists[size / BLOCKSIZE] = new int[BLOCKSIZE];
    }

    public void addAll(int[] values, int amt) {
        int pos = 0;
        while (pos < amt) {
            addToList(values, pos, Math.min(BLOCKSIZE, amt-pos));
            pos += BLOCKSIZE;
        }
    }

    public void addAll(IntArray values) {
        int remSize = values.size;
        int curBlock = 0;
        while (remSize > 0) {
            addToList(values.lists[curBlock], 0, Math.min(BLOCKSIZE, remSize));
            remSize -= BLOCKSIZE;
            curBlock++;
        }
    }

    public int size() { return size; }
    public int get(int i) { return lists[i/BLOCKSIZE][i%BLOCKSIZE]; }
}
