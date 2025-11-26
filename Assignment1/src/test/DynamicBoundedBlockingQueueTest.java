// Unit tests for DynamicBoundedBlockingQueue
public class DynamicBoundedBlockingQueueTest {
    
    // Test basic put and take operations
    public static void testBasicOperations() {
        System.out.println("Test: Basic Operations");
        DynamicBoundedBlockingQueue queue = new DynamicBoundedBlockingQueue(5);
        
        try {
            assert queue.getInitialCapacity() == 5 : "Initial capacity should be 5";
            assert queue.capacity() == 5 : "Current capacity should be 5";
            
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
    
    // Test queue growth when full
    public static void testQueueGrowth() {
        System.out.println("Test: Queue Growth");
        DynamicBoundedBlockingQueue queue = new DynamicBoundedBlockingQueue(5);
        
        try {
            for (int i = 1; i <= 5; i++) {
                queue.put(i);
            }
            
            assert queue.isFull() : "Queue should be full";
            assert queue.capacity() == 5 : "Capacity should be 5";
            
            queue.put(6);
            
            assert queue.capacity() == 10 : "Capacity should grow to 10";
            assert queue.size() == 6 : "Size should be 6";
            assert !queue.isFull() : "Queue should not be full after growth";
            
            for (int i = 7; i <= 10; i++) {
                queue.put(i);
            }
            
            assert queue.isFull() : "Queue should be full again";
            assert queue.capacity() == 10 : "Capacity should still be 10";
            
            queue.put(11);
            
            assert queue.capacity() == 15 : "Capacity should grow to 15";
            assert queue.size() == 11 : "Size should be 11";
            
            System.out.println("Queue growth test passed");
            System.out.println("  Final capacity: " + queue.capacity() + ", size: " + queue.size());
        } catch (Exception e) {
            System.out.println("Queue growth test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Test queue shrink when underutilized
    public static void testQueueShrink() {
        System.out.println("Test: Queue Shrink");
        DynamicBoundedBlockingQueue queue = new DynamicBoundedBlockingQueue(5);
        
        try {
            for (int i = 1; i <= 6; i++) {
                queue.put(i);
            }
            
            assert queue.capacity() == 10 : "Capacity should be 10 after growth";
            assert queue.size() == 6 : "Size should be 6";
            
            queue.take(); // size = 5
            queue.take(); // size = 4
            queue.take(); // size = 3
            queue.take(); // size = 2
            
            assert queue.size() == 2 : "Size should be 2";
            assert queue.capacity() == 5 : "Capacity should shrink back to 5";
            
            queue.take(); // size = 1
            queue.take(); // size = 0
            
            assert queue.isEmpty() : "Queue should be empty";
            assert queue.capacity() == 5 : "Capacity should remain 5 (initial)";
            
                System.out.println("Queue shrink test passed");
            System.out.println("  Final capacity: " + queue.capacity() + ", size: " + queue.size());
        } catch (Exception e) {
            System.out.println("Queue shrink test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Test thread safety during resize operations
    public static void testThreadSafetyDuringResize() {
        System.out.println("Test: Thread Safety During Resize");
        DynamicBoundedBlockingQueue queue = new DynamicBoundedBlockingQueue(5);
        final int NUM_ITEMS = 30;
        final int NUM_PRODUCERS = 2;
        final int NUM_CONSUMERS = 2;
        
        java.util.List<Integer> consumedItems = new java.util.ArrayList<>();
        java.util.concurrent.atomic.AtomicInteger producedCount = new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.concurrent.atomic.AtomicInteger consumedCount = new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.concurrent.atomic.AtomicBoolean productionComplete = new java.util.concurrent.atomic.AtomicBoolean(false);
        
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
        
        Thread[] consumers = new Thread[NUM_CONSUMERS];
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            consumers[i] = new Thread(() -> {
                try {
                    int targetCount = NUM_PRODUCERS * NUM_ITEMS;
                    while (true) {
                        if (consumedCount.get() >= targetCount) {
                            break;
                        }
                        
                        boolean shouldExit = false;
                        boolean queueHasItems = false;
                        
                        synchronized (queue) {
                            queueHasItems = !queue.isEmpty();
                            
                            if (productionComplete.get() && !queueHasItems && consumedCount.get() >= targetCount) {
                                shouldExit = true;
                            }
                            else if (productionComplete.get() && !queueHasItems) {
                                try {
                                    queue.wait(50);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    shouldExit = true;
                                }
                                queueHasItems = !queue.isEmpty();
                                if (productionComplete.get() && !queueHasItems) {
                                    if (consumedCount.get() >= targetCount) {
                                        shouldExit = true;
                                    } else {
                                        shouldExit = true;
                                    }
                                }
                            }
                        }
                        
                        if (shouldExit) {
                            break;
                        }
                        
                        // Final check right before calling take() to handle race conditions
                        // Another thread might have changed the state between our check and this point
                        synchronized (queue) {
                            if (productionComplete.get() && queue.isEmpty() && consumedCount.get() >= targetCount) {
                                break;
                            }
                            if (productionComplete.get() && queue.isEmpty()) {
                                try {
                                    queue.wait(10);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    break;
                                }
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
                            if (consumedCount.get() >= targetCount) {
                                break;
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            });
            consumers[i].start();
        }
        
        try {
            for (Thread t : producers) {
                t.join(5000);
                if (t.isAlive()) {
                    System.out.println("Producer thread did not complete in time");
                    t.interrupt();
                }
            }
            productionComplete.set(true);
            
            synchronized (queue) {
                queue.notifyAll();
            }
            
            for (Thread t : consumers) {
                t.join(3000);
                if (t.isAlive()) {
                    System.out.println("Consumer thread did not complete in time");
                    t.interrupt();
                    t.join(1000);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Verify results
        assert producedCount.get() == NUM_PRODUCERS * NUM_ITEMS : 
            "All items should be produced. Got: " + producedCount.get() + ", Expected: " + (NUM_PRODUCERS * NUM_ITEMS);
        assert consumedCount.get() == NUM_PRODUCERS * NUM_ITEMS : 
            "All items should be consumed. Got: " + consumedCount.get() + ", Expected: " + (NUM_PRODUCERS * NUM_ITEMS);
        assert consumedItems.size() == NUM_PRODUCERS * NUM_ITEMS : 
            "Consumed items list should have all items";
        assert queue.isEmpty() : "Queue should be empty";
        assert queue.capacity() == 5 : "Queue should shrink back to initial capacity";
        
        System.out.println("✓ Thread safety during resize test passed");
        System.out.println("  Produced: " + producedCount.get() + 
                          ", Consumed: " + consumedCount.get());
        System.out.println("  Final capacity: " + queue.capacity());
    }
    
    // Test capacity management with rapid growth and shrink
    public static void testRapidGrowthAndShrink() {
        System.out.println("Test: Rapid Growth and Shrink");
        DynamicBoundedBlockingQueue queue = new DynamicBoundedBlockingQueue(5);
        
        try {
            for (int i = 1; i <= 20; i++) {
                queue.put(i);
            }
            
            int maxCapacity = queue.capacity();
            System.out.println("  Max capacity reached: " + maxCapacity);
            
            while (!queue.isEmpty()) {
                queue.take();
            }
            
            assert queue.capacity() == 5 : "Queue should shrink back to initial capacity";
            assert queue.isEmpty() : "Queue should be empty";
            
            System.out.println("Rapid growth and shrink test passed");
            System.out.println("  Max capacity: " + maxCapacity + 
                            ", Final capacity: " + queue.capacity());
        } catch (Exception e) {
            System.out.println("Rapid growth and shrink test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Run all tests
    public static void main(String[] args) {
        System.out.println("=== DynamicBoundedBlockingQueue Tests ===\n");
        
        testBasicOperations();
        testQueueGrowth();
        testQueueShrink();
        testThreadSafetyDuringResize();
        testRapidGrowthAndShrink();
        
        System.out.println("\n=== All Tests Completed ===");
    }
}

