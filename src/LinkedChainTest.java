import static org.junit.Assert.*;
import org.junit.Test;

/** One set of chain checks run on THREE element types: String, Task, Action. */
public class LinkedChainTest {

    // ---------- String ----------
    @Test public void addToEndKeepsOrder() {
        LinkedChain<String> c = new LinkedChain<String>();
        c.addToEnd("A"); c.addToEnd("B");
        assertEquals(2, c.size());
        assertEquals("B", c.get(1));
    }
    @Test(expected = IllegalArgumentException.class)
    public void addNullRejected() { new LinkedChain<String>().add(null); }

    @Test public void insertInMiddle() {
        LinkedChain<String> c = new LinkedChain<String>();
        c.addToEnd("A"); c.addToEnd("C");
        c.add(1, "B");
        assertEquals("[A, B, C]", c.toString());
        assertEquals(3, c.size());
    }
    @Test public void insertAtFrontAndEnd() {
        LinkedChain<String> c = new LinkedChain<String>();
        c.add(0, "B");           // empty chain, index 0 == size
        c.add(0, "A");           // front
        c.add(2, "C");           // end (index == size)
        assertEquals("[A, B, C]", c.toString());
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void insertPastEndRejected() {
        LinkedChain<String> c = new LinkedChain<String>();
        c.addToEnd("A"); c.addToEnd("B");
        c.add(5, "X");
    }
    @Test(expected = IllegalArgumentException.class)
    public void insertNullRejected() { new LinkedChain<String>().add(0, null); }

    @Test public void removeAtMiddleReturnsItem() {
        LinkedChain<String> c = new LinkedChain<String>();
        c.addToEnd("A"); c.addToEnd("B"); c.addToEnd("C");
        assertEquals("B", c.removeAt(1));
        assertEquals("[A, C]", c.toString());
        assertEquals(2, c.size());
    }
    @Test public void removeAtFrontLastAndOnly() {
        LinkedChain<String> c = new LinkedChain<String>();
        c.addToEnd("A"); c.addToEnd("B"); c.addToEnd("C");
        assertEquals("A", c.removeAt(0));
        assertEquals("C", c.removeAt(1));       // last
        assertEquals("B", c.removeAt(0));       // only item
        assertTrue(c.isEmpty());
        c.addToEnd("D");                        // chain still works afterward
        assertEquals("[D]", c.toString());
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void removeAtOnEmptyChain() { new LinkedChain<String>().removeAt(0); }
    @Test(expected = IndexOutOfBoundsException.class)
    public void removeAtNegative() {
        LinkedChain<String> c = new LinkedChain<String>();
        c.addToEnd("A");
        c.removeAt(-1);
    }
    @Test public void removeOnlyFirstDuplicate() {
        LinkedChain<String> c = new LinkedChain<String>();
        c.addToEnd("A"); c.addToEnd("B"); c.addToEnd("A");
        assertTrue(c.remove("A"));
        assertEquals("[B, A]", c.toString());
    }
    @Test public void removeItemNotPresent() {
        LinkedChain<String> c = new LinkedChain<String>();
        c.addToEnd("A");
        assertFalse(c.remove("Z"));
        assertEquals(1, c.size());
    }
    @Test public void containsAndEmpty() {
        LinkedChain<String> c = new LinkedChain<String>();
        assertFalse(c.contains("A"));
        c.addToEnd("A");
        assertTrue(c.contains("A"));
    }
    @Test public void getOutOfRange() {
        LinkedChain<String> c = new LinkedChain<String>();
        c.addToEnd("A");
        try { c.get(1); fail("expected IndexOutOfBoundsException"); }
        catch (IndexOutOfBoundsException expected) { }
    }
    @Test public void clearThenReuse() {
        LinkedChain<String> c = new LinkedChain<String>();
        c.addToEnd("A"); c.clear();
        assertEquals(0, c.size());
        c.addToEnd("B");
        assertEquals("[B]", c.toString());
    }

    // ---------- Task: the SAME class, a different element type ----------
    @Test public void chainHoldsTasks() {
        LinkedChain<Task> c = new LinkedChain<Task>();
        c.addToEnd(new Task("Math")); c.addToEnd(new Task("Study"));
        c.add(1, new Task("Read"));
        assertEquals("Read", c.get(1).getName());       // no casting needed
        assertEquals("Read", c.removeAt(1).getName());
    }

    // ---------- Action: a third element type ----------
    @Test public void chainHoldsActions() {
        LinkedChain<Action> c = new LinkedChain<Action>();
        Action a = new Action(ActionType.ADD, new Task("Math"), null, 0, Day.MONDAY, 1);
        c.add(a);
        assertSame(a, c.get(0));
        assertSame(a, c.removeAt(0));
        assertTrue(c.isEmpty());
    }
}