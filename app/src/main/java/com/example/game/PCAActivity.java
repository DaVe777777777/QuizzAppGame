package com.example.game;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PCAActivity extends AppCompatActivity {
    private TextView progressText, progressSurvival, progressFastMatch;
    private DBHelper dbHelper;
    private String userId = "1"; // Replace with actual user ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pca);
        setTitle("Game Mode"); // Set Activity Title

        LinearLayout btnTimeTrial = findViewById(R.id.btnTimeTrial);
        LinearLayout btnSurvivalMode = findViewById(R.id.btnSurvivalMode);
        LinearLayout btnFastMatch = findViewById(R.id.btnFastMatch);
        ImageView btnBack = findViewById(R.id.btnBack); // Back button

        progressText = findViewById(R.id.progressText);
        progressSurvival = findViewById(R.id.progressSurvival);
        progressFastMatch = findViewById(R.id.progressFastMatch);

        dbHelper = new DBHelper(this);

        // Fetch and update progress for each game mode
        updateProgress();

        // Set click listeners
        btnTimeTrial.setOnClickListener(view -> {
            MainActivity.stopMusic();
            startActivity(new Intent(PCAActivity.this, PCA_TT_GM_ACTIVITY.class));
        });

        btnSurvivalMode.setOnClickListener(view -> {
            MainActivity.stopMusic();
            startActivity(new Intent(PCAActivity.this, PCA_SM_GM_ACTIVITY.class));
        });

        btnFastMatch.setOnClickListener(view -> {
            MainActivity.stopMusic();
            startActivity(new Intent(PCAActivity.this, PCA_FM_GM_ACTIVITY.class));
        });
        // Back button click listener
        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(PCAActivity.this, CategoryActivity.class);
            startActivity(intent);
            finish(); // Close current activity
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainActivity.resumeMusic();
    }

    private void updateProgress() {
        // Fetch progress for all game modes
        int ttProgress = getProgressFromDB(dbHelper.getTTProgress(userId));
        int smProgress = getProgressFromDB(dbHelper.getSMProgress(userId));
        int fmProgress = getProgressFromDB(dbHelper.getFMProgress(userId));

        // Set "Done" if progress is 15/15
        progressText.setText(ttProgress == 15 ? "Done" : String.format("%d/15", ttProgress + 1));
        progressSurvival.setText(smProgress == 15 ? "Done" : String.format("%d/15", smProgress + 1));
        progressFastMatch.setText(fmProgress == 15 ? "Done" : String.format("%d/15", fmProgress + 1));
    }

    private int getProgressFromDB(Cursor cursor) {
        int progress = 0;
        if (cursor != null && cursor.moveToFirst()) {
            int index = cursor.getColumnIndex("question_index");
            if (index != -1) {
                progress = cursor.getInt(index);
            }
            cursor.close();
        }
        return progress;
    }
}
