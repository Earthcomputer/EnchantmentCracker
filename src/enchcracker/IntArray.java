package enchcracker;

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
        list = new int[1000000];
    }

    public void clear() {
        // array size is not reduced to reduce number of allocations
        size = 0;
    }

    public void add(int i) {
        if (list.length == size) {
            int[] newArr = new int[list.length + 1000000];
            System.arraycopy(list, 0, newArr, 0, size);
            list = newArr;
        }
        list[size++] = i;
    }

    public void addAll(int[] values, int amt) {
        addToList(values, amt);
    }

    public void addAll(IntArray values) {
        addToList(values.list, values.size);
    }

    public int size() { return size; }
    public int get(int i) { return list[i]; }
}
