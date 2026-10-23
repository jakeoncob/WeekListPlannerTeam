import java.util.Arrays;
import java.util.EnumMap;

/** WeekList application rules; LinkedChain owns all collection storage.
 * Manages all the tasks for each day of the week. User ranks starts at 1 
 * while linkedchain pos starts at 0, then changes are validated so it can be redone and undone */
public class TaskList {
    // stores task list for each day
    private final EnumMap<Day, LinkedChain<Task>> days = new EnumMap<Day, LinkedChain<Task>>(Day.class);
    // stores records of completed task
    private final LinkedChain<CompletedEntry> completedLog = new LinkedChain<CompletedEntry>();
    // tracks changes so it can be redo and undo
    private final UndoHistory history = new UndoHistory();
    
    
    
    /** Creates a task list with an empty task chain for each day of the week.*/
    public TaskList() {
        for (Day d : Day.values()) days.put(d, new LinkedChain<Task>());
    }
    //if day is null throw exception
    private LinkedChain<Task> nonEmptyChainFor(Day day) {
        LinkedChain<Task> chain = chainFor(day);
        if (chain.isEmpty()) throw new IllegalArgumentException("No tasks on this day");
        return chain;
    }
    /**
     * Gets the task chain associated with the specified day. */
    private LinkedChain<Task> chainFor(Day day) {
        if (day == null) throw new IllegalArgumentException("Day is required");
        return days.get(day);
    }
    
    // checks whether task rank is within range
    private void checkRank(int rank, int max) {
        if (rank < 1 || rank > max)
            throw new IllegalArgumentException("Rank must be between 1 and " + max);
    }
    
    private int[] checkedSelection(int[] ranks, int size) {
        if (ranks == null || ranks.length == 0)
            throw new IllegalArgumentException("Select at least one task");
        int[] sorted = ranks.clone();
        Arrays.sort(sorted);
        for (int i = 0; i < sorted.length; i++) {
            checkRank(sorted[i], size);
            if (i > 0 && sorted[i] == sorted[i - 1])
                throw new IllegalArgumentException("Each task can only be picked once");
        }
        return sorted;
    }
    
    
    private Action doMove(Day fromDay, int fromRank, Day toDay, int toRank) {
        Task task = chainFor(fromDay).removeAt(fromRank - 1);
        chainFor(toDay).add(toRank - 1, task);
        return new Action(ActionType.MOVE, task, fromDay, fromRank, toDay, toRank);
    }
    private Action doRankChange(Day day, int fromRank, int toRank) {
        LinkedChain<Task> chain = chainFor(day);
        Task task = chain.removeAt(fromRank - 1);
        chain.add(toRank - 1, task);
        return new Action(ActionType.RANK_CHANGE, task, day, fromRank, day, toRank);
    }
    private Action doComplete(Day day, int rank) {
        Task task = chainFor(day).removeAt(rank - 1);
        completedLog.add(new CompletedEntry(task, day));          // newest at the front
        return new Action(ActionType.COMPLETE, task, day, rank, null, 0);
    }
    
    /**
     * Adds a new task to the specified day at the given rank.
     *
     * @param day the day on which to add the task
     * @param name the name of the new task
     * @param rank the position at which to insert the task, starting at 1
     * @throws IllegalArgumentException if the day or task name is invalid,
     *         or the rank is outside the allowed range
     */
    public void addTask(Day day, String name, int rank) {
        LinkedChain<Task> chain = chainFor(day);
        Task task = new Task(name);
        checkRank(rank, chain.size() + 1);
        chain.add(rank - 1, task);
        history.record(new Action(ActionType.ADD, task, null, 0, day, rank));
    }
    
    
    
    
   
    /**
     * Adds a task to the bottom of the specified day's list.
     *
     * @param day the day on which to add the task
     * @param name the name of the new task
     * @throws IllegalArgumentException if the day or task name is invalid
     */
    public void addTask(Day day, String name) {
        addTask(day, name, chainFor(day).size() + 1);
    }
    
