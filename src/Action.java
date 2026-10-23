/**
 * A note about ONE change, with enough detail to reverse it.
 * It stores only the change, never copies of lists. Ranks are the user's
 * numbers (1 = top). Fields a type does not use are null (objects) or 0 (numbers).
 * ADD: task, toDay, toRank          REMOVE: task, fromDay, fromRank
 * RANK_CHANGE: task, fromDay, fromRank, toDay (same day), toRank
 * MOVE: task, fromDay, fromRank, toDay, toRank      COMPLETE: task, fromDay, fromRank
 * BATCH: only a list of steps (a LinkedChain of Actions). Undoing a BATCH
 *        undoes its steps newest-first, so one undo reverses a whole selection.
 */
public class Action {
    private final ActionType type;
    private final Task task;
    private final Day fromDay;
    private final int fromRank;
    private final Day toDay;
    private final int toRank;
    private final LinkedChain<Action> steps;     // only used by BATCH

    /**
     * @param type 
     * @param task
     * @param fromDay
     * @param fromRank
     * @param toDay
     * @param toRank
     */
    public Action(ActionType type, Task task, Day fromDay, int fromRank, Day toDay, int toRank) {
        this(type, task, fromDay, fromRank, toDay, toRank, null);
    }

    /** Groups already-performed steps (oldest first) into a single undoable action. 
     * @param steps 
     * @return New Action*/
    public static Action batch(LinkedChain<Action> steps) {
        return new Action(ActionType.BATCH, null, null, 0, null, 0, steps);
    }

    private Action(ActionType type, Task task, Day fromDay, int fromRank,
                   Day toDay, int toRank, LinkedChain<Action> steps) {
        if (type == null)
            throw new IllegalArgumentException("Action needs a type");
        if (type == ActionType.BATCH) {
            if (steps == null || steps.isEmpty())
                throw new IllegalArgumentException("A batch needs at least one step");
        } else if (task == null || steps != null) {
            throw new IllegalArgumentException("Action needs a task");
        }
        this.type = type;
        this.task = task;
        this.fromDay = fromDay;
        this.fromRank = fromRank;
        this.toDay = toDay;
        this.toRank = toRank;
        this.steps = steps;
    }


    /**@return type of action*/
    public ActionType getType() { return type; }
    /**@return task*/
    public Task getTask() { return task; }
    /**@return fromDay*/
    public Day getFromDay() { return fromDay; }
    /**@return fromRank*/
    public int getFromRank() { return fromRank; }
    /**@return toDay*/
    public Day getToDay() { return toDay; }
    /**@return toRank*/
    public int getToRank() { return toRank; }

    /** The steps of a BATCH, oldest first. Empty for every other type. 
     * @return steps*/
    public Action[] getSteps() {
        if (steps == null) return new Action[0];
        return steps.toArray(new Action[steps.size()]);
    }

    @Override public String toString() {
        if (type == ActionType.BATCH) return "BATCH (" + steps.size() + " steps)";
        return type + " " + task + " (" + fromDay + " " + fromRank + " -> " + toDay + " " + toRank + ")";
    }
}