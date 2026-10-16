import static org.junit.Assert.*;
import org.junit.Test;
/** JUnit 4 application tests, including normal and invalid inputs. */
public class TaskListTest {
    @Test public void addAndGet() {
        TaskList list = new TaskList();
        list.addTask("Monday", "Math");
        list.addTask("Monday", "Study");
        assertEquals(2, list.countTasks("Monday"));
        assertEquals("Math", list.getTasks("Monday")[0].getName());
        assertEquals("Study", list.getTasks("Monday")[1].getName());
    }
    @Test(expected = IllegalArgumentException.class)
    public void addRejectsBlank() { new TaskList().addTask("Monday", "  "); }
    @Test(expected = IllegalArgumentException.class)
    public void addRejectsBadDay() { new TaskList().addTask("Munsday", "Math"); }
    @Test public void removeAndEmpty() {
        TaskList list = new TaskList();
        list.addTask("Tuesday", "Read");
        assertEquals("Read", list.removeTask("Tuesday", 0).getName());
        assertEquals(0, list.countTasks("Tuesday"));
        assertEquals(0, list.getTasks("Tuesday").length);
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void removeEmpty() { new TaskList().removeTask("Tuesday", 0); }
    @Test public void moveAndSameDay() {
        TaskList list = new TaskList();
        list.addTask("Monday", "Math");
        assertFalse(list.moveTask("Monday", 0, "Monday"));
        assertTrue(list.moveTask("Monday", 0, "Wednesday"));
        assertEquals(0, list.countTasks("Monday"));
        assertEquals("Math", list.getTasks("Wednesday")[0].getName());
    }
    @Test(expected = IllegalArgumentException.class)
    public void moveBadDayDoesNotLoseTask() {
        TaskList list = new TaskList();
        list.addTask("Monday", "Math");
        try { list.moveTask("Monday", 0, "Munsday"); }
        finally { assertEquals(1, list.countTasks("Monday")); }
    }
    @Test public void completeAndLog() {
        TaskList list = new TaskList();
        list.addTask("Friday", "Essay");
        assertEquals("Friday", list.completeTask("Friday", 0).getDay());
        assertEquals(0, list.countTasks("Friday"));
        assertEquals("Essay", list.getCompleted()[0].getTask().getName());
    }
    @Test(expected = IndexOutOfBoundsException.class)
    public void completeEmpty() { new TaskList().completeTask("Friday", 0); }
    @Test(expected = IllegalArgumentException.class)
    public void invalidDayForRead() { new TaskList().getTasks(null); }
    @Test public void completedInitiallyEmpty() {
        assertEquals(0, new TaskList().getCompleted().length);
    }
    @Test public void validDayNormalization() {
        assertEquals("Monday", TaskList.validDay(" monday "));
    }
    @Test(expected = IllegalArgumentException.class)
    public void validDayRejectsInvalid() { TaskList.validDay("abc"); }
    @Test(expected = IllegalArgumentException.class)
    public void taskRejectsNull() { new Task(null); }
    @Test public void taskNameAndString() {
        Task t = new Task(" Read ");
        assertEquals("Read", t.getName());
        assertEquals("Read", t.toString());
    }
    @Test(expected = IllegalArgumentException.class)
    public void completedEntryRejectsNull() { new CompletedEntry(null, "Monday"); }
    @Test public void completedEntryAccessors() {
        Task t = new Task("Read");
        CompletedEntry e = new CompletedEntry(t, "monday");
        assertSame(t, e.getTask());
        assertEquals("Monday", e.getDay());
        assertEquals("Read (Monday)", e.toString());
    }
}
