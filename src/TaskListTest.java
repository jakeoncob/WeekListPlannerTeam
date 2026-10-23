import static org.junit.Assert.*;
import org.junit.Test;

/** Application tests: one normal and one bad-input case per public TaskList method. */
public class TaskListTest {

    private TaskList monday(String... names) {
        TaskList w = new TaskList();
        for (String n : names) w.addTask(Day.MONDAY, n);
        return w;
    }

    // ---------- addTask ----------
    @Test public void addAtRankShiftsOthersDown() {
        TaskList w = monday("A", "B");
        w.addTask(Day.MONDAY, "C", 2);
        assertEquals("1. A\n2. C\n3. B", w.describeDay(Day.MONDAY));
    }
    @Test public void addWithoutRankGoesToBottom() {
        TaskList w = monday("A");
        w.addTask(Day.MONDAY, "B");
        assertEquals("1. A\n2. B", w.describeDay(Day.MONDAY));
    }
    @Test public void addBadRankLeavesDayUnchanged() {
        TaskList w = monday("A", "B");
        for (int bad : new int[] {0, -1, 4, 1000000}) {
            try { w.addTask(Day.MONDAY, "X", bad); fail("rank " + bad + " should be rejected"); }
            catch (IllegalArgumentException expected) { }
        }
        assertEquals(2, w.taskCount(Day.MONDAY));
    }
    @Test(expected = IllegalArgumentException.class)
    public void addBlankNameRejected() { new TaskList().addTask(Day.MONDAY, "   "); }
    @Test(expected = IllegalArgumentException.class)
    public void addNullDayRejected() { new TaskList().addTask(null, "A"); }
    @Test public void duplicateNamesAllowed() {
        TaskList w = monday("Study", "Study");
        assertEquals(2, w.taskCount(Day.MONDAY));
        w.removeTask(Day.MONDAY, 2);
        assertEquals("1. Study", w.describeDay(Day.MONDAY));
    }

    // ---------- removeTask ----------
    @Test public void removeReturnsTheChosenTask() {
        TaskList w = monday("A", "B", "C");
        assertEquals("B", w.removeTask(Day.MONDAY, 2).getName());
        assertEquals("1. A\n2. C", w.describeDay(Day.MONDAY));
    }
    @Test(expected = IllegalArgumentException.class)
    public void removeFromEmptyDay() { new TaskList().removeTask(Day.TUESDAY, 1); }
    @Test(expected = IllegalArgumentException.class)
    public void removeRankTooBig() { monday("A").removeTask(Day.MONDAY, 2); }

    // ---------- changeRank ----------
    @Test public void changeRankFourToOne() {
        TaskList w = monday("A", "B", "C", "D");
        assertTrue(w.changeRank(Day.MONDAY, 4, 1));
        assertEquals("1. D\n2. A\n3. B\n4. C", w.describeDay(Day.MONDAY));
    }
    @Test public void changeToSameRankChangesNothing() {
        TaskList w = monday("A", "B");
        assertFalse(w.changeRank(Day.MONDAY, 2, 2));
        w.undo();                                  // undoes the last ADD, so no RANK_CHANGE was recorded
        assertEquals("1. A", w.describeDay(Day.MONDAY));
    }
    @Test(expected = IllegalArgumentException.class)
    public void changeRankOnEmptyDay() { new TaskList().changeRank(Day.MONDAY, 1, 2); }
    @Test(expected = IllegalArgumentException.class)
    public void changeRankOutOfRange() { monday("A", "B").changeRank(Day.MONDAY, 1, 9); }

