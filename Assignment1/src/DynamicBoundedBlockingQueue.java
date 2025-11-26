// Dynamic Bounded Blocking Queue Implementation
public class DynamicBoundedBlockingQueue {
    private Integer[] buffer;
    private final int initialCapacity;
    private int capacity;
    private int size;
    private int putIndex;
    private int takeIndex;
    private final Object resizeLock = new Object(); // Lock for resize operations
    
    // Constructor to create a dynamic bounded queue with initial capacity
    public DynamicBoundedBlockingQueue(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Initial capacity must be at least 1");
        }
        this.initialCapacity = initialCapacity;
        this.capacity = initialCapacity;
        this.buffer = new Integer[initialCapacity];
        this.size = 0;
        this.putIndex = 0;
        this.takeIndex = 0;
    }
    
    // Inserts an element into the queue
    public void put(Integer item) throws InterruptedException {
        synchronized (this) {
            // If queue is full, attempt to grow
            if (size == capacity) {
                grow();
            }
            
            // Wait if resize is in progress 
            while (size == capacity) {
                wait();
            }
            
            // Insert element at putIndex
            buffer[putIndex] = item;
            putIndex = (putIndex + 1) % capacity;
            size++;
            
            // Check if we should shrink 
            if (size < initialCapacity && capacity > initialCapacity) {
                checkAndShrink();
            }
            
            // Notify waiting consumers
            notifyAll();
        }
    }
    
    // Removes and returns an element from the queue
    public Integer take() throws InterruptedException {
        synchronized (this) {
            // Wait for queue to have items
            while (size == 0) {
                wait();
            }
            
            // Remove element from takeIndex
            Integer item = buffer[takeIndex];
            buffer[takeIndex] = null; 
            takeIndex = (takeIndex + 1) % capacity;
            size--;
            
            // Check if we should shrink 
            if (size < initialCapacity && capacity > initialCapacity) {
                checkAndShrink();
            }
            
            // Notify waiting producers
            notifyAll();
            
            return item;
        }
    }
    
    // Grows the queue by initialCapacity amount
    private void grow() {
        synchronized (resizeLock) {
            // Sanity check
            if (size < capacity) {
                return;
            }
            
            int newCapacity = capacity + initialCapacity;
            Integer[] newBuffer = new Integer[newCapacity];
            
            // Copy existing elements maintaining order
            for (int i = 0; i < size; i++) {
                newBuffer[i] = buffer[(takeIndex + i) % capacity];
            }

            buffer = newBuffer;
            capacity = newCapacity;
            takeIndex = 0;
            putIndex = size;
        }
    }
    

    // Checks if queue should shrink and performs shrink if needed
    private void checkAndShrink() {
        synchronized (resizeLock) {
            // Shrink if size is less than initial capacity and capacity is greater than initial capacity
            if (size < initialCapacity && capacity > initialCapacity) {
                Integer[] newBuffer = new Integer[initialCapacity];
                // Copy existing elements to new buffer
                for (int i = 0; i < size; i++) {
                    newBuffer[i] = buffer[(takeIndex + i) % capacity];
                }
                buffer = newBuffer;
                capacity = initialCapacity;
                takeIndex = 0;
                putIndex = size;
            }
        }
    }
    
    // Returns the current number of elements in the queue
    public synchronized int size() {
        return size;
    }
    
    // Returns the current capacity of the queue
    public synchronized int capacity() {
        return capacity;
    }
    
    // Returns the initial capacity of the queue
    public int getInitialCapacity() {
        return initialCapacity;
    }
    
    // Checks if the queue is empty
    public synchronized boolean isEmpty() {
        return size == 0;
    }
    
    // Checks if the queue is full (at current capacity)
    public synchronized boolean isFull() {
        return size == capacity;
    }
}

