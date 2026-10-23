
# WeekList

## Project Overview

WeekList is a Java console application that helps users organize tasks across the seven days of the week. Users can add, remove, rank, move, and complete tasks, while also managing their changes through undo and redo functionality.

The project was developed for CS 2114 Project 2. It builds on our earlier specification and prototype, using a reusable generic linked data structure as the foundation of the application.

## Features

The final application supports:

- Viewing the week and the tasks assigned to individual days
- Adding and removing tasks
- Assigning and changing task ranks
- Moving tasks between days
- Marking tasks as completed and viewing the completed log
- Undoing and redoing changes
- Rolling over tasks
- Moving or completing multiple tasks at once
- Raising multiple tasks to the top of a list
- Handling invalid user input without terminating the application

The program manages one week at a time. Multiple-week support was considered but ultimately excluded to keep the project focused.

## System Design

WeekList separates user interaction, application logic, and data storage.

`TaskListApp` handles console input, displays the menu, and communicates with the user. `TaskList` manages the weekly tasks and application operations. Supporting classes represent tasks, completed entries, actions, days, and undo/redo history.

Our generic `LinkedChain<T>` is reused with multiple element types, including:

- `LinkedChain<Task>` for daily task lists
- `LinkedChain<CompletedEntry>` for completed tasks
- `LinkedChain<Action>` for undo/redo history

This allows the same underlying data structure to support different parts of the program without rewriting its implementation. Application classes interact with `LinkedChain` through its public methods rather than directly accessing its internal nodes.

## How to Compile and Run

1. Import the repository into Eclipse as an existing Java project.
2. Make sure the project has JUnit 4 configured in its build path.
3. Eclipse automatically compiles the project.
4. To launch WeekList, right-click `TaskListApp.java` and select **Run As → Java Application**.
5. To run the tests, right-click each of the following classes and select **Run As → JUnit Test**:
   - `LinkedChainTest.java`
   - `ModelClassesTest.java`
   - `TaskListTest.java`
   - `UndoHistoryTest.java`

## Testing and Input Validation

Our JUnit suite tests the linked data structure and the application's supporting classes. Testing includes normal operations, invalid inputs, and edge cases.

The program also validates user input during console interaction. For example, entering a rank outside the permitted range produces an error message instead of crashing the application.

The final build was also manually tested through its console menu.

## Development Process

We began with a specification outlining the planned features and class relationships. For Deliverable 3, we created a simplified working prototype that established the main application structure.

The final build expanded that prototype with ranking, undo/redo, and additional task-management operations. Building the simpler version first helped us understand how the classes should interact before implementing more complex features.

We originally considered allowing users to manage multiple weeks, but decided that a single-week system was sufficient for the project's goals.

## GenAI Use

Our team used GenAI to assist with drafting test cases based on our test plan, proposing a class breakdown, and approaching the implementation of stretch features such as rollover and multi-task operations.

GenAI served as a development aid rather than a substitute for understanding the project. We reviewed its suggestions, worked through the implementation, and tested the resulting program.

One lesson from this process was the importance of discussing and developing our own ideas before turning to GenAI for suggestions. This would allow us to evaluate its responses more critically and strengthen our understanding of the design decisions.

## File Attribution

| File | Contributor(s) |
|---|---|
| `Action.java` | [Name(s)] |
| `ActionType.java` | [Name(s)] |
| `CompletedEntry.java` | [Name(s)] |
| `Day.java` | [Name(s)] |
| `LinkedChain.java` | CS 2114 Staff; [modifications by name(s), if applicable] |
| `LinkedChainTest.java` | [Name(s)] |
| `ModelClassesTest.java` | [Name(s)] |
| `Task.java` | [Name(s)] |
| `TaskList.java` | [Name(s)] |
| `TaskListApp.java` | [Name(s)] |
| `TaskListTest.java` | [Name(s)] |
| `UndoHistory.java` | [Name(s)] |
| `UndoHistoryTest.java` | [Name(s)] |

## Final Reflection

The most important takeaway from this project was understanding how reuse and generics work together. By establishing a consistent contract for `LinkedChain<T>`, we could use one data structure across different parts of the application.

We also learned the value of developing a small working prototype before adding more complex functionality. Features such as undo/redo and multi-task operations were easier to approach once the basic task-management system was established.