    // ---------- moveTask ----------
    @Test public void moveToBottomOfOtherDay() {
        TaskList w = monday("A", "B");
        w.addTask(Day.WEDNESDAY, "X");
        assertTrue(w.moveTask(Day.MONDAY, 1, Day.WEDNESDAY));
        assertEquals(1, w.taskCount(Day.MONDAY));
        assertEquals("1. X\n2. A", w.describeDay(Day.WEDNESDAY));
    }
    @Test public void moveToChosenRank() {
        TaskList w = monday("A");
        w.addTask(Day.WEDNESDAY, "X");
        w.moveTask(Day.MONDAY, 1, Day.WEDNESDAY, 1);
        assertEquals("1. A\n2. X", w.describeDay(Day.WEDNESDAY));
    }
    @Test public void moveToSameDayChangesNothing() {
        TaskList w = monday("A");
        assertFalse(w.moveTask(Day.MONDAY, 1, Day.MONDAY));
        assertEquals(1, w.taskCount(Day.MONDAY));
    }
    @Test public void moveWithBadDestinationRankDoesNotLoseTask() {
        TaskList w = monday("A");
        try { w.moveTask(Day.MONDAY, 1, Day.TUESDAY, 5); fail("bad rank should be rejected"); }
        catch (IllegalArgumentException expected) { }
        assertEquals(1, w.taskCount(Day.MONDAY));
        assertEquals(0, w.taskCount(Day.TUESDAY));
    }
    @Test(expected = IllegalArgumentException.class)
    public void moveFromEmptyDay() { new TaskList().moveTask(Day.MONDAY, 1, Day.TUESDAY); }

    // ---------- completeTask / completedCount ----------
    @Test public void completeMovesTaskToLog() {
        TaskList w = monday("A", "B");
        w.completeTask(Day.MONDAY, 1);
        assertEquals(1, w.taskCount(Day.MONDAY));
        assertEquals(1, w.completedCount());
        assertEquals("A (Monday)", w.describeCompleted());
    }
    @Test(expected = IllegalArgumentException.class)
    public void completeOnEmptyDay() { new TaskList().completeTask(Day.FRIDAY, 1); }
    @Test public void completedLogStartsEmpty() {
        assertEquals(0, new TaskList().completedCount());
        assertEquals("No completed tasks", new TaskList().describeCompleted());
    }

    // ---------- undo ----------
    @Test public void undoAddRemovesTask() {
        TaskList w = monday("A");
        assertTrue(w.undo());
        assertEquals(0, w.taskCount(Day.MONDAY));
    }
    @Test public void undoOnFreshListReturnsFalse() { assertFalse(new TaskList().undo()); }
    @Test public void undoMoreTimesThanActions() {
        TaskList w = monday("A");
        assertTrue(w.undo());
        assertFalse(w.undo());
        assertFalse(w.undo());
    }
    @Test public void undoRemoveRestoresRank() {
        TaskList w = monday("A", "B", "C");
        w.removeTask(Day.MONDAY, 2);
        w.undo();
        assertEquals("1. A\n2. B\n3. C", w.describeDay(Day.MONDAY));
    }
    @Test public void undoRankChangeRestoresOrder() {
        TaskList w = monday("A", "B", "C", "D");
        w.changeRank(Day.MONDAY, 4, 1);
        w.undo();
        assertEquals("1. A\n2. B\n3. C\n4. D", w.describeDay(Day.MONDAY));
    }
    @Test public void undoMoveRestoresBothDays() {
        TaskList w = monday("A", "B");
        w.moveTask(Day.MONDAY, 1, Day.WEDNESDAY);
        w.undo();
        assertEquals("1. A\n2. B", w.describeDay(Day.MONDAY));
        assertEquals(0, w.taskCount(Day.WEDNESDAY));
    }
    @Test public void undoCompleteRestoresTaskAndLog() {
        TaskList w = monday("A", "B");
        w.completeTask(Day.MONDAY, 1);
        w.undo();
        assertEquals("1. A\n2. B", w.describeDay(Day.MONDAY));
        assertEquals(0, w.completedCount());
    }

