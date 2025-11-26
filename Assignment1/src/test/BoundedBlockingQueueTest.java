// Unit tests for BoundedBlockingQueue
public class BoundedBlockingQueueTest {
    
    // Test basic put and take operations
    public static void testBasicOperations() {
        System.out.println("Test: Basic Operations");
        BoundedBlockingQueue queue = new BoundedBlockingQueue(5);
        
        try {
            queue.put(1);
            queue.put(2);
            queue.put(3);
            
            assert queue.size() == 3 : "Size should be 3";
            assert !queue.isEmpty() : "Queue should not be empty";
            assert !queue.isFull() : "Queue should not be full";
            
            Integer item1 = queue.take();
            Integer item2 = queue.take();
            
            assert item1 == 1 : "First item should be 1";
            assert item2 == 2 : "Second item should be 2";
            assert queue.size() == 1 : "Size should be 1";
            
            System.out.println("Basic operations test passed");
        } catch (Exception e) {
            System.out.println("Basic operations test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Test capacity limits
    public static void testCapacity() {
        System.out.println("Test: Capacity Limits");
        BoundedBlockingQueue queue = new BoundedBlockingQueue(3);
        
        try {
            queue.put(1);
            queue.put(2);
            queue.put(3);
            
            assert queue.isFull() : "Queue should be full";
            assert queue.size() == 3 : "Size should be 3";
            assert queue.capacity() == 3 : "Capacity should be 3";
            
            queue.take();
            assert !queue.isFull() : "Queue should not be full after take";
            
            System.out.println("Capacity test passed");
        } catch (Exception e) {
            System.out.println("Capacity test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Test blocking behavior when queue is full
    public static void testBlockingWhenFull() {
        System.out.println("Test: Blocking When Full");
        BoundedBlockingQueue queue = new BoundedBlockingQueue(2);
        
        try {
            queue.put(1);
            queue.put(2);
            
            Thread producerThread = new Thread(() -> {
                try {
                    queue.put(3);
                    System.out.println("  Producer unblocked and put item 3");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            
            producerThread.start();
            
            Thread.sleep(100);
            assert producerThread.isAlive() : "Producer thread should be blocked";
            
            queue.take();
            Thread.sleep(100);
            
            assert queue.size() == 2 : "Queue should have 2 items";
            System.out.println("Blocking when full test passed");
        } catch (Exception e) {
            System.out.println("Blocking when full test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Test blocking behavior when queue is empty
    public static void testBlockingWhenEmpty() {
        System.out.println("Test: Blocking When Empty");
        BoundedBlockingQueue queue = new BoundedBlockingQueue(2);
        
        try {
            final Integer[] result = new Integer[1];
            Thread consumerThread = new Thread(() -> {
                try {
                    result[0] = queue.take();
                    System.out.println("  Consumer unblocked and took item: " + result[0]);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            
            consumerThread.start();
            
            Thread.sleep(100);
            assert consumerThread.isAlive() : "Consumer thread should be blocked";
            assert result[0] == null : "Result should be null (blocked)";
            
            queue.put(42);
            Thread.sleep(100);
            
            assert result[0] != null && result[0] == 42 : "Consumer should have received 42";
            System.out.println("Blocking when empty test passed");
        } catch (Exception e) {
            System.out.println("Blocking when empty test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Test thread safety with multiple producers and consumers
    public static void testThreadSafety() {
        System.out.println("Test: Thread Safety");
        BoundedBlockingQueue queue = new BoundedBlockingQueue(10);
        final int NUM_ITEMS = 50;
        final int NUM_PRODUCERS = 2;
        final int NUM_CONSUMERS = 2;
        
        java.util.List<Integer> consumedItems = new java.util.ArrayList<>();
        java.util.concurrent.atomic.AtomicInteger producedCount = new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.concurrent.atomic.AtomicInteger consumedCount = new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.concurrent.atomic.AtomicBoolean productionComplete = new java.util.concurrent.atomic.AtomicBoolean(false);
        
        // Create producer threads
        Thread[] producers = new Thread[NUM_PRODUCERS];
        for (int i = 0; i < NUM_PRODUCERS; i++) {
            final int producerId = i;
            producers[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < NUM_ITEMS; j++) {
                        int item = producerId * NUM_ITEMS + j;
                        queue.put(item);
                        producedCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            producers[i].start();
        }
        
        // Create consumer threads
        Thread[] consumers = new Thread[NUM_CONSUMERS];
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            consumers[i] = new Thread(() -> {
                try {
                    int targetCount = NUM_PRODUCERS * NUM_ITEMS;
                    while (true) {
                        if (consumedCount.get() >= targetCount) {
                            break;
                        }
                        
                        // Check termination condition atomically with queue state
                        boolean shouldExit = false;
                        
                        synchronized (queue) {
                            boolean queueIsEmpty = queue.isEmpty();
                            
                            // Critical check: if production is complete, queue is empty, and we've consumed enough
                            if (productionComplete.get() && queueIsEmpty && consumedCount.get() >= targetCount) {
                                shouldExit = true;
                            }
                            
                            // This handles race conditions where all items were consumed by other threads
                            else if (productionComplete.get() && queueIsEmpty) {
                                try {
                                    queue.wait(50);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    shouldExit = true;
                                }
                                queueIsEmpty = queue.isEmpty();
                                if (productionComplete.get() && queueIsEmpty) {
                                    if (consumedCount.get() >= targetCount) {
                                        shouldExit = true;
                                    } else {
                                        // Production done, queue empty, but count not reached
                                        // This shouldn't happen in normal execution, but exit to avoid deadlock
                                        shouldExit = true;
                                    }
                                }
                            }
                        }
                        
                        if (shouldExit) {
                            break;
                        }
                        
                        synchronized (queue) {
                            if (productionComplete.get() && queue.isEmpty() && consumedCount.get() >= targetCount) {
                                break;
                            }
                            // If production is complete and queue is empty, we're done (even if count not reached yet)

                            if (productionComplete.get() && queue.isEmpty()) {
                                try {
                                    queue.wait(10);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    return; // Exit the thread's run method
                                }
                                // Final check
                                if (productionComplete.get() && queue.isEmpty()) {
                                    break;
                                }
                            }
                        }
                        
                        Integer item = queue.take();
                        
                        synchronized (consumedItems) {
                            int current = consumedCount.get();
                            if (current < targetCount) {
                                consumedItems.add(item);
                                consumedCount.incrementAndGet();
                            }
                            // If we've reached target, break
                            if (consumedCount.get() >= targetCount) {
                                break;
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return; // Exit the thread's run method
                }
            });
            consumers[i].start();
        }
        
        try {
            for (Thread t : producers) {
                t.join(5000); // 5 second timeout
                if (t.isAlive()) {
                    System.out.println("Warning: Producer thread did not complete in time");
                    t.interrupt();
                }
            }
            productionComplete.set(true); // Signal that production is done
            
            synchronized (queue) {
                queue.notifyAll();
            }
            
            for (Thread t : consumers) {
                t.join(3000); // 3 second timeout
                if (t.isAlive()) {
                    System.out.println("Warning: Consumer thread did not complete in time");
                    t.interrupt();
                    t.join(1000); // Give it a moment to respond to interrupt
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        assert producedCount.get() == NUM_PRODUCERS * NUM_ITEMS : 
            "All items should be produced. Got: " + producedCount.get() + ", Expected: " + (NUM_PRODUCERS * NUM_ITEMS);
        assert consumedCount.get() == NUM_PRODUCERS * NUM_ITEMS : 
            "All items should be consumed. Got: " + consumedCount.get() + ", Expected: " + (NUM_PRODUCERS * NUM_ITEMS);
        assert consumedItems.size() == NUM_PRODUCERS * NUM_ITEMS : 
            "Consumed items list should have all items";
        assert queue.isEmpty() : "Queue should be empty";
        
        System.out.println("Thread safety test passed");
        System.out.println("  Produced: " + producedCount.get() + 
                          ", Consumed: " + consumedCount.get());
    }
    
    // Run all tests
    public static void main(String[] args) {
        System.out.println("=== BoundedBlockingQueue Tests ===\n");
        
        testBasicOperations();
        testCapacity();
        testBlockingWhenFull();
        testBlockingWhenEmpty();
        testThreadSafety();
        
        System.out.println("\n=== All Tests Completed ===");
    }
}

