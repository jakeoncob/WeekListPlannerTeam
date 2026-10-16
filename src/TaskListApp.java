import java.util.Scanner;
/** Text-based user interface for the WeekList prototype. */
public class TaskListApp {
    private static final String[] DAYS = {
        "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    };
    public static void main(String[] args) {
        TaskList list = new TaskList();
        Scanner scanner = new Scanner(System.in);
        System.out.println("WeekList prototype");
        while (true) {
            System.out.println("\n1 View week  2 View day  3 Add  4 Remove  5 Move  6 Complete  7 Completed log  0 Quit");
            System.out.print("> ");
            String choice = scanner.nextLine().trim();
            try {
                if ("0".equals(choice)) break;
                switch (choice) {
                    case "1":
                        for (String day : DAYS) System.out.println(day + ": " + list.countTasks(day) + " tasks");
                        break;
                    case "2": {
                        String day = ask(scanner, "Day: ");
                        Task[] tasks = list.getTasks(day);
                        if (tasks.length == 0) System.out.println("No tasks on this day");
                        for (int i = 0; i < tasks.length; i++) System.out.println((i + 1) + ". " + tasks[i]);
                        break;
                    }
                    case "3":
                        list.addTask(ask(scanner, "Day: "), ask(scanner, "Task name: "));
                        System.out.println("Added"); break;
                    case "4":
                        System.out.println("Removed " + list.removeTask(ask(scanner, "Day: "), position(scanner)));
                        break;
                    case "5": {
                        String from = ask(scanner, "From day: ");
                        int pos = position(scanner);
                        String to = ask(scanner, "To day: ");
                        System.out.println(list.moveTask(from, pos, to) ? "Moved" : "Nothing changed");
                        break;
                    }
                    case "6":
                        System.out.println("Completed " + list.completeTask(ask(scanner, "Day: "), position(scanner)));
                        break;
                    case "7":
                        CompletedEntry[] entries = list.getCompleted();
                        if (entries.length == 0) System.out.println("No completed tasks");
                        for (CompletedEntry entry : entries) System.out.println(entry);
                        break;
                    default: System.out.println("Choose a menu number from 0 to 7");
                }
            }
            catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
                System.out.println("Invalid input: " + ex.getMessage());
            }
        }
        System.out.println("Goodbye");
    }
    private static String ask(Scanner scanner, String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
    private static int position(Scanner scanner) {
        System.out.print("Task number (starting at 1): ");
        int result = Integer.parseInt(scanner.nextLine().trim());
        if (result < 1) throw new IllegalArgumentException("Task number must be at least 1");
        return result - 1;
    }
}
