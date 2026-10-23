/**
 * Generic linked storage supplied by CS 2114 staff.
 * @author CS 2114 Staff
 * @version September 2026
 */
public class LinkedChain<T> {
    private Node<T> firstNode;
    private int numberOfEntries;
    
    /**
     * Create a new LinkedChain object.
     */
    public LinkedChain() { firstNode = null; numberOfEntries = 0; }
   
    /**
     * Adds an entry to the front of the chain.
     *
     * @param entry the entry to add
     * @return true because the entry is successfully added
     * @throws IllegalArgumentException if entry is null
     */
    public boolean add(T entry) {
        requireNonNull(entry);
        firstNode = new Node<T>(entry, firstNode);
        numberOfEntries++;
        return true;
    }
    
    /**
     * Adds an entry to the end of the chain.
     *
     * @param entry the entry to add
     * @return true because the entry is successfully added
     * @throws IllegalArgumentException if entry is null
     */
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
    
    
    /**
     * Inserts an entry at the specified position in the chain.
     * Position 0 is the front of the chain. An index equal to the
     * current size adds the entry to the end.
     *
     * @param index the position where the entry should be inserted
     * @param entry the entry to add
     * @throws IndexOutOfBoundsException if index is less than 0
     *         or greater than the current size
     * @throws IllegalArgumentException if entry is null
     */
    public void add(int index, T entry) {
        requireNonNull(entry);
        if (index < 0 || index > numberOfEntries)
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + numberOfEntries);
        if (index == 0) {
            add(entry);                      
        } else if (index == numberOfEntries) {
            addToEnd(entry);                 
        } else {
            Node<T> before = firstNode;       
            for (int i = 0; i < index - 1; i++) before = before.next;
            before.next = new Node<T>(entry, before.next);
            numberOfEntries++;
        }
    }
    
    
    /**
     * Removes and returns the entry at the specified position.
     *
     * @param index the position of the entry to remove, starting at 0
     * @return the entry that was removed
     * @throws IndexOutOfBoundsException if index is less than 0
     *         or greater than or equal to the current size
     */
    public T removeAt(int index) {
        if (index < 0 || index >= numberOfEntries)
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + numberOfEntries);
        if (index == 0) {
            return remove();                
        }
        Node<T> before = firstNode;
        for (int i = 0; i < index - 1; i++) before = before.next;
        Node<T> target = before.next;
        before.next = target.next;            
        numberOfEntries--;
        return target.data;
    }
    
    
    /**
     * Removes and returns the first entry in the chain.
     *
     * @return the first entry, or null if the chain is empty
     */
    public T remove() {
        if (firstNode == null) return null;
        T result = firstNode.data;
        firstNode = firstNode.next;
        numberOfEntries--;
        return result;
    }
    
    /**
     * Removes the first occurrence of the specified entry.
     *
     * @param entry the entry to remove
     * @return true if an entry was removed; false if the entry is null,
     *         the chain is empty, or the entry is not found
     */
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
    
    
  
    /**
     * Removes all entries from the chain.
     * After this operation, the chain is empty.
     */
    public void clear() { firstNode = null; numberOfEntries = 0; }
    
    /**
     * Returns the entry at the specified position without removing it.
     *
     * @param index the position of the entry, starting at 0
     * @return the entry at the specified position
     * @throws IndexOutOfBoundsException if index is less than 0
     *         or greater than or equal to the current size
     */
    public T get(int index) {
        if (index < 0 || index >= numberOfEntries)
            throw new IndexOutOfBoundsException("Index " + index + " out of bounds for size " + numberOfEntries);
        Node<T> current = firstNode;
        for (int i = 0; i < index; i++) current = current.next;
        return current.data;
    }
    
    /**
     * Checks whether the chain contains the specified entry.
     *
     * @param entry the entry to search for
     * @return true if the entry is found; false otherwise
     */
    public boolean contains(T entry) { return count(entry) > 0; }
    
    /**
     * Counts how many times the specified entry appears in the chain.
     *
     * @param entry the entry to count
     * @return the number of occurrences, or 0 if entry is null
     */
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
    
    /**
     * Returns the number of entries in the chain.
     *
     * @return the number of entries
     */
    public int size() { return numberOfEntries; }
    
    /**
     * Checks whether the chain contains no entries.
     *
     * @return true if the chain is empty; false otherwise
     */
    public boolean isEmpty() { return numberOfEntries == 0; }
    
    /**
     * Copies the chain's entries into the provided array, starting at index 0.
     * Entries appear in the same order as they do in the chain.
     *
     * @param values the array that will receive the entries
     * @return the array containing the chain's entries
     * @throws IllegalArgumentException if values is null or too small
     */
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
