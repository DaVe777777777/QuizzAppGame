package com.example.game;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.text.method.ScrollingMovementMethod;

public class PCA_FM_GM_ACTIVITY extends AppCompatActivity {
    private TextView questionText, progressText, timerText;
    private Button optionA, optionB, optionC, optionD, exitButton, restartButton;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;
    private DBHelper dbHelper;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private int timeLimit = 20; // 20 seconds per question
    private int timeRemaining;
    private String userId = "1";
    private MediaPlayer quizMusic;
    private static PCA_FM_GM_ACTIVITY instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pca_fm_gm);

        // Initialize UI elements
        questionText = findViewById(R.id.questionText);
        progressText = findViewById(R.id.progressText);
        timerText = findViewById(R.id.timerText);
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
        quizMusic = MediaPlayer.create(this, R.raw.fast);
        quizMusic.setLooping(true);
        quizMusic.setVolume(isMuted ? 0.0f : 1.0f, isMuted ? 0.0f : 1.0f); // Apply mute state
        quizMusic.start();
    }

    private void loadUserProgress() {
        Cursor cursor = dbHelper.getFMProgress(userId);
        if (cursor != null && cursor.moveToFirst()) {
            currentQuestionIndex = cursor.getInt(cursor.getColumnIndexOrThrow("question_index"));
            timeRemaining = cursor.getInt(cursor.getColumnIndexOrThrow("time_remaining"));
            cursor.close();
        } else {
            currentQuestionIndex = 0;
            timeRemaining = timeLimit;
        }

        showQuestion();
    }


    private void loadQuestions() {
        questionList = new ArrayList<>();

        questionList.add(new Question("What is the traditional Filipino board game similar to mancala?", "Dama", "Sungka", "Teks", "Pitik", "Sungka", "A game played on a wooden board with shells or stones, similar to mancala."));
        questionList.add(new Question("What is the national bird of the Philippines?", "Maya", "Philippine Eagle", "Tarsier", "Kingfisher", "Philippine Eagle", "One of the world's largest eagles, an endangered species native to the country."));
        questionList.add(new Question("Which festival in Baguio is known for its flower-covered floats?", "Panagbenga", "Pahiyas", "Pintados", "Kadayawan", "Panagbenga", "A flower festival held every February, celebrating Baguio's blooming season."));
        questionList.add(new Question("Which dance mimics the movements of a duck?", "Maglalatik", "Itik-itik", "Singkil", "Tinikling", "Itik-itik", "A folk dance inspired by a duck’s graceful and playful movements."));
        questionList.add(new Question("What is the national tree of the Philippines?", "Narra", "Molave", "Acacia", "Mahogany", "Narra", "Known for its strong wood, it’s the official national tree symbolizing strength and resilience."));
        questionList.add(new Question("Where is the famous 'Chocolate Hills' located?", "Bohol", "Palawan", "Cebu", "Davao", "Bohol", "The Chocolate Hills in Bohol are famous for their unique, dome-shaped mounds that turn brown in the dry season."));
        questionList.add(new Question("Which material is commonly used in T’nalak weaving?", "Piña", "Cotton", "Abaca", "Silk", "Abaca", "T’nalak weaving, a tradition of the T’boli people, uses abaca, a strong natural fiber."));
        questionList.add(new Question("What is the Philippine folk song that tells a story of lost love?", "Bahay Kubo", "Paruparong Bukid", "Leron-Leron Sinta", "Sa Ugoy ng Duyan", "Sa Ugoy ng Duyan", "A heartfelt lullaby that expresses the theme of lost love and longing."));
        questionList.add(new Question("What is the traditional Filipino house on stilts called?", "Bahay na Bato", "Bahay Kubo", "Kamalig", "Balay", "Bahay Kubo", "A traditional rural house made of bamboo and nipa palm, elevated on stilts to protect from floods and ensure ventilation."));
        questionList.add(new Question("What is the Filipino dish made of fermented fish or shrimp paste, commonly used as a condiment?", "Bagoong", "Adobo", "Sinigang", "Kare-Kare", "Bagoong", "A fermented fish or shrimp paste, often used to enhance the flavor of Filipino dishes."));
        questionList.add(new Question("What Filipino instrument is a bamboo percussion instrument played by striking with hands or sticks?", "Kulintang", "Gongs", "Timpani", "Agung", "Kulintang", "A set of gongs, typically played in a rhythm, native to the southern Philippines."));
        questionList.add(new Question("What is the traditional Filipino hat made from woven palm leaves?", "Salakot", "Sombrero", "Kamisa", "Sombrero de Paja", "Salakot", "A traditional wide-brimmed hat made from woven bamboo or palm leaves, used for sun protection."));
        questionList.add(new Question("Which Filipino holiday honors the country’s heroes?", "Independence Day", "National Heroes Day", "Bonifacio Day", "EDSA Day", "National Heroes Day", "Celebrated to honor Filipino heroes who fought for the country’s freedom."));
        questionList.add(new Question("Which Filipino province is known for its beautiful white-sand beaches in Boracay?", "Palawan", "Cebu", "Aklan", "Bohol", "Aklan", "Aklan is the province where Boracay, known for its powdery white-sand beaches, is located."));
        questionList.add(new Question("Which Filipino cultural heritage is celebrated in the Kadayawan Festival?", "Harvest Festival", "Flower Festival", "Cultural and Artistic Expressions", "Historical Significance", "Cultural and Artistic Expressions", "The Kadayawan Festival is celebrated in Davao City to honor the indigenous culture, history, and the natural bounties of the region."));
    }

    private void startQuiz() {
        showQuestion();
    }

    private void startTimer() {
        timerHandler.removeCallbacks(timerRunnable); // Ensure no duplicate timers

        timerText.setText(String.format(Locale.getDefault(), "Time Left: %02d sec", timeRemaining));

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (timeRemaining > 0) {
                    timeRemaining--;
                    timerText.setText(String.format(Locale.getDefault(), "Time Left: %02d sec", timeRemaining));
                    timerHandler.postDelayed(this, 1000);
                } else {
                    showTimeUpDialog();
                }
            }
        };
        timerHandler.postDelayed(timerRunnable, 1000);
    }



    private void showTimeUpDialog() {
        timerHandler.removeCallbacks(timerRunnable);

        if (isFinishing()) return; // Prevent crash if activity is not valid

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create a custom TextView for the title
        TextView titleView = new TextView(this);
        titleView.setText("Time's Up!");
        titleView.setTextSize(30); // Bigger title size
        titleView.setTextColor(getResources().getColor(android.R.color.holo_red_dark)); // Red color for urgency
        titleView.setPadding(50, 40, 50, 20);
        titleView.setGravity(View.TEXT_ALIGNMENT_CENTER); // Center align

        // Create a custom TextView for the message
        TextView messageView = new TextView(this);
        messageView.setText("You ran out of time!");
        messageView.setTextSize(22); // Bigger message size
        messageView.setTextColor(getResources().getColor(android.R.color.black));
        messageView.setPadding(50, 20, 50, 20);
        messageView.setGravity(View.TEXT_ALIGNMENT_CENTER); // Center align

        builder.setCustomTitle(titleView) // Use custom title
                .setView(messageView) // Set custom message
                .setCancelable(false)
                .setPositiveButton("Restart", (dialog, which) -> restartQuiz())
                .setNegativeButton("Try Again", (dialog, which) -> {
                    timeRemaining = timeLimit; // Reset timer
                    startTimer(); // Restart timer
                });

        AlertDialog dialog = builder.create();
        if (!isFinishing()) {
            dialog.show();
        }
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
        startTimer();
    }

    public void checkAnswer(View view) {
        timerHandler.removeCallbacks(timerRunnable);
        Button selectedButton = (Button) view;
        String selectedAnswer = selectedButton.getText().toString();
        Question currentQuestion = questionList.get(currentQuestionIndex);

        if (selectedAnswer.equals(currentQuestion.getCorrectAnswer())) {
            timeRemaining = timeLimit; // Reset timer to 20 seconds
            showAnswerPopup("Correct!", currentQuestion.getExplanation(), true);
        } else {
            showAnswerPopup("Wrong Answer!", "Try Again!", false);
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
            builder.setPositiveButton("Try Again", (dialog, which) -> {
                // Continue with the remaining time
                startTimer();
            });
        }

        AlertDialog dialog = builder.create();
        if (!isFinishing()) {
            dialog.show();
        }
    }



    private void showNextQuestion() {
        timerHandler.removeCallbacks(timerRunnable); // Stop old timer
        currentQuestionIndex++;
        if (currentQuestionIndex < questionList.size()) {
            showQuestion();
        } else {
            showFinishScreen();
        }
    }



    private void showFinishScreen() {
        timerHandler.removeCallbacks(timerRunnable);
        dbHelper.clearFMProgress(userId);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create a custom TextView for the title
        TextView titleView = new TextView(this);
        titleView.setText("Quiz Finished!");
        titleView.setTextSize(30); // Bigger title size
        titleView.setTextColor(getResources().getColor(android.R.color.holo_blue_dark)); // Blue for emphasis
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
                .setNegativeButton("Exit", (dialog, which) -> exitToPCAActivity());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void restartQuiz() {
        timerHandler.removeCallbacks(timerRunnable); // Stop any running timer
        currentQuestionIndex = 0;
        timeRemaining = timeLimit; // Reset the timer
        loadQuestions(); // Reload questions
        startQuiz(); // Restart the quiz
    }



    private void saveUserProgress() {
        dbHelper.saveFMProgress(userId, currentQuestionIndex, timeRemaining);  // Save current timeRemaining
    }


    private void showExitConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create a custom TextView for the title
        TextView titleView = new TextView(this);
        titleView.setText("Exit Quiz");
        titleView.setTextSize(30); // Bigger title size
        titleView.setTextColor(getResources().getColor(android.R.color.holo_red_dark)); // Red for urgency
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
                .setPositiveButton("Yes", (dialog, which) -> exitToPCAActivity())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void exitToPCAActivity() {
        saveUserProgress();
        stopQuizMusic();
        Intent intent = new Intent(this, PCAActivity.class);
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