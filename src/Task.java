/** A validated to-do item. */
public class Task {
    private final String name;
    public Task(String name) {
        if (name == null || name.trim().isEmpty() || name.trim().length() > 100)
            throw new IllegalArgumentException("Task name must be 1-100 characters");
        this.name = name.trim();
    }
    public String getName() { return name; }
    @Override public String toString() { return name; }
}
