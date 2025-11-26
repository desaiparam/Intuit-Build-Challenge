import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

// Producer class that reads integers from a source list and places them into a queue.
public class Producer implements Runnable {
    private final BoundedBlockingQueue queue;
    private final List<Integer> sourceList;
    private final AtomicBoolean shutdown;
    private final String name;
    private static final long FLOW_CONTROL_WAIT_TIME_MS = 30000; // 30 seconds
    

    // Constructor for Producer
    public Producer(BoundedBlockingQueue queue, List<Integer> sourceList, 
                    AtomicBoolean shutdown, String name) {
        this.queue = queue;
        this.sourceList = sourceList;
        this.shutdown = shutdown;
        this.name = name;
    }
    
    @Override
    public void run() {
        int index = 0;
        System.out.println("[" + name + "] Started producing");
        try {
            while (!shutdown.get() && index < sourceList.size()) {
                if (queue.isFull()) {
                    handleFullQueue(); // Handle flow control when queue is full
                    if (queue.isFull()) {
                        continue;
                    }
                }
                // Produce item if not shutdown and queue has space
                if (!shutdown.get() && index < sourceList.size() && !queue.isFull()) {
                    Integer item = sourceList.get(index);
                    queue.put(item);
                    System.out.println("[" + name + "] Produced: " + item);
                    index++;
                }
            }
            
            System.out.println("[" + name + "] Finished producing. Total items produced: " + index);
        } catch (InterruptedException e) {
            System.out.println("[" + name + "] Interrupted while producing");
            Thread.currentThread().interrupt();
        }
    }
    
    
    // Waits 30 seconds and monitors if queue size decreases.
    // If size doesn't decrease, pauses and waits for size to decrease.
    private void handleFullQueue() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        int initialSize = queue.size();
        
        System.out.println("[" + name + "] Queue is full (size: " + initialSize + 
                          "), monitoring for " + (FLOW_CONTROL_WAIT_TIME_MS / 1000) + " seconds...");
        
        while (queue.isFull() && !shutdown.get()) {
            long elapsed = System.currentTimeMillis() - startTime;
            
            if (elapsed >= FLOW_CONTROL_WAIT_TIME_MS) {
                int currentSize = queue.size();
                
                if (currentSize < initialSize) {
                    System.out.println("[" + name + "] Queue size decreased (from " + 
                                      initialSize + " to " + currentSize + "), resuming production");
                    return;
                } else {
                    System.out.println("[" + name + "] Queue is full, waiting for consumers... " +
                                      "(size: " + currentSize + ", capacity: " + queue.capacity() + 
                                      ", unchanged for " + (elapsed / 1000) + " seconds)");
                    
                    synchronized (queue) {
                        while (queue.isFull() && !shutdown.get()) {
                            queue.wait(1000);
                            if (!queue.isFull()) {
                                System.out.println("[" + name + "] Queue has space, resuming production");
                                return;
                            }
                        }
                    }
                    if (!queue.isFull()) {
                        return;
                    }
                }
            } else {
                Thread.sleep(1000);
                
                if (!queue.isFull()) {
                    System.out.println("[" + name + "] Queue size decreased during wait, resuming production");
                    return;
                }
            }
        }
    }
}
