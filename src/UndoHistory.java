/**
 * Two piles of Actions, newest on top (position 0 = the front of the chain).
 * Pushing and popping at the front are both O(1) with the provided LinkedChain.
 */
public class UndoHistory {
    private final LinkedChain<Action> undoStack = new LinkedChain<Action>();
    private final LinkedChain<Action> redoStack = new LinkedChain<Action>();

    /** Newest undo action, or null if there is none.
     * @param a the new action to reccord */
    public void record(Action a) {
        undoStack.add(a);
        redoStack.clear();
    }

    /**
     * Removes and returns the newest action from the undo stack.
     *
     * @return the newest action, or null if there are no actions to undo
     */
    public Action popUndo() { return undoStack.remove(); }
    /**
     * Removes and returns the newest action from the redo stack.
     *
     * @return the newest action, or null if there are no actions to redo
     */
    public Action popRedo() { return redoStack.remove(); }
    /** Used after a redo. Does NOT clear the redo pile. */
    public void pushUndo(Action a) { undoStack.add(a); }
    /** Used after an undo. */
    public void pushRedo(Action a) { redoStack.add(a); }
    /**
     * Returns the number of actions currently available to undo.
     *
     * @return the number of actions in the undo stack
     */
    public int undoCount() { return undoStack.size(); }
    /**
     * Returns the number of actions currently available to redo.
     *
     * @return the number of actions in the redo stack
     */
    public int redoCount() { return redoStack.size(); }
}