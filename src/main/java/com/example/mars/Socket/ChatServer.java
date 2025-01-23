package com.example.mars.Socket;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    private static final List<PrintWriter> clients = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("Chat Server is running...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
                clients.add(writer);

                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            String message;
            while ((message = reader.readLine()) != null) {
                System.out.println("Received message: " + message); // Debug print
                String finalMessage = message;
                // Broadcast to all clients
                for (PrintWriter client : clients) {
                    client.println(finalMessage);
                    client.flush();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}