import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

// Integration tests for full producer-consumer scenarios
public class ProducerConsumerIntegrationTest {
    
    // Test single producer, single consumer scenario
    public static void testSingleProducerSingleConsumer() {
        System.out.println("Test: Single Producer, Single Consumer");
        
        BoundedBlockingQueue queue = new BoundedBlockingQueue(5);
        List<Integer> sourceList = new ArrayList<>();
        List<Integer> destinationList = Collections.synchronizedList(new ArrayList<>());
        AtomicBoolean shutdown = new AtomicBoolean(false);
        
        for (int i = 1; i <= 20; i++) {
            sourceList.add(i);
        }
        
        Producer producer = new Producer(queue, sourceList, shutdown, "Producer-1");
        Consumer consumer = new Consumer(queue, destinationList, shutdown, "Consumer-1");
        
        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);
        
        try {
            consumerThread.start();
            Thread.sleep(100);
            producerThread.start();
            
            producerThread.join();
            Thread.sleep(500);
            
            shutdown.set(true);
            synchronized (queue) {
                queue.notifyAll();
            }
            
            consumerThread.join(2000);
            
            assert sourceList.size() == destinationList.size() : 
                "All items should be consumed";
            assert queue.isEmpty() : "Queue should be empty";
            
            System.out.println("Single producer-consumer test passed");
            System.out.println("  Source items: " + sourceList.size() + 
                            ", Consumed items: " + destinationList.size());
        } catch (InterruptedException e) {
            System.out.println("Single producer-consumer test failed: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    
    // Test multiple producers, multiple consumers scenario
    public static void testMultipleProducersConsumers() {
        System.out.println("Test: Multiple Producers, Multiple Consumers");
        
        BoundedBlockingQueue queue = new BoundedBlockingQueue(5);
        List<Integer> sourceList = new ArrayList<>();
        List<Integer> destinationList = Collections.synchronizedList(new ArrayList<>());
        AtomicBoolean shutdown = new AtomicBoolean(false);
        
        final int NUM_PRODUCERS = 3;
        final int NUM_CONSUMERS = 2;
        final int ITEMS_PER_PRODUCER = 15;
        
        int itemId = 1;
        for (int i = 0; i < NUM_PRODUCERS; i++) {
            for (int j = 0; j < ITEMS_PER_PRODUCER; j++) {
                sourceList.add(itemId++);
            }
        }
        
        List<Thread> producerThreads = new ArrayList<>();
        List<Thread> consumerThreads = new ArrayList<>();
        
        int itemsPerProducer = sourceList.size() / NUM_PRODUCERS;
        for (int i = 0; i < NUM_PRODUCERS; i++) {
            int startIndex = i * itemsPerProducer;
            int endIndex = (i == NUM_PRODUCERS - 1) ? sourceList.size() : (i + 1) * itemsPerProducer;
            List<Integer> producerSource = new ArrayList<>(sourceList.subList(startIndex, endIndex));
            
            Producer producer = new Producer(queue, producerSource, shutdown, 
                                            "Producer-" + (i + 1));
            Thread thread = new Thread(producer);
            producerThreads.add(thread);
        }
        
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            Consumer consumer = new Consumer(queue, destinationList, shutdown, 
                                            "Consumer-" + (i + 1));
            Thread thread = new Thread(consumer);
            consumerThreads.add(thread);
        }
        
        try {
            for (Thread t : consumerThreads) {
                t.start();
            }
            Thread.sleep(200);
            
            for (Thread t : producerThreads) {
                t.start();
            }
            
            for (Thread t : producerThreads) {
                t.join();
            }
            
            Thread.sleep(1000);
            
            shutdown.set(true);
            synchronized (queue) {
                queue.notifyAll();
            }
            
            for (Thread t : consumerThreads) {
                t.join(3000);
            }
            
            assert sourceList.size() == destinationList.size() : 
                "All items should be consumed";
            assert queue.isEmpty() : "Queue should be empty";
            
            System.out.println("Multiple producers-consumers test passed");
            System.out.println("  Source items: " + sourceList.size() + 
                            ", Consumed items: " + destinationList.size());
            System.out.println("  Producers: " + NUM_PRODUCERS + 
                            ", Consumers: " + NUM_CONSUMERS);
        } catch (InterruptedException e) {
            System.out.println("Multiple producers-consumers test failed: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    
    // Test flow control - producer pausing when queue is full
    public static void testProducerFlowControl() {
        System.out.println("Test: Producer Flow Control");
        
        BoundedBlockingQueue queue = new BoundedBlockingQueue(3);
        List<Integer> sourceList = new ArrayList<>();
        List<Integer> destinationList = Collections.synchronizedList(new ArrayList<>());
        AtomicBoolean shutdown = new AtomicBoolean(false);
        
        for (int i = 1; i <= 10; i++) {
            sourceList.add(i);
        }
        
        Producer producer = new Producer(queue, sourceList, shutdown, "Producer-FC");
        Consumer consumer = new Consumer(queue, destinationList, shutdown, "Consumer-Slow");
        
        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);
        
        try {
            consumerThread.start();
            Thread.sleep(100);
            producerThread.start();
            
            Thread.sleep(2000);
            
            producerThread.join();
            Thread.sleep(1000);
            
            shutdown.set(true);
            synchronized (queue) {
                queue.notifyAll();
            }
            
            consumerThread.join(3000);
            
                System.out.println("Producer flow control test passed");
            System.out.println("  Source items: " + sourceList.size() + 
                            ", Consumed items: " + destinationList.size());
        } catch (InterruptedException e) {
            System.out.println("Producer flow control test failed: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    
    // Test flow control - consumer waiting when queue is empty
    public static void testConsumerFlowControl() {
        System.out.println("Test: Consumer Flow Control");
        
        BoundedBlockingQueue queue = new BoundedBlockingQueue(5);
        List<Integer> sourceList = new ArrayList<>();
        List<Integer> destinationList = Collections.synchronizedList(new ArrayList<>());
        AtomicBoolean shutdown = new AtomicBoolean(false);
        
        for (int i = 1; i <= 3; i++) {
            sourceList.add(i);
        }
        
        Producer producer = new Producer(queue, sourceList, shutdown, "Producer-Slow");
        Consumer consumer = new Consumer(queue, destinationList, shutdown, "Consumer-FC");
        
        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);
        
        try {
            consumerThread.start();
            Thread.sleep(500);
            
            producerThread.start();
            Thread.sleep(1000);
            
            producerThread.join();
            Thread.sleep(2000);
            
            shutdown.set(true);
            synchronized (queue) {
                queue.notifyAll();
            }
            
            consumerThread.join(3000);
            
            System.out.println("Consumer flow control test passed");
            System.out.println("  Source items: " + sourceList.size() + 
                            ", Consumed items: " + destinationList.size());
        } catch (InterruptedException e) {
            System.out.println("Consumer flow control test failed: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    
    // Test 30-second flow control monitoring - producer waiting when queue is full for 30+ seconds
    public static void test30SecondFlowControl() {
        System.out.println("Test: 30-Second Flow Control Monitoring");
        System.out.println("  (This test will take ~35 seconds to demonstrate the 30-second monitoring)");
        
        BoundedBlockingQueue queue = new BoundedBlockingQueue(3);
        List<Integer> sourceList = new ArrayList<>();
        List<Integer> destinationList = Collections.synchronizedList(new ArrayList<>());
        AtomicBoolean shutdown = new AtomicBoolean(false);
        
        for (int i = 1; i <= 20; i++) {
            sourceList.add(i);
        }
        
        Producer producer = new Producer(queue, sourceList, shutdown, "Producer-30Sec");
        Consumer consumer = new Consumer(queue, destinationList, shutdown, "Consumer-VerySlow");
        
        Thread producerThread = new Thread(producer);
        Thread consumerThread = new Thread(consumer);
        
        try {
            consumerThread.start();
            Thread.sleep(100);
            producerThread.start();
            
                Thread.sleep(2000);
            
            System.out.println("  Queue should be full now. Waiting 35 seconds to see flow control messages...");
            System.out.println("  (You should see 'monitoring for 30 seconds' message, then 'waiting for consumers' message)");
            
            Thread.sleep(35000);
            
            Thread.sleep(5000);
            
            producerThread.join(5000);
            Thread.sleep(2000);
            
            shutdown.set(true);
            synchronized (queue) {
                queue.notifyAll();
            }
            
            consumerThread.join(5000);
            
            System.out.println("30-second flow control test completed");
            System.out.println("  Source items: " + sourceList.size() + 
                            ", Consumed items: " + destinationList.size());
            System.out.println("  (Check output above for flow control messages)");
        } catch (InterruptedException e) {
            System.out.println("30-second flow control test failed: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
    
    // Run all integration tests
    public static void main(String[] args) {
        System.out.println("=== Producer-Consumer Integration Tests ===\n");
        
        testSingleProducerSingleConsumer();
        System.out.println();
        
        testMultipleProducersConsumers();
        System.out.println();
        
        testProducerFlowControl();
        System.out.println();
        
        testConsumerFlowControl();
        System.out.println();
        
        test30SecondFlowControl();
        System.out.println();
        
        System.out.println("\n=== All Integration Tests Completed ===");
       
    }
}

