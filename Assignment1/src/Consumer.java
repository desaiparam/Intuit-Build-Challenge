import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

// Consumer class that reads integers from a queue and stores them in a destination list.
public class Consumer implements Runnable {
    private final BoundedBlockingQueue queue;
    private final List<Integer> destinationList;
    private final AtomicBoolean shutdown;
    private final String name;
    private static final long EMPTY_QUEUE_CHECK_INTERVAL_MS = 30000; // 30 seconds
  
    // Constructor for Consumer
    public Consumer(BoundedBlockingQueue queue, List<Integer> destinationList, 
                   AtomicBoolean shutdown, String name) {
        this.queue = queue;
        this.destinationList = destinationList;
        this.shutdown = shutdown;
        this.name = name;
    }
    
    @Override
    public void run() {
        int consumedCount = 0;
        System.out.println("[" + name + "] Started consuming");
        
        try {
            while (!shutdown.get()) {
                if (queue.isEmpty()) {
                    handleEmptyQueue();
                }
                
                //If queue is not empty, consume item
                if (!shutdown.get() && !queue.isEmpty()) {
                    Integer item = queue.take();
                    synchronized (destinationList) {
                        destinationList.add(item);
                    }
                    consumedCount++;
                    System.out.println("[" + name + "] Consumed: " + item + " (total: " + consumedCount + ")");
                }
            }
            
            System.out.println("[" + name + "] Finished consuming. Total items consumed: " + consumedCount);
        } catch (InterruptedException e) {
            System.out.println("[" + name + "] Interrupted while consuming");
            Thread.currentThread().interrupt();
        }
    }
    

    // Checks every 30 seconds and prints status message.
    // Waits indefinitely until items become available.
    private void handleEmptyQueue() throws InterruptedException {
        long lastCheckTime = System.currentTimeMillis();
        
        while (queue.isEmpty() && !shutdown.get()) {
            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - lastCheckTime;
            
            if (elapsed >= EMPTY_QUEUE_CHECK_INTERVAL_MS) {
                System.out.println("[" + name + "] Queue is still empty, waiting... " +
                                  "(elapsed: " + (elapsed / 1000) + " seconds)");
                lastCheckTime = currentTime;
            }
            
            synchronized (queue) {
                if (queue.isEmpty() && !shutdown.get()) {
                    queue.wait(1000);
                }
            }
        }
    }
}
