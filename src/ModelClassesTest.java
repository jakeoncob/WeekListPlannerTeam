import static org.junit.Assert.*;
import org.junit.Test;

/** Tests for Task, Day, CompletedEntry, and Action. */
public class ModelClassesTest {
    // ---------- Task ----------
    @Test public void taskTrimsName() {
        Task t = new Task("  Study ");
        assertEquals("Study", t.getName());
        assertEquals("Study", t.toString());
    }
    @Test(expected = IllegalArgumentException.class)
    public void taskRejectsBlank() { new Task("   "); }
    @Test(expected = IllegalArgumentException.class)
    public void taskRejectsNull() { new Task(null); }
    @Test public void taskLengthLimit() {
        assertEquals(50, new Task(letters(50)).getName().length());
        try { new Task(letters(51)); fail("51 characters should be rejected"); }
        catch (IllegalArgumentException expected) { }
    }

    private static String letters(int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) sb.append('x');
        return sb.toString();
    }

    // ---------- Day ----------
    @Test public void dayFromTextIgnoresCaseAndSpaces() {
        assertEquals(Day.MONDAY, Day.fromText(" monday "));
        assertEquals("Monday", Day.MONDAY.toString());
    }
    @Test(expected = IllegalArgumentException.class)
    public void dayRejectsMisspelling() { Day.fromText("munsday"); }
    @Test(expected = IllegalArgumentException.class)
    public void dayRejectsNull() { Day.fromText(null); }

    // ---------- CompletedEntry ----------
    @Test public void completedEntryHoldsTaskAndDay() {
        Task t = new Task("Read");
        CompletedEntry e = new CompletedEntry(t, Day.FRIDAY);
        assertSame(t, e.getTask());
        assertEquals(Day.FRIDAY, e.getFromDay());
        assertEquals("Read (Friday)", e.toString());
    }
    @Test(expected = IllegalArgumentException.class)
    public void completedEntryRejectsNullTask() { new CompletedEntry(null, Day.MONDAY); }

    // ---------- Action ----------
    @Test public void actionStoresChange() {
        Task t = new Task("Math");
        Action a = new Action(ActionType.MOVE, t, Day.MONDAY, 2, Day.WEDNESDAY, 1);
        assertEquals(ActionType.MOVE, a.getType());
        assertEquals(2, a.getFromRank());
        assertEquals(Day.WEDNESDAY, a.getToDay());
    }
    @Test(expected = IllegalArgumentException.class)
    public void actionRejectsNullTask() { new Action(ActionType.ADD, null, null, 0, Day.MONDAY, 1); }

    // ---------- Action: BATCH ----------
    @Test public void batchHoldsItsStepsInOrder() {
        LinkedChain<Action> steps = new LinkedChain<Action>();
        steps.addToEnd(new Action(ActionType.MOVE, new Task("A"), Day.MONDAY, 1, Day.TUESDAY, 1));
        steps.addToEnd(new Action(ActionType.MOVE, new Task("B"), Day.MONDAY, 1, Day.TUESDAY, 2));
        Action batch = Action.batch(steps);
        assertEquals(ActionType.BATCH, batch.getType());
        assertEquals(2, batch.getSteps().length);
        assertEquals("A", batch.getSteps()[0].getTask().getName());
        assertEquals(0, new Action(ActionType.ADD, new Task("A"), null, 0, Day.MONDAY, 1).getSteps().length);
    }
    @Test(expected = IllegalArgumentException.class)
    public void batchNeedsAtLeastOneStep() { Action.batch(new LinkedChain<Action>()); }
    @Test(expected = IllegalArgumentException.class)
    public void batchCannotBeBuiltWithoutSteps() { new Action(ActionType.BATCH, null, null, 0, null, 0); }
}