/** WeekList application rules; LinkedChain owns all collection storage. */
public class TaskList {
    private static final String[] DAYS = {
        "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    };
    private final LinkedChain<Task>[] days;
    private final LinkedChain<CompletedEntry> completed;
    @SuppressWarnings("unchecked")
    public TaskList() {
        days = (LinkedChain<Task>[])new LinkedChain<?>[7];
        for (int i = 0; i < 7; i++) days[i] = new LinkedChain<Task>();
        completed = new LinkedChain<CompletedEntry>();
    }
    public static String validDay(String day) {
        if (day != null) {
            for (String candidate : DAYS)
                if (candidate.equalsIgnoreCase(day.trim())) return candidate;
        }
        throw new IllegalArgumentException("Enter a valid day: Sunday through Saturday");
    }
    private int index(String day) {
        String valid = validDay(day);
        for (int i = 0; i < DAYS.length; i++)
            if (DAYS[i].equals(valid)) return i;
        throw new IllegalArgumentException("Invalid day");
    }
    /** Add a task to the bottom of the chosen day's list. */
    public void addTask(String day, String name) {
        int i = index(day);
        Task task = new Task(name);
        days[i].addToEnd(task);
    }
    /** Return a snapshot; changes to the returned array do not alter storage. */
    public Task[] getTasks(String day) {
        LinkedChain<Task> chain = days[index(day)];
        return chain.toArray(new Task[chain.size()]);
    }
    public int countTasks(String day) { return days[index(day)].size(); }
    /** Remove the zero-based position, or throw for an invalid position. */
    public Task removeTask(String day, int position) {
        LinkedChain<Task> chain = days[index(day)];
        Task selected = chain.get(position);
        chain.remove(selected);
        return selected;
    }
    /** Complete an item and log its originating day. */
    public CompletedEntry completeTask(String day, int position) {
        String valid = validDay(day);
        Task task = removeTask(valid, position);
        CompletedEntry entry = new CompletedEntry(task, valid);
        completed.addToEnd(entry);
        return entry;
    }
    /** Move an item to another day, appending it at the bottom. Same-day moves do nothing. */
    public boolean moveTask(String from, int position, String to) {
        int source = index(from);
        int target = index(to);
        Task selected = days[source].get(position);
        if (source == target) return false;
        days[source].remove(selected);
        days[target].addToEnd(selected);
        return true;
    }
    public CompletedEntry[] getCompleted() {
        return completed.toArray(new CompletedEntry[completed.size()]);
    }
}