    /**
     * Removes a task at the specified rank and records the removal for undo.
     *
     * @param day the day containing the task
     * @param rank the rank of the task to remove, starting at 1
     * @return the removed task
     * @throws IllegalArgumentException if the day has no tasks or the rank is invalid
     */
    public Task removeTask(Day day, int rank) {
        LinkedChain<Task> chain = nonEmptyChainFor(day);
        checkRank(rank, chain.size());
        Task task = chain.removeAt(rank - 1);
        history.record(new Action(ActionType.REMOVE, task, day, rank, null, 0));
        return task;
    }
 
    /**
     * Changes a task's position within the specified day.
     *
     * @param day the day containing the task
     * @param fromRank the task's current rank
     * @param toRank the desired rank
     * @return true if the task was moved, or false if both ranks are equal
     * @throws IllegalArgumentException if the day has no tasks or either rank is invalid
     */
    public boolean changeRank(Day day, int fromRank, int toRank) {
        LinkedChain<Task> chain = nonEmptyChainFor(day);
        checkRank(fromRank, chain.size());
        checkRank(toRank, chain.size());
        if (fromRank == toRank) return false;
        history.record(doRankChange(day, fromRank, toRank));
        return true;
    }
    
    /**
     * Moves a task from one day to another at the specified destination rank.
     *
     * @param fromDay the day the task is moved from
     * @param fromRank the task's original rank
     * @param toDay the destination day
     * @param toRank the task's destination rank
     * @return true if the task was moved, or false if both days are the same
     * @throws IllegalArgumentException if the source day is empty or either rank is invalid
     */
    public boolean moveTask(Day fromDay, int fromRank, Day toDay, int toRank) {
        LinkedChain<Task> from = nonEmptyChainFor(fromDay);
        LinkedChain<Task> to = chainFor(toDay);
        if (fromDay == toDay) return false;
        checkRank(fromRank, from.size());
        checkRank(toRank, to.size() + 1);
        history.record(doMove(fromDay, fromRank, toDay, toRank));
        return true;
    }
    
    /**
     * Moves a task to the bottom of another day's list.
     *
     * @param fromDay the day the task is moved from
     * @param fromRank the task's original rank
     * @param toDay the destination day
     * @return true if the task was moved, or false if both days are the same
     * @throws IllegalArgumentException if the source day is empty or a day is invalid
     */
    public boolean moveTask(Day fromDay, int fromRank, Day toDay) {
        return moveTask(fromDay, fromRank, toDay, chainFor(toDay).size() + 1);
    }
    
    /**
     * Marks the task at the specified rank as complete.
     *
     * @param day the day containing the task
     * @param rank the rank of the task to complete
     * @throws IllegalArgumentException if the day has no tasks or the rank is invalid
     */
    public void completeTask(Day day, int rank) {
        LinkedChain<Task> chain = nonEmptyChainFor(day);
        checkRank(rank, chain.size());
        history.record(doComplete(day, rank));
    }
    
    /**
     * Moves multiple tasks from one day to another, preserving their original order.
     * Records the moves as one batch action so they can be undone together.
     *
     * @param fromDay the day the tasks are moved from
     * @param ranks the ranks of the tasks to move, starting at 1
     * @param toDay the day the tasks are moved to
     * @return the number of tasks moved, or 0 if both days are the same
     * @throws IllegalArgumentException if the source day is empty, the selection
     *         is invalid, or either day is null
     */
    public int moveTasks(Day fromDay, int[] ranks, Day toDay) {
        LinkedChain<Task> from = nonEmptyChainFor(fromDay);
        chainFor(toDay);
        int[] sorted = checkedSelection(ranks, from.size());
        if (fromDay == toDay) return 0;
        LinkedChain<Action> steps = new LinkedChain<Action>();
        for (int i = 0; i < sorted.length; i++) 
        {
            steps.addToEnd(doMove(fromDay, sorted[i] - i, toDay, chainFor(toDay).size() + 1));
        }
    history.record(Action.batch(steps));
    return sorted.length;
    }
       
