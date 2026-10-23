/** A validated to-do item. */
public class Task {
    /**longest task name allowed is 50 chars*/
    public final int MAX_LENGTH = 50; 
    private final String name;
    
    /**Runs when new task is created
     * @param name */
    public Task(String name) 
    {
        if (name == null || name.trim().isEmpty() || name.trim().length() > MAX_LENGTH)
            throw new IllegalArgumentException("Task name must be 1-50 characters");
        this.name = name.trim(); //saves name w/o extra spaces
    }
    
    /**public so gives other code access to task name
     * @return name*/
    public String getName() { return name; }
    @Override public String toString() { return name; } //controls how task appears when printed
}
