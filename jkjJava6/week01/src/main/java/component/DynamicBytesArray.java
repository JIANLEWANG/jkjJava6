package component;

/**
 * 动态扩容数组
 * 不支持并发
 */
public class DynamicBytesArray {
    private static final int DEFAULT_SIZE = 8;
    private byte[] bytes = new byte[DEFAULT_SIZE];
    private int capacity = DEFAULT_SIZE;
    private int size = 0;

    public void add(byte item) {
        if (isFull()) {
            resize();
        }
        bytes[size] = item;
        size++;

    }

    private boolean isFull() {
        return size == capacity;
    }

    private void resize() {
        capacity = capacity << 1;
        byte[] newBytes = new byte[capacity];
        if (size >= 0) System.arraycopy(bytes, 0, newBytes, 0, size);
        this.bytes = newBytes;
    }

    public byte[] getBytes() {
        byte[] cloneBytes = new byte[size];
        if (size >= 0) System.arraycopy(this.bytes, 0, cloneBytes, 0, size);
        return cloneBytes;
    }
}
