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

public class PLD_SM_ACTIVITY extends AppCompatActivity {
    private TextView questionText, progressText, lifeText;
    private Button optionA, optionB, optionC, optionD, exitButton, restartButton;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;
    private int lives = 2; // Start with 2 lives
    private DBHelper dbHelper;
    private String userId = "1";
    private MediaPlayer quizMusic;
    private static PLD_SM_ACTIVITY instance;


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
        Cursor cursor = dbHelper.getPLDSMProgress(userId);
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

        questionList.add(new Question("What makes Chavacano unique among Philippine languages?", "It has no verbs", "It is purely indigenous", "It is a creole language", "It has no vowels", "It is a creole language", "Chavacano is a Spanish-based creole, meaning it combines Spanish with indigenous Philippine languages."));
        questionList.add(new Question("What is the official language of the Autonomous Region in Muslim Mindanao (ARMM)?", "Tausug", "Maranao", "Maguindanaon", "All of the above", "All of the above", "ARMM recognizes multiple indigenous languages."));
        questionList.add(new Question("Which Philippine language has the closest relation to Malagasy (spoken in Madagascar)?", "Tagalog", "Cebuano", "Ivatan", "Sama-Bajau", "Sama-Bajau", "The Sama-Bajau language shares Austronesian roots with Malagasy."));
        questionList.add(new Question("The phrase 'Naay baligya?' in Cebuano means what?", "How are you?", "Do you have something for sale?", "What time is it?", "Where is the market?", "Do you have something for sale?", "'Naay' means 'there is' or 'do you have,' and 'baligya' means 'for sale' in Cebuano."));
        questionList.add(new Question("What does 'Ambot' mean in Cebuano?", "I don’t know", "I'm fine", "I'm sorry", "Let's go", "I don’t know", "'Ambot' is a common Cebuano expression meaning 'I don't know.'"));
        questionList.add(new Question("The Cordillera region is home to which major language?", "Kankanaey", "Bikolano", "Tausug", "Maguindanaon", "Kankanaey", "Kankanaey is a major language spoken in the Cordillera region."));
        questionList.add(new Question("What is 'rice' in Hiligaynon?", "Bugas", "Kan-on", "Bigas", "Humay", "Bugas", "'Bugas' is raw rice in Hiligaynon, while 'kan-on' refers to cooked rice."));
        questionList.add(new Question("Which language is spoken by the Aeta tribes?", "Sambal", "Pangasinense", "Kankanaey", "Tagbanua", "Sambal", "Many Aetas speak Sambal, a language of Zambales."));
        questionList.add(new Question("Which of these languages has its own ancient writing system still in use?", "Cebuano", "Kapampangan", "Tagalog", "Hanunuo", "Hanunuo", "Hanunuo, spoken by the Mangyan people, still uses a script derived from ancient Baybayin."));
        questionList.add(new Question("Which of these languages is closely related to Bahasa Indonesia?", "Yakan", "Hiligaynon", "Pangasinan", "Waray", "Yakan", "Yakan, spoken in Basilan, is closely related to Sama-Bajau and Bahasa Indonesia."));
        questionList.add(new Question("Which of the following is a sentence in correct Cebuano grammar?", "Ako ikaw palangga", "Palangga nako ikaw", "Ikaw palangga nako", "Palangga ako ikaw", "Palangga nako ikaw", "Cebuano follows a Verb-Subject-Object (VSO) order."));
        questionList.add(new Question("What is the term for a language used for communication between people who speak different native languages?", "Dialect", "Pidgin", "Creole", "Lingua franca", "Lingua franca", "A lingua franca is a bridge language used for communication, like Filipino in the Philippines."));
        questionList.add(new Question("Which region is known for speaking Ilocano?", "Bicol Region", "Northern Luzon", "Mindoro", "Central Visayas", "Northern Luzon", "Ilocano is widely spoken in Ilocos, La Union, and parts of Cagayan Valley."));
        questionList.add(new Question("What is the term for the mixing of two languages in speech?", "Code-switching", "Morphology", "Dialectology", "Syntax", "Code-switching", "Code-switching happens when speakers alternate between two languages in a conversation."));
        questionList.add(new Question("What language family do Philippine languages belong to?", "Sino-Tibetan", "Indo-European", "Austronesian", "Dravidian", "Austronesian", "Philippine languages are part of the Austronesian family."));
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
        dbHelper.clearPLDSMProgress(userId);

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
                .setNegativeButton("Exit", (dialog, which) -> exitToPLDActivity());

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
        dbHelper.savePLDSMProgress(userId, currentQuestionIndex, lives);
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
                .setPositiveButton("Yes", (dialog, which) -> exitToPLDActivity())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void exitToPLDActivity() {
        saveUserProgress();
        stopQuizMusic();
        Intent intent = new Intent(this, PLDActivity.class);
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