    /**
     * Marks multiple tasks as complete and records them as one batch action.
     *
     * @param day the day containing the tasks
     * @param ranks the ranks of the tasks to complete, starting at 1
     * @return the number of tasks completed
     * @throws IllegalArgumentException if the day is empty or the selection
     *         contains invalid or duplicate ranks
     */
    public int completeTasks(Day day, int[] ranks) {
        LinkedChain<Task> chain = nonEmptyChainFor(day);
        int[] sorted = checkedSelection(ranks, chain.size());
        LinkedChain<Action> steps = new LinkedChain<Action>();
        for (int i = 0; i < sorted.length; i++) {
            steps.addToEnd(doComplete(day, sorted[i] - i));
        }
        history.record(Action.batch(steps));
        return sorted.length;
    }
    
    /**
     * Moves the selected tasks to the top of the day's list, preserving their order.
     * Records the changes as one batch action.
     *
     * @param day the day containing the tasks
     * @param ranks the ranks of the tasks to move to the top, starting at 1
     * @return the number of tasks moved, or 0 if no changes were necessary
     * @throws IllegalArgumentException if the day is empty or the selection
     *         contains invalid or duplicate ranks
     */
    public int moveToTop(Day day, int[] ranks) {
        LinkedChain<Task> chain = nonEmptyChainFor(day);
        int[] sorted = checkedSelection(ranks, chain.size());
        LinkedChain<Action> steps = new LinkedChain<Action>();
        for (int i = 0; i < sorted.length; i++) 
        {
            if (sorted[i] != i + 1) steps.addToEnd(doRankChange(day, sorted[i], i + 1));
        }
        if (steps.isEmpty()) return 0;
        history.record(Action.batch(steps));
        return steps.size();
    }
    
    /**
     * Reverses the most recent undoable action and makes it available for redo.
     *
     * @return true if an action was undone, or false if there was nothing to undo
     */
    public boolean undo() {
        Action a = history.popUndo();
        if (a == null) return false;
        reverse(a);
        history.pushRedo(a);
        return true;
    }
 
    /**
     * Reapplies the most recently undone action and makes it available for undo.
     *
     * @return true if an action was redone, or false if there was nothing to redo
     */
    public boolean redo() {
        Action a = history.popRedo();
        if (a == null) return false;
        reapply(a);
        history.pushUndo(a);
        return true;
    }
    
    /**
     * Reverses an action by restoring the task list to its previous state.
     * Batch actions are reversed in reverse order so that earlier changes
     * can be safely undone.
     *
     * @param a the action to reverse
     */
    @SuppressWarnings("incomplete-switch")
    private void reverse(Action a) {
        switch (a.getType()) {
            case BATCH: {
                Action[] steps = a.getSteps();
                for (int i = steps.length - 1; i >= 0; i--) reverse(steps[i]);
                break;
            }
            case ADD:
                chainFor(a.getToDay()).removeAt(a.getToRank() - 1);
                break;
            case REMOVE:
                chainFor(a.getFromDay()).add(a.getFromRank() - 1, a.getTask());
                break;
            case RANK_CHANGE: {
                LinkedChain<Task> chain = chainFor(a.getFromDay());
                Task t = chain.removeAt(a.getToRank() - 1);
                chain.add(a.getFromRank() - 1, t);
                break;
            }
            case MOVE: {
                Task t = chainFor(a.getToDay()).removeAt(a.getToRank() - 1);
                chainFor(a.getFromDay()).add(a.getFromRank() - 1, t);
                break;
            }
            case COMPLETE:
                completedLog.remove();                            // newest entry
                chainFor(a.getFromDay()).add(a.getFromRank() - 1, a.getTask());
                break;
        }
    }
    
