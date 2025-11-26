import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

// Queue Server - Run this first in a separate terminal
// This server holds the shared queue and handles requests from producers and consumers
public class QueueServer {
    private static BoundedBlockingQueue queue;
    private static ServerSocket serverSocket;
    private static AtomicBoolean running = new AtomicBoolean(true);
    private static final int DEFAULT_PORT = 8888;
    
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        int capacity = 10;
        
        if (args.length > 0) {
            try {
                capacity = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid capacity, using default: 10");
            }
        }
        
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port, using default: 8888");
            }
        }
        
        queue = new BoundedBlockingQueue(capacity);
        
        System.out.println("========================================");
        System.out.println("   Queue Server");
        System.out.println("========================================");
        System.out.println("Queue capacity: " + capacity);
        System.out.println("Server port: " + port);
        System.out.println("Server is running...");
        System.out.println("Connect producers and consumers to this server");
        System.out.println("Press Ctrl+C to stop the server");
        System.out.println("========================================");
        System.out.println();
        
        // Start server
        try {
            serverSocket = new ServerSocket(port);
            
            // Add shutdown hook to handle Ctrl+C
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                running.set(false);
                try {
                    if (serverSocket != null) serverSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing server socket: " + e.getMessage());
                }
                System.out.println("\nServer shutting down...");
            }));
            
            while (running.get()) {
                try {
                    // Accept client connection
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new ClientHandler(clientSocket, queue)).start();
                } catch (IOException e) {
                    if (running.get()) {
                        System.err.println("Error accepting client: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }
    
    static class ClientHandler implements Runnable {
        private Socket socket;
        private BoundedBlockingQueue queue;
        
        public ClientHandler(Socket socket, BoundedBlockingQueue queue) {
            this.socket = socket;
            this.queue = queue;
        }
        
        @Override
        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
            ) {
                // Read commands from client
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String[] parts = inputLine.split(" ", 2);
                    String command = parts[0];
                    
                    if (command.equals("PUT")) {
                        try {
                            int item = Integer.parseInt(parts[1]);
                            queue.put(item);
                            out.println("OK " + queue.size());
                        } catch (InterruptedException e) {
                            out.println("ERROR Interrupted");
                            break;
                        } catch (Exception e) {
                            out.println("ERROR " + e.getMessage());
                        }
                    } else if (command.equals("TAKE")) {
                        try {
                            Integer item = queue.take();
                            out.println("OK " + item + " " + queue.size());
                        } catch (InterruptedException e) {
                            out.println("ERROR Interrupted");
                            break;
                        }
                    } else if (command.equals("SIZE")) {
                        out.println("OK " + queue.size() + " " + queue.capacity());
                    } else if (command.equals("STATUS")) {
                        out.println("OK " + queue.size() + " " + queue.capacity() + 
                                   " " + queue.isEmpty() + " " + queue.isFull());
                    } else if (command.equals("QUIT")) {
                        out.println("OK");
                        break;
                    } else {
                        out.println("ERROR Unknown command");
                    }
                }
            } catch (IOException e) {
                System.err.println("Error handling client: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.err.println("Error closing socket: " + e.getMessage());
                }
            }
        }
    }
}

