package com.example.mars.keyHandle;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

public class KeyHandle {
    public boolean upPressed, downPressed, leftPressed, rightPressed, attackPressed, interactPressed;

    public EventHandler<KeyEvent> keyPressedHandler = event -> {
        switch (event.getCode()) {
            case UP, W -> upPressed = true;
            case DOWN, S -> downPressed = true;
            case LEFT, A -> leftPressed = true;
            case RIGHT, D -> rightPressed = true;
            case SPACE -> attackPressed = true;
            case E -> interactPressed = true;
        }
    };

    public EventHandler<KeyEvent> keyReleasedHandler = event -> {
        switch (event.getCode()) {
            case UP, W -> upPressed = false;
            case DOWN, S -> downPressed = false;
            case LEFT, A -> leftPressed = false;
            case RIGHT, D -> rightPressed = false;
            case SPACE -> attackPressed = false;
            case E -> interactPressed = false;
        }
    };
}