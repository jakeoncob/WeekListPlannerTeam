/** A completed task and its original day. */
public class CompletedEntry {
    private final Task task;
    private final Day fromDay;
    
    /**Constructor method for completed entries
     * @param task from tasklist
     * @param fromDay from specific day*/
    public CompletedEntry(Task task, Day fromDay) {
        if (task == null || fromDay == null)
            throw new IllegalArgumentException("Task and day are required");
        this.fromDay = fromDay;
        this.task = task;
    }

    /**Gets task and returns it
     * @return task*/
    public Task getTask() { return task; }
    /**Gets day and returns it 
     * @return day*/
    public Day getDay() { return fromDay; }
    @Override public String toString() { return task + " (" + fromDay + ")"; }
}
