package enchcracker;

import java.util.ArrayList;

public class IntArray {
    // more efficient implementation of ArrayList<Integer>

    private int[] list;
    private int size;

    private void addToList(int[] toAdd, int len) {
        if (len == 0) return;
        int newLen = list.length;
        while (size + len >= newLen) newLen += 1000000;
        if (newLen > list.length) {
            int[] newArr = new int[newLen];
            System.arraycopy(list, 0, newArr, 0, size);
            list = newArr;
        }
        System.arraycopy(toAdd, 0, list, size, len);
        size += len;
    }

    public IntArray() {
        clear();
    }

    public void clear() {
        list = new int[1000000];
        size = 0;
    }

    public void addAll(int[] values, int amt) {
        addToList(values, amt);
    }

    public void addAll(ArrayList<Integer> nextPossibleSeeds) {
        // assumed to be a small quantity compared to other addAll, so it's inefficient
        int[] toAdd = new int[nextPossibleSeeds.size()];
        for (int a = 0; a < toAdd.length; a++) toAdd[a] = nextPossibleSeeds.get(a);
        addAll(toAdd, toAdd.length);
    }

    public int size() { return size; }
    public int get(int i) { return list[i]; }
}
