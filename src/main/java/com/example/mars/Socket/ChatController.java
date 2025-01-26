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

public class ChatController {
    @FXML private ScrollPane scrollPane;
    @FXML private VBox messageContainer;
    @FXML private TextField messageField;

    private PrintWriter writer;
    private BufferedReader reader;

    @FXML
    public void initialize() {
        try {
            Socket socket = new Socket("localhost", 5000);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Start receiving messages
            new Thread(() -> {
                try {
                    String message;
                    while ((message = reader.readLine()) != null) {
                        String finalMessage = message;
                        Platform.runLater(() -> {
                            if (finalMessage.startsWith("AGENT: ")) {
                                addMessage(finalMessage.substring(7), false); // Agent message
                            } else if (finalMessage.startsWith("CLIENT: ")) {
                                addMessage(finalMessage.substring(8), true); // Client message
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            writer.println("CLIENT: " + message);
            addMessage(message, true); // Client's own message
            messageField.clear();
        }
    }

    private void addMessage(String message, boolean isClient) {
        String label = isClient ? "Client: " : "Agent: ";
        String style = isClient
                ? "-fx-text-fill: #1976D2; -fx-background-color: #BBDEFB;"
                : "-fx-text-fill: #388E3C; -fx-background-color: #C8E6C9;";

        Label messageLabel = new Label(label + message);
        messageLabel.setWrapText(true);
        messageLabel.setStyle(style);

        HBox messageBox = new HBox(messageLabel);
        messageBox.setAlignment(isClient ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);
        messageBox.setStyle("-fx-padding: 10; -fx-background-radius: 10; -fx-border-width: 1;");

        messageContainer.getChildren().add(messageBox);
        scrollPane.setVvalue(1.0); // Scroll to the bottom
    }
}
