package com.example.game;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button startButton;
    private Button exitButton;
    public static MediaPlayer mediaPlayer;
    private boolean isMuted; // Track mute state

    private static MainActivity instance; // Store instance for context access

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this; // Assign instance

        startButton = findViewById(R.id.startButton);
        exitButton = findViewById(R.id.exitButton);

        // Load mute state
        SharedPreferences preferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        isMuted = preferences.getBoolean("MUTE_STATE", false);

        // Initialize and start background music
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(this, R.raw.background_music);
            mediaPlayer.setLooping(true);
            if (!isMuted) {
                mediaPlayer.start();
            }
        } else {
            setMusicVolume(isMuted ? 0.0f : 1.0f);
        }

        // Navigate to CategoryActivity
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
            startActivity(intent);
        });

        // Exit the app
        exitButton.setOnClickListener(v -> {
            stopMusic();
            finishAffinity();
        });
    }

    public static void stopMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public static void resumeMusic() {
        SharedPreferences preferences = instance.getSharedPreferences("GamePrefs", MODE_PRIVATE);
        boolean isMuted = preferences.getBoolean("MUTE_STATE", false);

        if (mediaPlayer != null) {
            mediaPlayer.setVolume(isMuted ? 0.0f : 1.0f, isMuted ? 0.0f : 1.0f);
            if (!isMuted && !mediaPlayer.isPlaying()) {
                mediaPlayer.start(); // Ensure music starts playing
            }
        }
    }



    public static void setMusicVolume(float volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volume, volume);
        }
    }

    // ✅ Add this method to provide application context
    public static Context getAppContext() {
        return instance;
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeMusic(); // Resume music when returning to MainActivity
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
