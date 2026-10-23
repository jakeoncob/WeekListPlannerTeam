import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * The menu. It reads typing, checks the FORMAT (number? real day?), calls
 * TaskList, and prints results. It makes no to-do rule decisions.
 */
public class TaskListApp {
    private final TaskList taskList = new TaskList();
    private final Scanner input;

    /**
     * Creates the application using the specified input source.
     *
     * @param input the Scanner used to read user input
     * @throws NullPointerException if input is null
     */
    public TaskListApp(Scanner input) { this.input = input; }

    /**
     * Starts the WeekList application using keyboard input.
     *
     * @param args command-line arguments, which are not used
     */
    public static void main(String[] args) {
        new TaskListApp(new Scanner(System.in)).run();
    }

    /**
     * Runs the main menu loop until the user quits or input ends.
     * Displays error messages when a menu operation receives invalid input.
     */
    public void run() {
        System.out.println("WeekList");
        try {
            while (true) {
                printMenu();
                String choice = readLine("> ").trim();
                if (choice.equals("0")) break;
                try {
                    handle(choice);
                } catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
                    System.out.println("Invalid input: " + ex.getMessage());
                }
            }
        } catch (NoSuchElementException endOfInput) {
            // input ended (for example Ctrl+D): fall through and quit
        }
        System.out.println("Goodbye");
    }

    private void printMenu() {
        System.out.println("\n1 View week   2 View day   3 Add   4 Remove   5 Change rank");
        System.out.println("6 Move   7 Complete   8 Undo   9 Redo   10 Roll over   11 Completed log");
        System.out.println("12 Move several   13 Complete several   14 Raise several to the top   0 Quit");
    }

    private void handle(String choice) {
        switch (choice) {
            case "1":
                System.out.print(taskList.describeWeek());
                break;
            case "2":
                System.out.println(taskList.describeDay(readDay("Day: ")));
                break;
            case "3": {
                Day day = readDay("Day: ");
                String name = readLine("Task name: ");
                Integer rank = readOptionalInt("Rank (1 = most important, Enter = bottom): ");
                if (rank == null) taskList.addTask(day, name);
                else taskList.addTask(day, name, rank);
                System.out.println("Added");
                break;
            }
            case "4": {
                Day day = readDay("Day: ");
                System.out.println("Removed " + taskList.removeTask(day, readInt("Task number: ")));
                break;
            }
            case "5": {
                Day day = readDay("Day: ");
                int from = readInt("Current rank: ");
                int to = readInt("New rank: ");
                System.out.println(taskList.changeRank(day, from, to) ? "Rank changed" : "Nothing changed");
                break;
            }
            case "6": {
                Day from = readDay("From day: ");
                int rank = readInt("Task number: ");
                Day to = readDay("To day: ");
                Integer toRank = readOptionalInt("Rank on new day (Enter = bottom): ");
                boolean moved = (toRank == null)
                        ? taskList.moveTask(from, rank, to)
                        : taskList.moveTask(from, rank, to, toRank);
                System.out.println(moved ? "Moved" : "Nothing changed");
                break;
            }
            case "7": {
                Day day = readDay("Day: ");
                taskList.completeTask(day, readInt("Task number: "));
                System.out.println("Completed");
                break;
            }
            case "8":
                System.out.println(taskList.undo() ? "Undone" : "Nothing to undo");
                break;
            case "9":
                System.out.println(taskList.redo() ? "Redone" : "Nothing to redo");
                break;
            case "10":
                System.out.println("Rolled over " + taskList.rollOver(readDay("Day to roll over: ")) + " task(s)");
                break;
            case "11":
                System.out.println(taskList.describeCompleted());
                break;
            case "12": {
                Day from = readDay("From day: ");
                if (noTasksToPick(from)) break;
                int[] ranks = readRanks("Task numbers to move (for example 1 3 4): ");
                Day to = readDay("To day: ");
                int moved = taskList.moveTasks(from, ranks, to);
                System.out.println(moved == 0 ? "Nothing changed" : "Moved " + moved + " task(s)");
                break;
            }
            case "13": {
                Day day = readDay("Day: ");
                if (noTasksToPick(day)) break;
                int[] ranks = readRanks("Task numbers to complete (for example 1 3 4): ");
                System.out.println("Completed " + taskList.completeTasks(day, ranks) + " task(s)");
                break;
            }
            case "14": {
                Day day = readDay("Day: ");
                if (noTasksToPick(day)) break;
                int[] ranks = readRanks("Task numbers to raise (for example 2 5): ");
                int changed = taskList.moveToTop(day, ranks);
                System.out.println(changed == 0 ? "Nothing changed" : "Raised " + changed + " task(s)");
                break;
            }
            default:
                System.out.println("Please choose a menu number from 0 to 14");
        }
    }

    // ---------- input helpers (FORMAT checks only) ----------

    //Displays a prompt and reads one line of user input.
    private String readLine(String prompt) {
        System.out.print(prompt);
        return input.nextLine();               
    }

    /**
     * Repeatedly asks for input until the user enters a whole number.
     *
     * @param prompt the message displayed before reading input
     * @return the integer entered by the user
     * @throws NoSuchElementException if no more input is available
     */
    int readInt(String prompt) {
        while (true) {
            String text = readLine(prompt).trim();
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("Please type a whole number.");
            }
        }
    }

    /** Blank means "no number given" (null). Anything else must be a whole number. */
    private Integer readOptionalInt(String prompt) {
        while (true) {
            String text = readLine(prompt).trim();
            if (text.isEmpty()) return null;
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("Please type a whole number, or press Enter.");
            }
        }
    }

    /** For multi-select: shows the day so the user can see the numbers. True if there is nothing to pick. */
    private boolean noTasksToPick(Day day) {
        System.out.println(taskList.describeDay(day));
        return taskList.taskCount(day) == 0;
    }

    /**
     * Repeatedly asks for one or more task ranks separated by spaces or commas.
     *
     * @param prompt the message displayed before reading input
     * @return an array containing the entered task ranks
     * @throws NoSuchElementException if no more input is available
     */
    int[] readRanks(String prompt) {
        while (true) {
            String text = readLine(prompt).trim();
            if (text.isEmpty()) {
                System.out.println("Type at least one task number.");
                continue;
            }
            String[] parts = text.split("[\\s,]+");
            int[] result = new int[parts.length];
            try {
                for (int i = 0; i < parts.length; i++) result[i] = Integer.parseInt(parts[i]);
                return result;
            } catch (NumberFormatException e) {
                System.out.println("Use whole numbers separated by spaces, like: 1 3 4");
            }
        }
    }

    /**
     * Repeatedly asks for a day until the user enters a recognized day name.
     *
     * @param prompt the message displayed before reading input
     * @return the day represented by the user's input
     * @throws NoSuchElementException if no more input is available
     */
    Day readDay(String prompt) {
        while (true) {
            try {
                return Day.fromText(readLine(prompt));
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}