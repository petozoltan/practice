### Executor interface

It has only `void execute(Runnable)`. Use `ExecutorService` instead.

How to create a regular one-thread-per-task `ExecutorService`:

- Similar to the low-level `new Thread(runnable).start()`.

```java
Executor newThreadPerTaskExecutor = (runnable) -> new Thread(runnable).start();
```

See `Executor` javadoc for more examples.

### ExecutorService interface

Extension of `Executor` that uses `Future` type as tasks.

Implements `AutoCloseable`.

- Usable in try-with-resources statement with caution:
- `close()` does not terminate the tasks.
- `close()` executes before `finally` block. (So you cannot stop the tasks in `finally` block.)

Solution 1

- Interrupt the threads with `Future.cancel(true)` in the `try` block.

Solution 2

- Don't create `ExecutorService` with try-with-resources statement.
- Use `Lock.lockInterruptibly()` instead of `Lock.lock()`.
- Call `ExecutorService.shutdownNow()`
- Optionally, `ExecutorService.awaitTermination()`.

Basic methods

| Method          | Usage                                                                                | Java |
|-----------------|--------------------------------------------------------------------------------------|:----:|
| `submit()`      | Adds a task and then it will start it, depending on the implementation.              |  5   |
| `close()`       | Tries to shut down the service, but does not terminate the tasks. It can be blocked. |  19  |
| `shutdown()`    | Shuts down the service, but does not terminate the tasks. It can be blocked.         |  5   |
| `shutdownNow()` | Shuts down the service, tries to terminate the tasks.                                |  5   |

### Executors

Factory methods to create various types of `ExecutorService`s.

How to create a regular one-thread-per-task `ExecutorService`:

- Similar to the low-level `new Thread(runnable).start()`.
- Based on the implementation of `Executors.newVirtualThreadPerTaskExecutor()`.
- But it is recommended to use an existing factory instead, like `Executors.newCachedThreadPool()`.

```java
ExecutorService newPlatformThreadPerTaskExecutor = Executors.newThreadPerTaskExecutor(Thread.ofPlatform().factory());
```

### Basic high-level API methods corresponding to low-level API

| Thread API                  | Executor/Future API                                                   |     Java     |
|-----------------------------|-----------------------------------------------------------------------|:------------:|
| `Thread.sleep()`            | `Thread.sleep()`                                                      |      1       |
| `Thread.start()`            | `ExecutorService.submit()`                                            |      5       |
| `Thread.join()`             | `Future.get()`<br>`Future.resultNow()`                                |   5<br>19    | 
| `Thread.interrupt()`        | `Future.cancel(true)`<br>`ExecutorService.shutdownNow()`              |      5       | 
| —                           | `Future.isDone()`<br>`Future.isCancelled()`<br>`Future.state()`       | 5<br>5<br>19 | 
| `synchronized(lock)`        | `Lock.lock()`<br>`Lock.lockInterruptibly()`<br>`Lock.tryLock()`       |      5       |
| (end of synchronized block) | `Lock.unlock()`                                                       |      5       |
| —                           | `ReentrantLock.isLocked()`<br>`ReentrantLock.isHeldByCurrentThread()` |      5       |
