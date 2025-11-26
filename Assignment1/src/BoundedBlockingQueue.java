
// Fixed Bounded Blocking Queue Implementation

public class BoundedBlockingQueue {
    private final Integer[] buffer;
    private final int capacity;
    private int size;
    private int putIndex;  
    private int takeIndex; 
    
    // Constructor to create a fixed bounded queue with specified capacity
    public BoundedBlockingQueue(int capacity) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be at least 1");
        }
        this.capacity = capacity;
        this.buffer = new Integer[capacity];
        this.size = 0;
        this.putIndex = 0;
        this.takeIndex = 0;
    }
    
    // Inserts an element into the queue
    public synchronized void put(Integer item) throws InterruptedException {
        while (size == capacity) {
            wait();
        }
        
        // Insert element at putIndex
        buffer[putIndex] = item;
        putIndex = (putIndex + 1) % capacity;
        size++;
        
        // Notify waiting consumers that an item is available
        notifyAll();
    }
    
    // Removes and returns an element from the queue
    public synchronized Integer take() throws InterruptedException {
        while (size == 0) {
            wait();
        }
        
        // Remove element from takeIndex
        Integer item = buffer[takeIndex];
        buffer[takeIndex] = null; 
        takeIndex = (takeIndex + 1) % capacity;
        size--;
        
        // Notify waiting producers that space is available
        notifyAll();
        
        return item;
    }
    
    // Returns the current number of elements in the queue
    public synchronized int size() {
        return size;
    }
    
    // Returns the maximum capacity of the queue
    public int capacity() {
        return capacity;
    }
    
    // Checks if the queue is empty
    public synchronized boolean isEmpty() {
        return size == 0;
    }
    
    // Checks if the queue is full
    public synchronized boolean isFull() {
        return size == capacity;
    }
}

