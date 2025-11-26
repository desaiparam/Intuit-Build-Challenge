# Producer-Consumer Pattern Implementation

## Overview

This project implements a classic **Producer-Consumer pattern** in Java, demonstrating thread synchronization using custom blocking queues with wait/notify mechanism.

## Features

- **BoundedBlockingQueue** - Fixed capacity blocking queue with wait/notify
- **DynamicBoundedBlockingQueue** - Dynamic capacity queue that grows/shrinks (Additional Feature)
- **Producer** - Thread that reads from source list and places items in queue
- **Consumer** - Thread that reads from queue and stores items in destination list
- **Flow Control** - Producers pause when queue is full for 30+ seconds; consumers wait when queue is empty

## Setup Instructions

### Requirements
- Java JDK 8 or higher
- No external dependencies (pure Java)

### Compile
```bash
cd Project1
./compile.sh
```

Or manually:
```bash
javac -d bin src/*.java src/test/*.java src/demo/*.java
```

### Run Demo
```bash
java -cp bin ProducerConsumerDemo
```

### Run Tests
```bash
./run_tests.sh
```

Or run tests individually:
```bash
# Unit tests
java -cp bin -ea BoundedBlockingQueueTest
java -cp bin -ea DynamicBoundedBlockingQueueTest

# Integration tests
java -cp bin -ea ProducerConsumerIntegrationTest
```

### Run Interactive Server/Client

**For Fixed Queue:**

Terminal 1 - Start Server:
```bash
java -cp bin QueueServer [capacity] [port]
# Example: java -cp bin QueueServer 10
```

Terminal 2 - Start Producer:
```bash
java -cp bin InteractiveProducer [host] [port]
# Example: java -cp bin InteractiveProducer
```

Terminal 3 - Start Consumer:
```bash
java -cp bin InteractiveConsumer [host] [port]
# Example: java -cp bin InteractiveConsumer
```

**For Dynamic Queue:**

Terminal 1 - Start Server:
```bash
java -cp bin DynamicQueueServer [initialCapacity] [port]
# Example: java -cp bin DynamicQueueServer 10
```

Terminal 2 - Start Producer:
```bash
java -cp bin InteractiveProducer --dynamic [host] [port]
# Example: java -cp bin InteractiveProducer --dynamic
```

Terminal 3 - Start Consumer:
```bash
java -cp bin InteractiveConsumer --dynamic [host] [port]
# Example: java -cp bin InteractiveConsumer --dynamic
```

## Project Structure

```
Project1/
├── src/                          # Source files
│   ├── BoundedBlockingQueue.java
│   ├── DynamicBoundedBlockingQueue.java
│   ├── Producer.java
│   ├── Consumer.java
│   ├── demo/                     # Demo and interactive applications
│   │   ├── ProducerConsumerDemo.java
│   │   ├── InteractiveProducer.java    # Works with both fixed and dynamic queues
│   │   ├── InteractiveConsumer.java    # Works with both fixed and dynamic queues
│   │   ├── QueueServer.java            # Fixed queue server (port 8888)
│   │   └── DynamicQueueServer.java     # Dynamic queue server (port 8889)
│   └── test/                     # Test files
│       ├── BoundedBlockingQueueTest.java
│       ├── DynamicBoundedBlockingQueueTest.java
│       └── ProducerConsumerIntegrationTest.java
├── bin/                          # Compiled files
├── compile.sh                    # Compilation script
├── run_tests.sh                  # Test runner script
├── README.md
├── DESIGN_DECISIONS.md           # Detailed design documentation
└── INTERACTIVE_TESTING.md        # Interactive testing guide
```

## Assumptions

- Queue capacity is a positive integer (validated in constructor)
- Source list contains finite number of items (producers stop when list is exhausted)
- Multiple producers and consumers can safely share the same queue instance
- Thread interruption is handled gracefully with proper interrupt status restoration
- Flow control monitoring period (30 seconds) is sufficient for detecting slow consumption
- Dynamic queue growth is bounded by available memory (no explicit maximum capacity limit)
- All queue operations are thread-safe and maintain FIFO order

## Sample Output

```
=== Producer-Consumer Pattern Demo ===

Configuration:
  Queue Capacity: 5
  Number of Producers: 2
  Number of Consumers: 2
  Items per Producer: 20

[Consumer-1] Started consuming
[Consumer-2] Started consuming
Initialized source list with 40 items
Started 2 consumer thread(s)
Started 2 producer thread(s)
[Producer-1] Started producing
[Producer-2] Started producing
[Producer-1] Produced: 1
[Producer-2] Produced: 21
[Consumer-1] Consumed: 1 (total: 1)
[Consumer-2] Consumed: 21 (total: 1)
[Producer-1] Produced: 2
[Producer-2] Produced: 22
[Consumer-1] Consumed: 2 (total: 2)
[Consumer-2] Consumed: 22 (total: 2)
...

All producers finished

All consumers finished

=== Summary ===
Source list size: 40
Destination list size: 40
Queue final size: 0
Queue capacity: 5

✓ SUCCESS: All items were consumed!
```

## Test Coverage

### Unit Tests
- **BoundedBlockingQueueTest**: Basic operations, blocking behavior, thread safety, FIFO order
- **DynamicBoundedBlockingQueueTest**: Queue growth/shrink, thread safety during resize, order preservation

### Integration Tests
- **ProducerConsumerIntegrationTest**: Single/multiple producers-consumers, flow control mechanisms, graceful shutdown, item verification

## Key Design Decisions

For detailed design information, see [DESIGN_DECISIONS.md](DESIGN_DECISIONS.md).

## Author

Intuit Software Developer - Take Home Assignment
