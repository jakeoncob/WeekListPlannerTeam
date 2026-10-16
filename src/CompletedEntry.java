/** A completed task and its original day. */
public class CompletedEntry {
    private final Task task;
    private final String day;
    public CompletedEntry(Task task, String day) {
        if (task == null) throw new IllegalArgumentException("Task required");
        this.day = TaskList.validDay(day);
        this.task = task;
    }
    public Task getTask() { return task; }
    public String getDay() { return day; }
    @Override public String toString() { return task + " (" + day + ")"; }
}
