import static org.junit.Assert.*;
import org.junit.Test;

public class UndoHistoryTest {
    private Action act(String name) {
        return new Action(ActionType.ADD, new Task(name), null, 0, Day.MONDAY, 1);
    }
    @Test public void recordThenPopNewestFirst() {
        UndoHistory h = new UndoHistory();
        Action a = act("A"), b = act("B");
        h.record(a); h.record(b);
        assertSame(b, h.popUndo());
        assertSame(a, h.popUndo());
    }
    @Test public void popOnEmptyReturnsNull() {
        UndoHistory h = new UndoHistory();
        assertNull(h.popUndo());
        assertNull(h.popRedo());
    }
    @Test public void recordClearsRedo() {
        UndoHistory h = new UndoHistory();
        h.pushRedo(act("A"));
        assertEquals(1, h.redoCount());
        h.record(act("B"));
        assertEquals(0, h.redoCount());
    }
    @Test public void pushUndoDoesNotClearRedo() {
        UndoHistory h = new UndoHistory();
        h.pushRedo(act("A"));
        h.pushUndo(act("B"));
        assertEquals(1, h.redoCount());
    }
}
