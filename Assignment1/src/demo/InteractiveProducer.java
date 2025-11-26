import java.io.*;
import java.net.*;
import java.util.Scanner;

// Interactive Producer - Run this in one terminal
// Connects to QueueServer or DynamicQueueServer and allows you to manually produce items

public class InteractiveProducer {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT_FIXED = 8888;
    private static final int DEFAULT_PORT_DYNAMIC = 8889;
    
    public static void main(String[] args) {
        boolean isDynamic = false;
        String host = DEFAULT_HOST;
        int port = DEFAULT_PORT_FIXED;
        
        // Parse command line arguments
        int argIndex = 0;
        if (args.length > argIndex && (args[argIndex].equals("--dynamic") || args[argIndex].equals("-d"))) {
            isDynamic = true;
            port = DEFAULT_PORT_DYNAMIC;
            argIndex++;
        }
        
        if (args.length > argIndex) {
            host = args[argIndex];
            argIndex++;
        }
        if (args.length > argIndex) {
            try {
                port = Integer.parseInt(args[argIndex]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port, using default: " + (isDynamic ? DEFAULT_PORT_DYNAMIC : DEFAULT_PORT_FIXED));
            }
        }
        
        Scanner scanner = new Scanner(System.in);
        // Print welcome message
        System.out.println("========================================");
        System.out.println("   Interactive Producer" + (isDynamic ? " (Dynamic Queue)" : ""));
        System.out.println("========================================");
        System.out.println("Connecting to " + (isDynamic ? "dynamic queue " : "") + "server at " + host + ":" + port + "...");
        
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            
            System.out.println("✓ Connected to " + (isDynamic ? "dynamic queue " : "") + "server!");
            System.out.println();
            System.out.println("Commands:");
            System.out.println("  - Enter a number to produce it");
            System.out.println("  - Type 'size' to check queue size" + (isDynamic ? " and capacity" : ""));
            System.out.println("  - Type 'status' to see queue status");
            System.out.println("  - Type 'quit' to exit");
            if (isDynamic) {
                System.out.println();
                System.out.println("NOTE: Queue will GROW when full!");
            }
            System.out.println("========================================");
            System.out.println();
            
            String input;
            int itemCount = 0;
            
            while (true) {
                System.out.print("Producer> ");
                input = scanner.nextLine().trim();
                
                if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                    out.println("QUIT");
                    in.readLine(); 
                    System.out.println("Producer shutting down. Produced " + itemCount + " items total.");
                    break;
                }
                
                if (input.equalsIgnoreCase("size")) {
                    out.println("SIZE");
                    String response = in.readLine();
                    if (response.startsWith("OK")) {
                        String[] parts = response.split(" ");
                        if (isDynamic && parts.length > 3) {
                            // Dynamic queue size and capacity
                            System.out.println("  Queue size: " + parts[1] + " / " + parts[2] + " (initial: " + parts[3] + ")");
                            if (Integer.parseInt(parts[2]) > Integer.parseInt(parts[3])) {
                                System.out.println("  ⬆ Queue has GROWN from initial capacity!");
                            }
                        } else {
                            // Fixed queue size and capacity
                            System.out.println("  Queue size: " + parts[1] + " / " + parts[2]);
                        }
                    }
                    continue;
                }
                
                if (input.equalsIgnoreCase("status")) {
                    out.println("STATUS");
                    String response = in.readLine();
                    if (response.startsWith("OK")) {
                        String[] parts = response.split(" ");
                        if (isDynamic && parts.length > 5) {
                            // Dynamic queue size, capacity, initial capacity, is empty, and is full and increase queue size
                            System.out.println("  Queue size: " + parts[1] + " / " + parts[2] + " (initial: " + parts[3] + ")");
                            System.out.println("  Is empty: " + parts[4]);
                            System.out.println("  Is full: " + parts[5]);
                            if (Integer.parseInt(parts[2]) > Integer.parseInt(parts[3])) {
                                System.out.println("  ⬆ Queue has GROWN!");
                            } else if (Integer.parseInt(parts[2]) == Integer.parseInt(parts[3]) && Integer.parseInt(parts[1]) < Integer.parseInt(parts[3])) {
                                System.out.println("  ⬇ Queue can shrink (size < initial capacity)");
                            }
                        } else {
                            // Fixed queue size, capacity, is empty, and is full
                            System.out.println("  Queue size: " + parts[1] + " / " + parts[2]);
                            System.out.println("  Is empty: " + parts[3]);
                            System.out.println("  Is full: " + parts[4]);
                        }
                    }
                    continue;
                }
                
                // Try to parse as integer
                try {
                    int item = Integer.parseInt(input);
                    int oldCapacity = 0;
                    
                    if (isDynamic) {
                        out.println("SIZE");
                        String sizeResponse = in.readLine();
                        if (sizeResponse.startsWith("OK")) {
                            String[] parts = sizeResponse.split(" ");
                            oldCapacity = Integer.parseInt(parts[2]);
                        }
                    }
                    
                    out.println("PUT " + item);
                    String response = in.readLine();
                    
                    if (response.startsWith("OK")) {
                        String[] parts = response.split(" ");
                        int queueSize = Integer.parseInt(parts[1]);
                        itemCount++;
                        
                        if (isDynamic && parts.length > 2) {
                            // Dynamic queue size and capacity
                            int newCapacity = Integer.parseInt(parts[2]);
                            System.out.println("Produced: " + item + " (queue size: " + queueSize + ", capacity: " + newCapacity + ")");
                            
                            if (newCapacity > oldCapacity) {
                                System.out.println(" Queue GREW from " + oldCapacity + " to " + newCapacity + "!");
                            }
                        } else {
                            // Fixed queue size
                            System.out.println("Produced: " + item + " (queue size: " + queueSize + ")");
                            
                            // Check if full
                            out.println("STATUS");
                            String statusResponse = in.readLine();
                            if (statusResponse.startsWith("OK")) {
                                String[] statusParts = statusResponse.split(" ");
                                if (statusParts.length > 4 && statusParts[4].equals("true")) {
                                    System.out.println("  ⚠ Queue is now FULL!");
                                }
                            }
                        }
                    } else {
                        System.out.println("  ✗ Error: " + response);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("  ✗ Invalid input. Enter a number, 'size', 'status', or 'quit'");
                }
            }
        } catch (ConnectException e) {
            // Print error message if unable to connect to server
            System.out.println("✗ Could not connect to server at " + host + ":" + port);
            if (isDynamic) {
                System.out.println("  Make sure DynamicQueueServer is running first!");
                System.out.println("  Run: java -cp bin DynamicQueueServer [initialCapacity] [port]");
            } else {
                System.out.println("  Make sure QueueServer is running first!");
                System.out.println("  Run: java -cp bin QueueServer [capacity] [port]");
            }
        } catch (IOException e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
        
        scanner.close();
    }
}
