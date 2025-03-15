package com.example.game;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class CategoryActivity extends AppCompatActivity {
    private ImageView volumeIcon;
    private boolean isMuted;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        setTitle("Select a Category");

        // Initialize SharedPreferences
        preferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        isMuted = preferences.getBoolean("MUTE_STATE", false);

        // Find views
        ImageView backButton = findViewById(R.id.backButton);
        volumeIcon = findViewById(R.id.volumeIcon);

        // Set correct icon state
        updateVolumeIcon();

        // Handle back button click
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Volume toggle functionality
        volumeIcon.setOnClickListener(view -> toggleVolume());

        // Button initializations
        Button btnPHNI = findViewById(R.id.btnPHNI);
        Button btnPCA = findViewById(R.id.btnPCA);
        Button btnPGNW = findViewById(R.id.btnPGNW);
        Button btnPLD = findViewById(R.id.btnPLD);
        Button btnPGS = findViewById(R.id.btnPGS);

        // Button click listeners
        btnPHNI.setOnClickListener(view -> startActivity(new Intent(CategoryActivity.this, PHNIActivity.class)));
        btnPCA.setOnClickListener(view -> startActivity(new Intent(CategoryActivity.this, PCAActivity.class)));
        btnPGNW.setOnClickListener(view -> startActivity(new Intent(CategoryActivity.this, PGNWActivity.class)));
        btnPLD.setOnClickListener(view -> startActivity(new Intent(CategoryActivity.this, PLDActivity.class)));
        btnPGS.setOnClickListener(view -> startActivity(new Intent(CategoryActivity.this, PGSActivity.class)));
    }

    private void toggleVolume() {
        isMuted = !isMuted; // Toggle state

        // Apply mute/unmute to all music
        MainActivity.setMusicVolume(isMuted ? 0.0f : 1.0f);
        PCA_TT_GM_ACTIVITY.setQuizMusicVolume(isMuted ? 0.0f : 1.0f);
        PCA_SM_GM_ACTIVITY.setQuizMusicVolume(isMuted ? 0.0f : 1.0f);
        PCA_FM_GM_ACTIVITY.setQuizMusicVolume(isMuted ? 0.0f : 1.0f);

        PGNW_TT_ACTIVITY.setQuizMusicVolume(isMuted ? 0.0f : 1.0f);
        PGNW_SM_ACTIVITY.setQuizMusicVolume(isMuted ? 0.0f : 1.0f);
        PGNW_FM_ACTIVITY.setQuizMusicVolume(isMuted ? 0.0f : 1.0f);

        PGS_TT_ACTIVITY.setQuizMusicVolume(isMuted ? 0.0f : 1.0f);
        PGS_SM_ACTIVITY.setQuizMusicVolume(isMuted ? 0.0f : 1.0f);
        PGS_FM_ACTIVITY.setQuizMusicVolume(isMuted ? 0.0f : 1.0f);

        PHNI_TT_ACTIVITY.setQuizMusicVolume(isMuted ? 0.0f : 1.0f);
        PHNI_SM_ACTIVITY.setQuizMusicVolume(isMuted ? 0.0f : 1.0f);
        PHNI_FM_ACTIVITY.setQuizMusicVolume(isMuted ? 0.0f : 1.0f);

        PLD_TT_ACTIVITY.setQuizMusicVolume(isMuted ? 0.0f : 1.0f);
        PLD_SM_ACTIVITY.setQuizMusicVolume(isMuted ? 0.0f : 1.0f);
        PLD_FM_ACTIVITY.setQuizMusicVolume(isMuted ? 0.0f : 1.0f);

        // If unmuted, restart the music if it's not playing
        if (!isMuted && MainActivity.mediaPlayer != null && !MainActivity.mediaPlayer.isPlaying()) {
            MainActivity.mediaPlayer.start();
        }

        // Save state
        saveMuteState();
        updateVolumeIcon();
    }



    private void updateVolumeIcon() {
        volumeIcon.setImageResource(isMuted ? R.drawable.ic_volume_off : R.drawable.ic_volume_up);
    }

    private void saveMuteState() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("MUTE_STATE", isMuted);
        editor.apply();
    }
}
