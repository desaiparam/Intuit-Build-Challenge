import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

// Main demonstration application for Producer-Consumer pattern.
public class ProducerConsumerDemo {
    private static final int QUEUE_CAPACITY = 5;
    private static final int NUM_PRODUCERS = 2;
    private static final int NUM_CONSUMERS = 2;
    private static final int ITEMS_PER_PRODUCER = 20;
    
    private BoundedBlockingQueue queue;
    private List<Integer> sourceList;
    private List<Integer> destinationList;
    private List<Thread> producerThreads;
    private List<Thread> consumerThreads;
    private AtomicBoolean shutdown;
    
    public ProducerConsumerDemo() {
        this.queue = new BoundedBlockingQueue(QUEUE_CAPACITY);
        this.sourceList = Collections.synchronizedList(new ArrayList<>());
        this.destinationList = Collections.synchronizedList(new ArrayList<>());
        this.producerThreads = new ArrayList<>();
        this.consumerThreads = new ArrayList<>();
        this.shutdown = new AtomicBoolean(false);
    }
    
    // Initializes the source list with integers for producers to consume
    private void initializeSourceList() {
        int itemId = 1;
        for (int i = 0; i < NUM_PRODUCERS; i++) {
            for (int j = 0; j < ITEMS_PER_PRODUCER; j++) {
                sourceList.add(itemId++);
            }
        }
        System.out.println("Initialized source list with " + sourceList.size() + " items");
    }
    
    // Creates and starts producer threads
    private void startProducers() {
        int itemsPerProducer = sourceList.size() / NUM_PRODUCERS;
        int remainder = sourceList.size() % NUM_PRODUCERS;
        
        for (int i = 0; i < NUM_PRODUCERS; i++) {
            int startIndex = i * itemsPerProducer;
            int endIndex = startIndex + itemsPerProducer + (i < remainder ? 1 : 0);
            
            List<Integer> producerSource = new ArrayList<>(
                sourceList.subList(startIndex, endIndex)
            );
            
            Producer producer = new Producer(queue, producerSource, shutdown, 
                                           "Producer-" + (i + 1));
            Thread thread = new Thread(producer);
            thread.setName("ProducerThread-" + (i + 1));
            producerThreads.add(thread);
            thread.start();
        }
        
        System.out.println("Started " + NUM_PRODUCERS + " producer thread(s)");
    }
    
    // Creates and starts consumer threads
    private void startConsumers() {
        for (int i = 0; i < NUM_CONSUMERS; i++) {
            Consumer consumer = new Consumer(queue, destinationList, shutdown, 
                                            "Consumer-" + (i + 1));
            Thread thread = new Thread(consumer);
            thread.setName("ConsumerThread-" + (i + 1));
            consumerThreads.add(thread);
            thread.start();
        }
        
        System.out.println("Started " + NUM_CONSUMERS + " consumer thread(s)");
    }
    
    // Waits for all threads to complete
    private void waitForCompletion() throws InterruptedException {
        // Wait for all producers to finish
        for (Thread thread : producerThreads) {
            thread.join();
        }
        System.out.println("\nAll producers finished");
        
        // Give consumers time to process remaining items
        Thread.sleep(2000);
        
        // Signal shutdown to consumers
        shutdown.set(true);
        
        // Notify all waiting threads
        synchronized (queue) {
            queue.notifyAll();
        }
        
        // Wait for all consumers to finish
        for (Thread thread : consumerThreads) {
            thread.join(5000); // Wait max 5 seconds
            if (thread.isAlive()) {
                System.out.println("Warning: " + thread.getName() + " did not terminate gracefully");
            }
        }
        System.out.println("All consumers finished");
    }
    
    // Prints summary statistics
    private void printSummary() {
        System.out.println("\n=== Summary ===");
        System.out.println("Source list size: " + sourceList.size());
        System.out.println("Destination list size: " + destinationList.size());
        System.out.println("Queue final size: " + queue.size());
        System.out.println("Queue capacity: " + queue.capacity());
        
        // Verify all items were consumed
        if (sourceList.size() == destinationList.size()) {
            System.out.println("\n✓ SUCCESS: All items were consumed!");
        } else {
            System.out.println("\n✗ WARNING: Item count mismatch!");
        }
        
        // Show some sample items
        System.out.println("\nSample items from destination (first 10):");
        int sampleSize = Math.min(10, destinationList.size());
        for (int i = 0; i < sampleSize; i++) {
            System.out.print(destinationList.get(i) + " ");
        }
        System.out.println();
    }
    
    // Sets up shutdown hook for graceful termination
    private void setupShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n\nShutdown signal received. Initiating graceful shutdown...");
            shutdown.set(true);
            
            // Notify all waiting threads
            synchronized (queue) {
                queue.notifyAll();
            }
            
            // Wait a bit for threads to finish
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            System.out.println("Shutdown complete.");
        }));
    }
    
    // Main execution method
    public void run() {
        try {
            System.out.println("=== Producer-Consumer Pattern Demo ===\n");
            System.out.println("Configuration:");
            System.out.println("  Queue Capacity: " + QUEUE_CAPACITY);
            System.out.println("  Number of Producers: " + NUM_PRODUCERS);
            System.out.println("  Number of Consumers: " + NUM_CONSUMERS);
            System.out.println("  Items per Producer: " + ITEMS_PER_PRODUCER);
            System.out.println("\nPress Ctrl+C to shutdown gracefully\n");
            
            setupShutdownHook();
            initializeSourceList();
            startConsumers(); // Start consumers first so they're ready
            Thread.sleep(500); // Small delay to let consumers start
            startProducers(); // Start producers
            
            waitForCompletion(); // Wait for all threads to complete
            printSummary();
            
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted");
            Thread.currentThread().interrupt();
        }
    }
    
    // Main entry point
    public static void main(String[] args) {
        ProducerConsumerDemo demo = new ProducerConsumerDemo();
        demo.run();
    }
}

