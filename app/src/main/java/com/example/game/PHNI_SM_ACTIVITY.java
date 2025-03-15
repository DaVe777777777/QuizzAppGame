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

public class PHNI_SM_ACTIVITY extends AppCompatActivity {
    private TextView questionText, progressText, lifeText;
    private Button optionA, optionB, optionC, optionD, exitButton, restartButton;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;
    private int lives = 2; // Start with 2 lives
    private DBHelper dbHelper;
    private String userId = "1";
    private MediaPlayer quizMusic;
    private static PHNI_SM_ACTIVITY instance;// Example user ID


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
        Cursor cursor = dbHelper.getPHNISMProgress(userId);
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

        questionList.add(new Question("What was the reason for Lapu-Lapu's revolt against Magellan?", "He refused to pay tribute to Spain", "He wanted to become the governor of Cebu", "He wanted to join the Spaniards", "He had a conflict with Rajah Humabon", "He refused to pay tribute to Spain", "Lapu-Lapu, the chieftain of Mactan, refused to recognize Spanish authority and rejected paying tribute to Spain. This led to the Battle of Mactan in 1521, where he and his warriors defeated Magellan and his forces."));
        questionList.add(new Question("Who is considered the first Filipino hero?", "Jose Rizal", "Lapu-Lapu", "Andres Bonifacio", "Emilio Aguinaldo", "Lapu-Lapu", "Lapu-Lapu is considered the first Filipino hero because he led the resistance against Spanish colonization by defeating Ferdinand Magellan in the Battle of Mactan."));
        questionList.add(new Question("What was the main goal of the Propaganda Movement?", "To fight for Philippine independence from Spain", "To continue Spanish colonization", "To replace the Spanish government with an American government", "To request reforms and equal rights under Spain", "To request reforms and equal rights under Spain", "The Propaganda Movement, led by reformists like Jose Rizal, Marcelo H. del Pilar, and Graciano Lopez Jaena, aimed to achieve peaceful reforms, such as equal treatment of Filipinos and representation in the Spanish Cortes (parliament)."));
        questionList.add(new Question("Where did the name 'Philippines' originate?", "From the name of an ancient kingdom", "Derived from the name of King Philip II of Spain", "The name of an ancient god", "From the phrase 'Pearl of the Orient'", "Derived from the name of King Philip II of Spain", "The Philippines was named after King Philip II of Spain by the Spanish explorer Ruy López de Villalobos in the 16th century."));
        questionList.add(new Question("What was the role of 'GOMBURZA' in Philippine history?", "They were the first to revolt against Spain", "They were three priests executed for being accused of rebellion", "They were the first three presidents of the Philippines", "They were the first teachers under American rule", "They were three priests executed for being accused of rebellion", "Fathers Mariano Gomez, Jose Burgos, and Jacinto Zamora (GOMBURZA) were Filipino priests executed in 1872 for allegedly supporting the Cavite Mutiny. Their deaths inspired the Philippine revolution against Spanish rule."));
        questionList.add(new Question("What was the 'Polo y Servicio' system during Spanish rule?", "Forced labor of Filipinos for the Spanish government", "A tax imposed on wealthy Filipinos", "A voting system for Filipinos during the Spanish era", "A trade system between the Philippines and Spain", "Forced labor of Filipinos for the Spanish government", "Polo y Servicio was a system where Filipino males, aged 16-60, were forced to work for 40 days per year on Spanish government projects, such as building churches and roads."));
        questionList.add(new Question("Why was Andres Bonifacio removed from the Katipunan?", "He was accused of treason by Emilio Aguinaldo", "He chose to become the governor of Cavite", "He disagreed with the new rules of the Katipunan", "He lost the election against Emilio Aguinaldo", "He was accused of treason by Emilio Aguinaldo", "During the Tejeros Convention, Bonifacio lost the leadership of the revolution to Emilio Aguinaldo. Later, Aguinaldo’s supporters accused Bonifacio of treason, leading to his execution in 1897."));
        questionList.add(new Question("Which battle marked the first major victory of Filipino revolutionaries against the Spaniards?", "Battle of Mactan", "Battle of Manila", "Battle of Pinaglabanan", "Battle of Alapan", "Battle of Alapan", "The Battle of Alapan on May 28, 1898, was the first major victory of Filipino revolutionaries under Emilio Aguinaldo against the Spaniards. It led to the raising of the Philippine flag for the first time in Imus, Cavite."));
        questionList.add(new Question("What does the 'Cry of Pugad Lawin' mean?", "The declaration of Philippine independence", "The tearing of cedulas as a sign of revolution", "The first cry of the Katipunan in Biak-na-Bato", "The proclamation of the Malolos Constitution", "The tearing of cedulas as a sign of revolution", "The Cry of Pugad Lawin happened in 1896 when members of the Katipunan, led by Andres Bonifacio, tore their cedulas (residence tax certificates) as a symbol of rebellion against Spanish rule."));
        questionList.add(new Question("Who declared Philippine Independence in 1898?", "Andres Bonifacio", "Jose Rizal", "Emilio Aguinaldo", "Apolinario Mabini", "Emilio Aguinaldo", "Emilio Aguinaldo declared Philippine independence from Spain on June 12, 1898, in Kawit, Cavite, after leading the revolution against Spanish rule."));
        questionList.add(new Question("What was the reason for Diego Silang's revolt?", "He wanted Ilocos to be free from Spanish rule", "He wanted to replace Emilio Aguinaldo as president", "He wanted to join the Spanish army", "He wanted Ilocano to be the official language", "He wanted Ilocos to be free from Spanish rule", "Diego Silang led a revolt in 1762 against Spanish colonial rule in the Ilocos region, aiming to establish an independent Ilocano state."));
        questionList.add(new Question("Who was the leader of the Philippine Revolution against America?", "Antonio Luna", "Emilio Aguinaldo", "Andres Bonifacio", "Gregorio del Pilar", "Emilio Aguinaldo", "Emilio Aguinaldo initially fought against Spanish rule and later led Filipino forces in the Philippine-American War (1899-1902) to resist American colonization."));
        questionList.add(new Question("What was the term for forced tax payment during the Spanish era?", "Encomienda", "Cedula", "Bandala", "Tributo", "Tributo", "'Tributo' was a tax imposed on Filipino natives during the Spanish colonial period to fund the Spanish government and Catholic Church."));
        questionList.add(new Question("Who was the general who led the Battle of Tirad Pass?", "Antonio Luna", "Miguel Malvar", "Gregorio del Pilar", "Macario Sakay", "Gregorio del Pilar", "Gregorio del Pilar led Filipino forces in the Battle of Tirad Pass in 1899, where he and his men defended Emilio Aguinaldo’s retreat from American forces."));
        questionList.add(new Question("What do the three stars on the Philippine flag represent?", "Luzon, Visayas, Mindanao", "The three largest cities in the country", "The three leaders of the Katipunan", "The three major battles of the revolution", "Luzon, Visayas, Mindanao", "The three stars on the Philippine flag represent the three main island groups of the country: Luzon, Visayas, and Mindanao."));
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
        builder.setTitle(title)
                .setMessage(message)
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
        new AlertDialog.Builder(this)
                .setTitle("No More Lives!")
                .setMessage("You have no more lives left.")
                .setCancelable(false)
                .setPositiveButton("Try Again", (dialog, which) -> {
                    lives = 2;
                    updateLifeText();
                    showQuestion();
                })
                .setNegativeButton("Restart", (dialog, which) -> restartQuiz())
                .show();
    }

    private void showFinishScreen() {
        dbHelper.clearPHNISMProgress(userId);
        new AlertDialog.Builder(this)
                .setTitle("Quiz Finished!")
                .setMessage("You have completed the quiz.")
                .setCancelable(false)
                .setPositiveButton("Restart", (dialog, which) -> restartQuiz())
                .setNegativeButton("Exit", (dialog, which) -> exitToPHNIActivity())
                .show();
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
        dbHelper.savePHNISMProgress(userId, currentQuestionIndex, lives);
    }

    private void showExitConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Quiz")
                .setMessage("Do you want to exit? Your progress will be saved.")
                .setPositiveButton("Yes", (dialog, which) -> exitToPHNIActivity())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void exitToPHNIActivity() {
        saveUserProgress();
        stopQuizMusic();
        Intent intent = new Intent(this, PHNIActivity.class);
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
