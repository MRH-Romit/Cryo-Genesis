package com.example.mars.sound;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundManager {
    private static MediaPlayer mediaPlayer;

    public static void playLandingPageMusic(String filePath) {
        stopMusic(); // Stop previous music before playing new one
        try {
            Media media = new Media(SoundManager.class.getResource(filePath).toExternalForm());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE); // Loop indefinitely
            mediaPlayer.setVolume(0.5); // Set volume (0.0 to 1.0)
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}
