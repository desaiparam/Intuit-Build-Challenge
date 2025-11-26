# Design Decisions Documentation

This document details key design choices, trade-offs, and implementation reasoning for the Producer-Consumer pattern implementation.

## Table of Contents

1. [Language and Build System](#1-language-and-build-system)
2. [Queue Implementation Strategy](#2-queue-implementation-strategy)
3. [Thread Configuration](#3-thread-configuration)
4. [Synchronization Approach](#4-synchronization-approach)
5. [Dynamic Queue Design](#5-dynamic-queue-design)
6. [Flow Control Mechanisms](#6-flow-control-mechanisms)
7. [Testing Strategy](#7-testing-strategy)
8. [Project Organization](#8-project-organization)

---

## 1. Language and Build System

**Decision**: Java only, no Maven/Gradle - pure Java classes

**Rationale**: Simplicity, portability, focus on core synchronization concepts

**Implementation**:
- Compilation: `./compile.sh` or `javac -d bin src/*.java src/test/*.java src/demo/*.java`
- Execution: `java -cp bin <ClassName>`
- No external dependencies

**Trade-offs**: ✅ Simple, portable | ❌ No dependency injection, custom test framework

---

## 2. Queue Implementation Strategy

**Decision**: Two implementations - Fixed (primary) and Dynamic (additional feature)

### Fixed Bounded Queue
- **Data Structure**: Circular buffer (array-based) for O(1) operations
- **Methods**: `put()` blocks when full, `take()` blocks when empty, both use `wait()/notifyAll()`
- **Why**: Predictable, demonstrates core blocking behavior, meets assignment requirements

### Dynamic Bounded Queue
- **Growth**: Grows by initial capacity increments (5 → 10 → 15...)
- **Shrink**: Shrinks to initial capacity when underutilized
- **Synchronization**: Separate `resizeLock` with double-check locking pattern
- **Why**: Advanced demonstration, real-world relevance, complex thread-safe resize

**Trade-offs**: 
- Fixed: ✅ Simple, predictable | ❌ Fixed capacity limiting
- Dynamic: ✅ Adapts to workload | ❌ Complex, resize overhead

---

## 3. Thread Configuration

**Decision**: Multiple producers (2) and consumers (2)

**Rationale**: Real-world scenarios, better thread safety testing, demonstrates concurrency

**Implementation**: Named threads (`ProducerThread-1`, `ConsumerThread-1`), shared queue instance

**Trade-offs**: ✅ Better concurrency demonstration | ❌ More complex debugging

---

## 4. Synchronization Approach

**Decision**: Wait/Notify mechanism with synchronized blocks

**Rationale**: Assignment requirement, demonstrates fundamental Java synchronization

**Implementation**:
- All queue operations are `synchronized`
- Wait conditions: `while (size == capacity) { wait(); }` (prevents spurious wakeups)
- Use `notifyAll()` for multiple waiting threads
- Queue object itself is the monitor

**Trade-offs**: ✅ Educational, required | ❌ More error-prone than high-level APIs

**Alternatives Considered**: `BlockingQueue`, `ReentrantLock` - rejected (assignment requires custom implementation)

---

## 5. Dynamic Queue Design

**Growth Strategy**: When full, grow by initial capacity. Double-check locking prevents race conditions.

**Shrink Strategy**: When size < initialCapacity and capacity > initialCapacity, shrink to initial capacity.

**Resize Synchronization**:
- Separate `resizeLock` prevents deadlock
- Allows concurrent put/take during resize
- Maintains FIFO order during resize

**Challenges Addressed**:
- Race conditions → `resizeLock`
- Element loss → careful copying
- Order preservation → FIFO maintained

**Trade-offs**: ✅ Adapts to workload | ❌ Complex, resize overhead

---

## 6. Flow Control Mechanisms

**Decision**: Intelligent flow control with 30-second monitoring

### Producer Flow Control
- Monitors queue size when full
- Pauses if size unchanged for 30 seconds
   - Resumes when size decreases

### Consumer Flow Control
- Waits when queue empty
- Prints status every 30 seconds
- Resumes when items available

**Why 30 seconds?**: Long enough to detect stuck consumers, short enough to be responsive

**Trade-offs**: ✅ Prevents overflow, efficient waiting | ❌ Adds complexity

---

## 7. Testing Strategy

**Decision**: Comprehensive tests without external framework

**Test Coverage**:
- **Unit Tests**: Basic operations, blocking behavior, thread safety, growth/shrink
- **Integration Tests**: Single/multiple producers-consumers, flow control

**Approach**: Custom assertions, thread testing with `Thread.join()`, verify item counts

**Trade-offs**: ✅ No dependencies, comprehensive | ❌ No framework features

---

## 8. Project Organization

**Decision**: Separated folders for core code, demos, and tests

**Structure**:
```
Project1/
├── src/
│   ├── BoundedBlockingQueue.java          # Core
│   ├── DynamicBoundedBlockingQueue.java  # Core
│   ├── Producer.java, Consumer.java      # Core
│   ├── demo/                              # Demo apps
│   │   ├── InteractiveProducer.java      # Unified (fixed + dynamic)
│   │   ├── InteractiveConsumer.java      # Unified (fixed + dynamic)
│   │   ├── QueueServer.java              # Fixed (port 8888)
│   │   └── DynamicQueueServer.java       # Dynamic (port 8889)
│   └── test/                              # Tests
├── compile.sh, run_tests.sh
└── Documentation files
```

**Key Decisions**:
1. **Core vs Demo**: Core in `src/`, demos in `src/demo/`
2. **Unified Clients**: `InteractiveProducer/Consumer` support both queue types via `--dynamic` flag
3. **Separate Servers**: Different response formats, different ports (8888/8889)
4. **Test Organization**: All tests in `src/test/`, run with `run_tests.sh`

**Usage**:
- Fixed: `java -cp bin InteractiveProducer`
- Dynamic: `java -cp bin InteractiveProducer --dynamic`

**Trade-offs**: ✅ Clear organization | ❌ Slightly more complex structure

---



## Conclusion

This implementation demonstrates:
- ✅ Thread synchronization with wait/notify
- ✅ Concurrent programming patterns
- ✅ Blocking queue implementation
- ✅ Flow control mechanisms
- ✅ Thread-safe operations
- ✅ Graceful shutdown

All decisions prioritized: assignment requirements, educational value, code quality, testing, and documentation.
