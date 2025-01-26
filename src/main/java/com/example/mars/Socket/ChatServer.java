package com.example.mars.Socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {

    // Thread-safe list to manage connected clients
    private static final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Chat Server is running...");

            while (true) {
                // Accept new client connections
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress());

                // Create and start a new ClientHandler thread
                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Server encountered an error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Inner class to handle individual client connections
    private static class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter writer;
        private BufferedReader reader;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                // Initialize input/output streams for the client
                this.writer = new PrintWriter(socket.getOutputStream(), true);
                this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                System.err.println("Error setting up client streams: " + e.getMessage());
                closeAll();
            }
        }

        @Override
        public void run() {
            try {
                String message;
                // Continuously listen for messages from the client
                while ((message = reader.readLine()) != null) {
                    System.out.println("Received: " + message);
                    broadcastMessage(message);
                }
            } catch (IOException e) {
                System.err.println("Error in client communication: " + e.getMessage());
            } finally {
                // Remove client from the list and clean up resources
                clients.remove(this);
                System.out.println("Client disconnected: " + socket.getInetAddress());
                closeAll();
            }
        }

        // Broadcast a message to all connected clients
        private void broadcastMessage(String message) {
            System.out.println("Broadcasting message: " + message); // Debug line
            for (ClientHandler client : clients) {
                try {
                    if (client != this) { // Skip sending the message back to the sender
                        if (message.startsWith("AGENT: ")) {
                            System.out.println("Sending agent message: " + message); // Debug line
                            client.writer.println(message);
                        } else if (message.startsWith("CLIENT: ")) {
                            System.out.println("Sending client message: " + message); // Debug line
                            client.writer.println(message);
                        }
                        client.writer.flush();
                    }
                } catch (Exception e) {
                    System.err.println("Error broadcasting message: " + e.getMessage());
                    client.closeAll();
                }
            }
        }

        // Close all resources for this client
        private void closeAll() {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (writer != null) {
                    writer.close();
                }
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing client resources: " + e.getMessage());
            }
        }
    }
}
