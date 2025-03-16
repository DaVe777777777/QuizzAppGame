package com.example.game;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.text.method.ScrollingMovementMethod;

public class PGNW_SM_ACTIVITY extends AppCompatActivity {
    private TextView questionText, progressText, lifeText;
    private Button optionA, optionB, optionC, optionD, exitButton, restartButton;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;
    private int lives = 2; // Start with 2 lives
    private DBHelper dbHelper;
    private String userId = "1";
    private MediaPlayer quizMusic;
    private static PGNW_SM_ACTIVITY instance;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pca_sm_gm);

        // Initialize UI elements
        questionText = findViewById(R.id.questionText);
        progressText = findViewById(R.id.progressText);
        lifeText = findViewById(R.id.lifeText);
        optionA = findViewById(R.id.optionA);
        optionB = findViewById(R.id.optionB);
        optionC = findViewById(R.id.optionC);
        optionD = findViewById(R.id.optionD);
        exitButton = findViewById(R.id.exitButton);
        restartButton = findViewById(R.id.restartButton);

        questionText.setMovementMethod(new ScrollingMovementMethod());

        // Set button click listeners
        optionA.setOnClickListener(this::checkAnswer);
        optionB.setOnClickListener(this::checkAnswer);
        optionC.setOnClickListener(this::checkAnswer);
        optionD.setOnClickListener(this::checkAnswer);
        exitButton.setOnClickListener(v -> showExitConfirmation());
        restartButton.setOnClickListener(v -> restartQuiz());

        dbHelper = new DBHelper(this);
        loadQuestions();
        loadUserProgress();
        startQuiz();

        instance = this;

        // Load mute state
        SharedPreferences preferences = getSharedPreferences("GamePrefs", MODE_PRIVATE);
        boolean isMuted = preferences.getBoolean("MUTE_STATE", false);

        // Initialize and start quiz background music
        quizMusic = MediaPlayer.create(this, R.raw.survival);
        quizMusic.setLooping(true);
        quizMusic.setVolume(isMuted ? 0.0f : 1.0f, isMuted ? 0.0f : 1.0f); // Apply mute state
        quizMusic.start();
    }

    private void loadUserProgress() {
        Cursor cursor = dbHelper.getPGNWSMProgress(userId);
        if (cursor != null && cursor.moveToFirst()) {
            currentQuestionIndex = cursor.getInt(cursor.getColumnIndexOrThrow("question_index"));
            lives = cursor.getInt(cursor.getColumnIndexOrThrow("lives"));
            cursor.close();
        } else {
            currentQuestionIndex = 0;
            lives = 2; // Reset to 2 lives
        }

        updateLifeText();
        showQuestion();
    }

    private void loadQuestions() {
        questionList = new ArrayList<>();

        questionList.add(new Question("What is the national flower of the Philippines?", "Sampaguita", "Gumamela", "Rafflesia", "Waling-Waling", "Sampaguita", "The Sampaguita is the national flower of the Philippines, symbolizing purity and simplicity."));
        questionList.add(new Question("Which body of water lies to the west of the Philippines?", "Philippine Sea", "South China Sea", "Celebes Sea", "Sulu Sea", "South China Sea", "The South China Sea lies to the west of the Philippines."));
        questionList.add(new Question("Which province is home to the famous Taal Volcano?", "Batangas", "Cavite", "Quezon", "Laguna", "Batangas", "Taal Volcano is located in Batangas, known for its picturesque crater lake."));
        questionList.add(new Question("What is the highest peak in the Philippines?", "Mount Pulag", "Mount Apo", "Mount Banahaw", "Mount Mayon", "Mount Apo", "Mount Apo is the highest peak in the Philippines, standing at 2,954 meters above sea level."));
        questionList.add(new Question("Which river is known for being the longest river in the Visayas region?", "Cagayan River", "Agusan River", "Panay River", "Loboc River", "Agusan River", "Agusan River is the longest river in the Visayas region, flowing through Mindanao."));
        questionList.add(new Question("Which famous island is located in the province of Palawan and known for its stunning limestone cliffs and crystal-clear waters?", "Boracay", "Bohol", "El Nido", "Cebu", "El Nido", "El Nido is a popular tourist destination in Palawan known for its beautiful limestone cliffs and beaches."));
        questionList.add(new Question("What is the name of the Philippine mountain that erupted in 1991?", "Mount Banahaw", "Mount Mayon", "Mount Pinatubo", "Mount Apo", "Mount Pinatubo", "Mount Pinatubo erupted in 1991, causing massive destruction and environmental changes."));
        questionList.add(new Question("Which of the following is the largest body of water surrounding the Philippines?", "Celebes Sea", "Philippine Sea", "Sulu Sea", "South China Sea", "Philippine Sea", "The Philippine Sea is the largest body of water surrounding the Philippines."));
        questionList.add(new Question("Where can you find the famous 'Chocolate Hills'?", "Palawan", "Bohol", "Cebu", "Leyte", "Bohol", "The Chocolate Hills are a natural formation in Bohol, famous for their dome-shaped mounds."));
        questionList.add(new Question("What is the name of the underwater tunnel that connects the Philippines to Taiwan?", "Bashi Channel", "Sibuyan Sea", "Mindanao Sea", "Balintang Channel", "Bashi Channel", "The Bashi Channel connects the northern part of the Philippines to Taiwan."));
        questionList.add(new Question("Which city in the Philippines is known for its historical significance and old Spanish architecture?", "Manila", "Cebu", "Vigan", "Davao", "Vigan", "Vigan, located in Ilocos Sur, is known for its well-preserved Spanish colonial architecture."));
        questionList.add(new Question("Which Philippine province is known for the 'Hundred Islands'?", "Pangasinan", "Bohol", "Palawan", "Batangas", "Pangasinan", "Pangasinan is home to the Hundred Islands, a popular tourist destination."));
        questionList.add(new Question("Which city in the Philippines is famous for its colorful Lantern Festival?", "Davao", "Pampanga", "Manila", "Cebu", "Pampanga", "Pampanga is known for its Giant Lantern Festival held annually in San Fernando."));
        questionList.add(new Question("Which Philippine island is known for its rich biodiversity and endemic species of plants and animals?", "Mindanao", "Palawan", "Luzon", "Cebu", "Palawan", "Palawan is famous for its diverse ecosystems, including rare species of animals and plants."));
        questionList.add(new Question("Which Philippine region is known for its volcanic activity and beautiful beaches?", "Visayas", "Luzon", "Mindanao", "Bicol", "Bicol", "Bicol is known for its active volcanoes, particularly Mayon Volcano, and its stunning beaches."));
    }

    private void startQuiz() {
        showQuestion();
    }

    private void showQuestion() {
        if (currentQuestionIndex >= questionList.size()) {
            showFinishScreen();
            return;
        }
        Question currentQuestion = questionList.get(currentQuestionIndex);
        questionText.setText(currentQuestion.getQuestion());
        optionA.setText(currentQuestion.getOptionA());
        optionB.setText(currentQuestion.getOptionB());
        optionC.setText(currentQuestion.getOptionC());
        optionD.setText(currentQuestion.getOptionD());
        progressText.setText(String.format(Locale.getDefault(), "Question %d/%d", currentQuestionIndex + 1, questionList.size()));
    }

    public void checkAnswer(View view) {
        Button selectedButton = (Button) view;
        String selectedAnswer = selectedButton.getText().toString();
        Question currentQuestion = questionList.get(currentQuestionIndex);

        if (selectedAnswer.equals(currentQuestion.getCorrectAnswer())) {
            lives = 2; // Restore lives to 2 on correct answer
            updateLifeText();
            showAnswerPopup("Correct!", currentQuestion.getExplanation(), true);
        } else {
            lives--;
            updateLifeText();
            if (lives <= 0) {
                showNoMoreLivesDialog();
            } else {
                showAnswerPopup("Wrong Answer!", "Try Again!", false);
            }
        }
    }

    private void showAnswerPopup(String title, String message, boolean isCorrect) {
        if (isFinishing()) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create a custom TextView for the title (Correct/Wrong Answer)
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(30); // Bigger title size
        titleView.setTextColor(isCorrect ? getResources().getColor(android.R.color.holo_green_dark)
                : getResources().getColor(android.R.color.holo_red_dark)); // Green for correct, red for wrong
        titleView.setPadding(50, 40, 50, 20);
        titleView.setGravity(View.TEXT_ALIGNMENT_CENTER); // Center align

        // Create a custom TextView for the message (Explanation)
        TextView messageView = new TextView(this);
        messageView.setText(message);
        messageView.setTextSize(22); // Bigger message size
        messageView.setTextColor(getResources().getColor(android.R.color.black));
        messageView.setPadding(50, 20, 50, 20);
        messageView.setGravity(View.TEXT_ALIGNMENT_CENTER); // Center align

        builder.setCustomTitle(titleView) // Use custom title
                .setView(messageView) // Set custom message
                .setCancelable(false);

        if (isCorrect) {
            builder.setPositiveButton("Next", (dialog, which) -> {
                saveUserProgress();
                showNextQuestion();
            });
        } else {
            builder.setPositiveButton("Try Again", (dialog, which) -> {});
        }

        AlertDialog dialog = builder.create();
        if (!isFinishing()) {
            dialog.show();
        }
    }

    private void showNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questionList.size()) {
            showQuestion();
        } else {
            showFinishScreen();
        }
    }

    private void showNoMoreLivesDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create a custom TextView for the title
        TextView titleView = new TextView(this);
        titleView.setText("No More Lives!");
        titleView.setTextSize(30); // Bigger title size
        titleView.setTextColor(getResources().getColor(android.R.color.holo_red_dark)); // Red color for warning
        titleView.setPadding(50, 40, 50, 20);
        titleView.setGravity(View.TEXT_ALIGNMENT_CENTER); // Center align

        // Create a custom TextView for the message
        TextView messageView = new TextView(this);
        messageView.setText("You have no more lives left.");
        messageView.setTextSize(22); // Bigger message size
        messageView.setTextColor(getResources().getColor(android.R.color.black));
        messageView.setPadding(50, 20, 50, 20);
        messageView.setGravity(View.TEXT_ALIGNMENT_CENTER); // Center align

        builder.setCustomTitle(titleView) // Use custom title
                .setView(messageView) // Set custom message
                .setCancelable(false)
                .setPositiveButton("Try Again", (dialog, which) -> {
                    lives = 2;
                    updateLifeText();
                    showQuestion();
                })
                .setNegativeButton("Restart", (dialog, which) -> restartQuiz());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showFinishScreen() {
        dbHelper.clearPGNWSMProgress(userId);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create a custom TextView for the title
        TextView titleView = new TextView(this);
        titleView.setText("Quiz Finished!");
        titleView.setTextSize(30); // Bigger title size
        titleView.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        titleView.setPadding(50, 40, 50, 20);
        titleView.setGravity(View.TEXT_ALIGNMENT_CENTER); // Center align

        // Create a custom TextView for the message
        TextView messageView = new TextView(this);
        messageView.setText("You have completed the quiz.");
        messageView.setTextSize(22); // Bigger message size
        messageView.setTextColor(getResources().getColor(android.R.color.black));
        messageView.setPadding(50, 20, 50, 20);
        messageView.setGravity(View.TEXT_ALIGNMENT_CENTER); // Center align

        builder.setCustomTitle(titleView) // Use custom title
                .setView(messageView) // Set custom message
                .setCancelable(false)
                .setPositiveButton("Restart", (dialog, which) -> restartQuiz())
                .setNegativeButton("Exit", (dialog, which) -> exitToPGNWActivity());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void restartQuiz() {
        currentQuestionIndex = 0; // Reset question index
        lives = 2; // Reset lives
        updateLifeText();
        saveUserProgress(); // Save reset progress
        showQuestion(); // Show first question
    }

    private void updateLifeText() {
        lifeText.setText(String.format(Locale.getDefault(), "Lives: %d", lives));
    }

    private void saveUserProgress() {
        dbHelper.savePGNWSMProgress(userId, currentQuestionIndex, lives);
    }


    private void showExitConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create a custom TextView for the title
        TextView titleView = new TextView(this);
        titleView.setText("Exit Quiz");
        titleView.setTextSize(30); // Bigger title size
        titleView.setTextColor(getResources().getColor(android.R.color.holo_red_dark)); // Red color for warning
        titleView.setPadding(50, 40, 50, 20);
        titleView.setGravity(View.TEXT_ALIGNMENT_CENTER); // Center align

        // Create a custom TextView for the message
        TextView messageView = new TextView(this);
        messageView.setText("Do you want to exit? Your progress will be saved.");
        messageView.setTextSize(22); // Bigger message size
        messageView.setTextColor(getResources().getColor(android.R.color.black));
        messageView.setPadding(50, 20, 50, 20);
        messageView.setGravity(View.TEXT_ALIGNMENT_CENTER); // Center align

        builder.setCustomTitle(titleView) // Use custom title
                .setView(messageView) // Set custom message
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> exitToPGNWActivity())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void exitToPGNWActivity() {
        saveUserProgress();
        stopQuizMusic();
        Intent intent = new Intent(this, PGNWActivity.class);
        startActivity(intent);
        finish();
    }

    private void stopQuizMusic() {
        if (quizMusic != null) {
            quizMusic.stop();
            quizMusic.release();
            quizMusic = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopQuizMusic();
        saveUserProgress();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopQuizMusic();
        MainActivity.resumeMusic();
    }

    public static void setQuizMusicVolume(float volume) {
        if (instance != null && instance.quizMusic != null) {
            instance.quizMusic.setVolume(volume, volume);
        }
    }
}