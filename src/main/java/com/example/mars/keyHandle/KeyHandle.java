package com.example.mars.keyHandle;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

public class KeyHandle {
    public boolean upPressed, downPressed, leftPressed, rightPressed, attackPressed;

    public EventHandler<KeyEvent> keyPressedHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent e) {
            switch (e.getCode()) {
                case W:
                case UP:
                    upPressed = true;
                    break;
                case S:
                case DOWN:
                    downPressed = true;
                    break;
                case A:
                case LEFT:
                    leftPressed = true;
                    break;
                case D:
                case RIGHT:
                    rightPressed = true;
                    break;
                case SPACE:
                    attackPressed = true; // Attack action starts when SPACE is pressed
                    break;
                default:
                    break;
            }
        }
    };

    public EventHandler<KeyEvent> keyReleasedHandler = new EventHandler<KeyEvent>() {
        @Override
        public void handle(KeyEvent e) {
            switch (e.getCode()) {
                case W:
                case UP:
                    upPressed = false;
                    break;
                case S:
                case DOWN:
                    downPressed = false;
                    break;
                case A:
                case LEFT:
                    leftPressed = false;
                    break;
                case D:
                case RIGHT:
                    rightPressed = false;
                    break;
                case SPACE:
                    attackPressed = false; // Reset attack action when SPACE is released
                    break;
                default:
                    break;
            }
        }
    };
}
