package com.example.mars.Socket;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.Socket;

public class AgentChatController {
    @FXML private ScrollPane scrollPane;
    @FXML private VBox messageContainer;
    @FXML private TextField messageField;

    private PrintWriter writer;
    private Socket socket;
    private BufferedReader reader;

    @FXML
    public void initialize() {
        try {
            socket = new Socket("localhost", 5000);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Start receiving messages
            new Thread(this::receiveMessages).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = reader.readLine()) != null) {
                String finalMessage = message;
                Platform.runLater(() -> {
                    if (finalMessage.startsWith("AGENT: ")) {
                        addMessage(finalMessage.substring(7), true); // Agent message
                    } else if (finalMessage.startsWith("CLIENT: ")) {
                        addMessage(finalMessage.substring(8), false); // Client message
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            writer.println("AGENT: " + message);  // Changed from "CLIENT: " to "AGENT: "
            writer.flush();
            addMessage(message, true); // Sent by Agent
            messageField.clear();
        }
    }

    private void addMessage(String message, boolean isClient) {
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);

        // Style the message bubble
        messageLabel.setStyle(isClient
                ? "-fx-background-color: linear-gradient(to bottom, #FEEE99, #FFD166); -fx-text-fill: black; -fx-padding: 10; -fx-background-radius: 20;" // Client style with gradient
                : "-fx-background-color: #A8E6A3; -fx-text-fill: black; -fx-padding: 10; -fx-background-radius: 20;"); // Agent style with light green

        HBox messageBox = new HBox(messageLabel);
        messageBox.setStyle("-fx-padding: 5;");

        // Align based on sender
        messageBox.setAlignment(isClient ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        // Add to the container
        Platform.runLater(() -> {
            messageContainer.getChildren().add(messageBox);
            scrollPane.setVvalue(1.0); // Scroll to the bottom
        });
    }

}
