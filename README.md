# WeekList — Project 2 working prototype

## Compile and run
Place all `.java` files in one folder (default package). Requires JDK 8+.

```sh
javac LinkedChain.java Task.java CompletedEntry.java TaskList.java 
TaskListApp.java
java TaskListApp
```

JUnit 4 tests (with JUnit 4 and Hamcrest jars on your classpath):
```sh
javac -cp ".:junit-4.13.2.jar:hamcrest-core-1.3.jar" *.java
java -cp ".:junit-4.13.2.jar:hamcrest-core-1.3.jar" org.junit.runner.JUnitCore 
TaskListTest
```
On Windows, replace `:` with `;` in the classpath.

## Working slice
The console app can show weekly counts, view a day's ordered tasks, 
add/remove a task, move a task to another day (at the end), complete a task, 
and view the completion log. It uses the **same** `LinkedChain<T>` 
class for `Task` and `CompletedEntry`. 
The application uses only its public API. Tasks can have duplicate names 
because each task is a distinct object. Invalid day, blank name, bad menu 
number and out-of-range task positions are handled without terminating the menu.

## Reflection
This prototype implements a smaller vertical slice of the original 
specification: basic weekly task management and the completion log. 
Priority insertion/reordering and undo/redo are not implemented yet. 
The provided LinkedChain was used with its existing public API and no 
application-specific modifications. One useful detail we resolved is that 
`add()` prepends, so `addToEnd()` is used to preserve the order in which 
tasks are entered. Moving tasks works by retrieving a Task, removing it 
from its source chain and appending the same Task to the target chain. 
Multi-task moves and atomic undo/redo remain open. The tests currently 
cover the implemented app methods, including bad days, empty lists, blank 
task names and invalid positions; they do not claim to cover unimplemented 
features.

## Next Steps
1. Add rank-based insertion and reordering with explicit position validation.
2. Add an Action record and undo/redo history using `LinkedChain<Action>`, 
including clearing redo on a new change.
3. Test every action and inverse, including moves, completion and 
same-day no-ops.
4. Expand bad-input tests and menu interactions.
5. If time remains, consider multiple-week support; defer batch moves 
unless core MVP and undo/redo are stable.

## Attribution
| File | Team member(s) | Actual contribution |
|---|---|---|
| LinkedChain.java | CS 2114 Staff | Provided starter implementation
| Task.java |  | [wrote/reviewed/tested] |
| CompletedEntry.java | Jake Coble | wrote |
| TaskList.java | Vihaan Gadkari | wrote |
| TaskListApp.java | Elias Dejene | wrote |
| TaskListTest.java | Kyra Ma | wrote |
| README.md | Elias Dejene| wrote |

GenAI note: ChatGPT was used to assist with drafting the prototype 
implementation, generating JUnit test cases, troubleshooting Eclipse setup,
and organizing the README. The application was compiled and run in Eclipse,
and all 17 JUnit tests passed.
