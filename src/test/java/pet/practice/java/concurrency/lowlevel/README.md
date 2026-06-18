Most important low-level Java API for threads

### Thread

**`Thread` class instance methods** → directly manage a thread

- `start()`
- `join()`

**`Thread` class static methods** → manage the current thread

- `sleep()`
- `yield()`

**`Runnable` interface** → the code to be executed by a thread

- `run()`

### Lock

**`Object` class** → every object can be used as a lock for synchronization

- `synchronized` methods → uses `this`
- `synchronized (object)` blocks → uses any `object`

_Only objects that have identity can be used as locks._

- _Primitive types cannot be used._
- _Newer Java versions introduce 'value objects' that cannot be used as locks either._

**`Object` class instance methods** → manage current or all threads that have acquired this lock

- `wait()`
- `notify()` / `notifyAll()`

_Can be called only from a thread that has acquired this lock, i.e. within a synchronized method or block on the same lock._
