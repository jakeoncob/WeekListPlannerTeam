/**
 * Generic linked storage supplied by CS 2114 staff.
 * @author CS 2114 Staff
 * @version September 2026
 */
public class LinkedChain<T> {
    private Node<T> firstNode;
    private int numberOfEntries;
    public LinkedChain() { firstNode = null; numberOfEntries = 0; }
    public boolean add(T entry) {
        requireNonNull(entry);
        firstNode = new Node<T>(entry, firstNode);
        numberOfEntries++;
        return true;
    }
    public boolean addToEnd(T entry) {
        requireNonNull(entry);
        Node<T> node = new Node<T>(entry);
        if (firstNode == null) firstNode = node;
        else {
            Node<T> current = firstNode;
            while (current.next != null) current = current.next;
            current.next = node;
        }
        numberOfEntries++;
        return true;
    }
    public T remove() {
        if (firstNode == null) return null;
        T result = firstNode.data;
        firstNode = firstNode.next;
        numberOfEntries--;
        return result;
    }
    public boolean remove(T entry) {
        if (entry == null || firstNode == null) return false;
        if (entry.equals(firstNode.data)) {
            firstNode = firstNode.next; numberOfEntries--; return true;
        }
        Node<T> prev = firstNode;
        Node<T> current = firstNode.next;
        while (current != null) {
            if (entry.equals(current.data)) {
                prev.next = current.next; numberOfEntries--; return true;
            }
            prev = current; current = current.next;
        }
        return false;
    }
    public void clear() { firstNode = null; numberOfEntries = 0; }
    public T get(int index) {
        if (index < 0 || index >= numberOfEntries)
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + numberOfEntries);
        Node<T> current = firstNode;
        for (int i = 0; i < index; i++) current = current.next;
        return current.data;
    }
    public boolean contains(T entry) { return count(entry) > 0; }
    public int count(T entry) {
        if (entry == null) return 0;
        int n = 0;
        Node<T> current = firstNode;
        while (current != null) {
            if (entry.equals(current.data)) n++;
            current = current.next;
        }
        return n;
    }
    public int size() { return numberOfEntries; }
    public boolean isEmpty() { return numberOfEntries == 0; }
    public T[] toArray(T[] values) {
        if (values == null) throw new IllegalArgumentException("Array must not be null");
        if (values.length < numberOfEntries) throw new IllegalArgumentException("Array too small");
        Node<T> current = firstNode;
        int i = 0;
        while (current != null) { values[i++] = current.data; current = current.next; }
        return values;
    }
    @Override public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<T> current = firstNode;
        while (current != null) {
            sb.append(current.data);
            if (current.next != null) sb.append(", ");
            current = current.next;
        }
        return sb.append("]").toString();
    }
    private void requireNonNull(T entry) {
        if (entry == null) throw new IllegalArgumentException("Cannot add null to chain");
    }
    private static class Node<E> {
        private E data;
        private Node<E> next;
        private Node(E data) { this(data, null); }
        private Node(E data, Node<E> next) { this.data = data; this.next = next; }
    }
}
