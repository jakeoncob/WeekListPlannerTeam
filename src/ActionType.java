/** The kinds of change that can be undone and redone. BATCH groups several steps into one. */
public enum ActionType { ADD, REMOVE, RANK_CHANGE, MOVE, COMPLETE, BATCH }