    // ---------- redo ----------
    @Test public void redoPutsTaskBackAtSameRank() {
        TaskList w = monday("A", "B");
        w.addTask(Day.MONDAY, "C", 1);
        w.undo();
        assertTrue(w.redo());
        assertEquals("1. C\n2. A\n3. B", w.describeDay(Day.MONDAY));
    }
    @Test public void redoWithNothingToRedo() { assertFalse(new TaskList().redo()); }
    @Test public void newChangeAfterUndoClearsRedo() {
        TaskList w = monday("A");
        w.undo();
        w.addTask(Day.MONDAY, "B");
        assertFalse(w.redo());
    }
    @Test public void everyTypeSurvivesUndoAllThenRedoAll() {
        TaskList w = monday("A", "B", "C");
        w.addTask(Day.MONDAY, "D", 2);
        w.changeRank(Day.MONDAY, 4, 1);
        w.moveTask(Day.MONDAY, 2, Day.TUESDAY);
        w.completeTask(Day.MONDAY, 1);
        w.removeTask(Day.TUESDAY, 1);
        String monAfter = w.describeDay(Day.MONDAY);
        String tueAfter = w.describeDay(Day.TUESDAY);
        int doneAfter = w.completedCount();
        while (w.undo()) { }                       // undoes everything, including the first three adds
        assertEquals("No tasks on this day", w.describeDay(Day.MONDAY));
        assertEquals(0, w.taskCount(Day.TUESDAY));
        assertEquals(0, w.completedCount());
        while (w.redo()) { }
        assertEquals(monAfter, w.describeDay(Day.MONDAY));
        assertEquals(tueAfter, w.describeDay(Day.TUESDAY));
        assertEquals(doneAfter, w.completedCount());
    }

    // ---------- rollOver (stretch goal) ----------
    @Test public void rollOverMovesEverythingInOrder() {
        TaskList w = monday("A", "B");
        w.addTask(Day.TUESDAY, "X");
        assertEquals(2, w.rollOver(Day.MONDAY));
        assertEquals(0, w.taskCount(Day.MONDAY));
        assertEquals("1. X\n2. A\n3. B", w.describeDay(Day.TUESDAY));
    }
    @Test public void rollOverOnEmptyDayMovesNothing() {
        assertEquals(0, new TaskList().rollOver(Day.MONDAY));
    }
    @Test(expected = IllegalArgumentException.class)
    public void rollOverLastDayRejected() { new TaskList().rollOver(Day.SATURDAY); }

    // ---------- multi-select (stretch goal): moveTasks / completeTasks / moveToTop ----------
    @Test public void moveSeveralKeepsTheirOrder() {
        TaskList w = monday("A", "B", "C", "D");
        assertEquals(2, w.moveTasks(Day.MONDAY, new int[] {3, 1}, Day.TUESDAY));   // order typed does not matter
        assertEquals("1. B\n2. D", w.describeDay(Day.MONDAY));
        assertEquals("1. A\n2. C", w.describeDay(Day.TUESDAY));
    }
    @Test public void oneUndoReversesAWholeMoveSelection() {
        TaskList w = monday("A", "B", "C", "D");
        w.moveTasks(Day.MONDAY, new int[] {1, 3, 4}, Day.TUESDAY);
        assertTrue(w.undo());
        assertEquals("1. A\n2. B\n3. C\n4. D", w.describeDay(Day.MONDAY));
        assertEquals(0, w.taskCount(Day.TUESDAY));
        assertTrue(w.redo());                                      // one redo re-applies the batch
        assertEquals("1. B", w.describeDay(Day.MONDAY));
        assertEquals("1. A\n2. C\n3. D", w.describeDay(Day.TUESDAY));
    }
    @Test public void moveSeveralToSameDayChangesNothing() {
        TaskList w = monday("A", "B");
        assertEquals(0, w.moveTasks(Day.MONDAY, new int[] {1, 2}, Day.MONDAY));
        assertEquals(2, w.taskCount(Day.MONDAY));
    }
    @Test public void badSelectionLeavesEverythingUnchanged() {
        TaskList w = monday("A", "B", "C");
        int[][] bad = { {1, 9}, {0}, {-2, 1}, {2, 2}, {} };       // out of range, zero, negative, repeated, empty
        for (int[] selection : bad) {
            try { w.moveTasks(Day.MONDAY, selection, Day.TUESDAY); fail("selection should be rejected"); }
            catch (IllegalArgumentException expected) { }
        }
        assertEquals("1. A\n2. B\n3. C", w.describeDay(Day.MONDAY));
        assertEquals(0, w.taskCount(Day.TUESDAY));
        w.undo();                                                  // undoes the last ADD, so no batch was recorded
        assertEquals("1. A\n2. B", w.describeDay(Day.MONDAY));
    }
    @Test(expected = IllegalArgumentException.class)
    public void moveSeveralFromEmptyDay() { new TaskList().moveTasks(Day.MONDAY, new int[] {1}, Day.TUESDAY); }
    @Test(expected = IllegalArgumentException.class)
    public void moveSeveralNullSelection() { monday("A").moveTasks(Day.MONDAY, null, Day.TUESDAY); }