    /**
     * Reapplies a previously reversed action to restore the resulting task list.
     * Batch actions are reapplied in their original order.
     *
     * @param a the action to reapply
     */
    @SuppressWarnings("incomplete-switch")
    private void reapply(Action a) {
        switch (a.getType()) {
            case BATCH: {
                for (Action step : a.getSteps()) reapply(step);
                break;
            }
            case ADD:
                chainFor(a.getToDay()).add(a.getToRank() - 1, a.getTask());
                break;
            case REMOVE:
                chainFor(a.getFromDay()).removeAt(a.getFromRank() - 1);
                break;
            case RANK_CHANGE: {
                LinkedChain<Task> chain = chainFor(a.getFromDay());
                Task t = chain.removeAt(a.getFromRank() - 1);
                chain.add(a.getToRank() - 1, t);
                break;
            }
            case MOVE: {
                Task t = chainFor(a.getFromDay()).removeAt(a.getFromRank() - 1);
                chainFor(a.getToDay()).add(a.getToRank() - 1, t);
                break;
            }
            case COMPLETE:
                chainFor(a.getFromDay()).removeAt(a.getFromRank() - 1);
                completedLog.add(new CompletedEntry(a.getTask(), a.getFromDay()));
                break;
        }
    }
    
    /**
     * Moves all tasks from the specified day to the following day.
     *
     * @param from the day whose tasks will be moved
     * @return the number of tasks moved
     * @throws IllegalArgumentException if from is null or is the last day of the week
     */
    public int rollOver(Day from) {
        LinkedChain<Task> source = chainFor(from);
        Day[] all = Day.values();
        if (from.ordinal() == all.length - 1)
            throw new IllegalArgumentException(from + " is the last day, so there is no next day");
        if (source.isEmpty()) return 0;
        int[] everyRank = new int[source.size()];
        for (int i = 0; i < everyRank.length; i++) everyRank[i] = i + 1;
        return moveTasks(from, everyRank, all[from.ordinal() + 1]);
    }
    
    /**
     * Gets the number of tasks scheduled for the specified day.
     *
     * @param day the day to count tasks for
     * @return the number of tasks on that day
     * @throws IllegalArgumentException if day is null
     */
    public int taskCount(Day day) { return chainFor(day).size(); }
    /**
     * Gets the total number of completed-task records.
     *
     * @return the number of entries in the completed-task log
     */
    public int completedCount() { return completedLog.size(); }
    
    /**
     * Creates a numbered text description of the tasks scheduled for a day.
     *
     * @param day the day whose tasks should be described
     * @return a numbered list of tasks, or a message if the day has no tasks
     * @throws IllegalArgumentException if day is null
     */
    public String describeDay(Day day) {
        LinkedChain<Task> chain = chainFor(day);
        if (chain.isEmpty()) return "No tasks on this day";
        Task[] tasks = chain.toArray(new Task[chain.size()]);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tasks.length; i++) {
            if (i > 0) sb.append("\n");
            sb.append(i + 1).append(". ").append(tasks[i].getName());
        }
        return sb.toString();
    }
    
    /**
     * Creates a text summary of the number of tasks scheduled for each day.
     *
     * @return a multiline summary of the task counts for the entire week
     */
    public String describeWeek() {
        StringBuilder sb = new StringBuilder();
        for (Day d : Day.values()) {
            int n = taskCount(d);
            sb.append(d).append(": ");
            sb.append(n == 0 ? "No tasks" : n + (n == 1 ? " task" : " tasks")).append("\n");
        }
        return sb.toString();
    }
 
    /**
     * Creates a text description of the completed-task log, with the newest
     * completion listed first.
     *
     * @return the completed-task entries as multiline text, or a message if
     *         there are no completed tasks
     */
    public String describeCompleted() {
        if (completedLog.isEmpty()) return "No completed tasks";
        CompletedEntry[] entries = completedLog.toArray(new CompletedEntry[completedLog.size()]);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < entries.length; i++) {
            if (i > 0) sb.append("\n");
            sb.append(entries[i]);
        }
        return sb.toString();
    }
    
}
