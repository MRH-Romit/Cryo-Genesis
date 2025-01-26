package com.example.mars.sound;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundManager {
    private static MediaPlayer mediaPlayer;
    private static MediaPlayer soundEffectPlayer;

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

    public static void playSound(String filePath) {
        try {
            Media sound = new Media(SoundManager.class.getResource(filePath).toExternalForm());
            soundEffectPlayer = new MediaPlayer(sound);
            soundEffectPlayer.setVolume(0.8); // Set sound effect volume
            soundEffectPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public static void stopSound() {
        if (soundEffectPlayer != null) {
            soundEffectPlayer.stop();
        }
    }
}