    @Test public void completeSeveralAndUndoOnce() {
        TaskList w = monday("A", "B", "C", "D");
        assertEquals(2, w.completeTasks(Day.MONDAY, new int[] {2, 4}));
        assertEquals("1. A\n2. C", w.describeDay(Day.MONDAY));
        assertEquals(2, w.completedCount());
        assertTrue(w.undo());
        assertEquals("1. A\n2. B\n3. C\n4. D", w.describeDay(Day.MONDAY));
        assertEquals(0, w.completedCount());
        assertTrue(w.redo());
        assertEquals(2, w.completedCount());
    }
    @Test(expected = IllegalArgumentException.class)
    public void completeSeveralOnEmptyDay() { new TaskList().completeTasks(Day.FRIDAY, new int[] {1}); }
    @Test(expected = IllegalArgumentException.class)
    public void completeSeveralRepeatedRank() { monday("A", "B").completeTasks(Day.MONDAY, new int[] {1, 1}); }

    @Test public void raiseSeveralToTheTop() {
        TaskList w = monday("A", "B", "C", "D", "E");
        assertEquals(2, w.moveToTop(Day.MONDAY, new int[] {5, 3}));
        assertEquals("1. C\n2. E\n3. A\n4. B\n5. D", w.describeDay(Day.MONDAY));
        assertTrue(w.undo());                                      // one undo restores the original order
        assertEquals("1. A\n2. B\n3. C\n4. D\n5. E", w.describeDay(Day.MONDAY));
        assertTrue(w.redo());
        assertEquals("1. C\n2. E\n3. A\n4. B\n5. D", w.describeDay(Day.MONDAY));
    }
    @Test public void raiseTasksAlreadyOnTopChangesNothing() {
        TaskList w = monday("A", "B", "C");
        assertEquals(0, w.moveToTop(Day.MONDAY, new int[] {1, 2}));
        w.undo();                                                  // undoes the last ADD, so nothing was recorded
        assertEquals("1. A\n2. B", w.describeDay(Day.MONDAY));
    }
    @Test(expected = IllegalArgumentException.class)
    public void raiseOutOfRange() { monday("A", "B").moveToTop(Day.MONDAY, new int[] {3}); }

    @Test public void rollOverIsOneUndo() {
        TaskList w = monday("A", "B", "C");
        w.rollOver(Day.MONDAY);
        assertTrue(w.undo());
        assertEquals("1. A\n2. B\n3. C", w.describeDay(Day.MONDAY));
        assertEquals(0, w.taskCount(Day.TUESDAY));
    }

    // ---------- read-only views ----------
    @Test public void taskCountAndEmptyDay() {
        TaskList w = monday("A", "B", "C");
        assertEquals(3, w.taskCount(Day.MONDAY));
        assertEquals(0, w.taskCount(Day.SUNDAY));
    }
    @Test(expected = IllegalArgumentException.class)
    public void taskCountNullDay() { new TaskList().taskCount(null); }
    @Test public void describeDayEmpty() {
        assertEquals("No tasks on this day", new TaskList().describeDay(Day.MONDAY));
    }
    @Test public void describeWeekShowsCounts() {
        TaskList w = monday("A", "B");
        String week = w.describeWeek();
        assertTrue(week.contains("Monday: 2 tasks"));
        assertTrue(week.contains("Sunday: No tasks"));
    }
}