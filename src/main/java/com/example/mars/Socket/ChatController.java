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
    private Socket socket;
    private BufferedReader reader;

    @FXML
    public void initialize() {
        try {
            socket = new Socket("localhost", 5000);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Start message receiving thread
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
                    boolean isAgentMessage = finalMessage.startsWith("AGENT: ");
                    boolean isClientMessage = finalMessage.startsWith("CLIENT: ");

                    if (isAgentMessage) {
                        addMessage(finalMessage.substring(7), false);
                    } else if (isClientMessage) {
                        addMessage(finalMessage.substring(8), true);
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
            String fullMessage = "CLIENT: " + message;
            writer.println(fullMessage);
            writer.flush();
            addMessage(message, true);
            messageField.clear();
        }
    }

    private void addMessage(String message, boolean isClient) {
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.getStyleClass().add(isClient ? "client-message" : "agent-message");

        HBox messageBox = new HBox(messageLabel);
        messageBox.getStyleClass().add("message-box");
        messageBox.setAlignment(isClient ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT);

        messageContainer.getChildren().add(messageBox);
        scrollPane.setVvalue(1.0);
    }
}