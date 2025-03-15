package com.example.game;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.text.method.ScrollingMovementMethod;

public class PGNW_FM_ACTIVITY extends AppCompatActivity {
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
    private static PGNW_FM_ACTIVITY instance;

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
        Cursor cursor = dbHelper.getPGNWFMProgress(userId);
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

        questionList.add(new Question("Which province is known for its white sand beaches in Boracay?", "Aklan", "Cebu", "Palawan", "Bohol", "Aklan", "Boracay, located in Aklan, is famous for its powdery white sand beaches."));
        questionList.add(new Question("Which mountain is famous for its symmetrical shape?", "Mount Apo", "Mount Mayon", "Mount Banahaw", "Mount Pulag", "Mount Mayon", "Mount Mayon is known for its near-perfect cone shape, making it a popular symbol of beauty."));
        questionList.add(new Question("What is the capital of the Philippines?", "Quezon City", "Manila", "Cebu", "Davao", "Manila", "Manila is the capital city of the Philippines, located in the National Capital Region."));
        questionList.add(new Question("What is the national tree of the Philippines?", "Mahogany", "Narra", "Mango", "Pine", "Narra", "Narra is the national tree of the Philippines, symbolizing strength and resilience."));
        questionList.add(new Question("Which famous volcano erupted in 1991 in the Philippines?", "Mount Apo", "Mount Mayon", "Mount Pinatubo", "Mount Taal", "Mount Pinatubo", "Mount Pinatubo erupted in 1991, causing significant global climatic effects."));
        questionList.add(new Question("Which island is famous for its underground river?", "Palawan", "Cebu", "Bohol", "Leyte", "Palawan", "Palawan is home to the Puerto Princesa Underground River, a UNESCO World Heritage Site."));
        questionList.add(new Question("What is the longest river in the Philippines?", "Cagayan River", "Pasig River", "Agusan River", "Davao River", "Cagayan River", "The Cagayan River is the longest river in the Philippines, located in the northern part of Luzon."));
        questionList.add(new Question("Which mountain is the highest in the Philippines?", "Mount Apo", "Mount Pulag", "Mount Banahaw", "Mount Arayat", "Mount Apo", "Mount Apo, located in Mindanao, is the highest peak in the Philippines, standing at 2,954 meters."));
        questionList.add(new Question("Which of the following is a UNESCO World Heritage Site known for its ancient rice terraces?", "Banaue Rice Terraces", "Chocolate Hills", "Taal Volcano", "Hundred Islands", "Banaue Rice Terraces", "The Banaue Rice Terraces in Ifugao are recognized as a UNESCO World Heritage Site and are over 2,000 years old."));
        questionList.add(new Question("Which of the following is a famous beach destination in the Visayas known for its crystal-clear waters?", "Panglao Beach", "Boracay", "Samal Island", "Pagudpud", "Boracay", "Boracay is famous for its white sandy beaches and clear blue waters, attracting tourists from around the world."));
        questionList.add(new Question("Which famous natural attraction is located in Bohol?", "Mayon Volcano", "Chocolate Hills", "Taal Volcano", "Mount Pulag", "Chocolate Hills", "The Chocolate Hills, located in Bohol, are famous for their distinct dome-shaped mounds that turn brown in the dry season."));
        questionList.add(new Question("Which body of water lies between the Philippine islands of Luzon and Mindanao?", "Sulu Sea", "Visayan Sea", "Philippine Sea", "Celebes Sea", "Philippine Sea", "The Philippine Sea lies to the east of the Philippines, separating Luzon from Mindanao."));
        questionList.add(new Question("Which province is home to the famous Banaue Rice Terraces?", "Bohol", "Ifugao", "Palawan", "Cebu", "Ifugao", "The Banaue Rice Terraces are located in Ifugao province, known for their ancient agricultural heritage."));
        questionList.add(new Question("What is the name of the protected marine park located in Palawan?", "Tubbataha Reefs", "Apo Reef", "Honda Bay", "Donsol Bay", "Tubbataha Reefs", "Tubbataha Reefs Natural Park is a UNESCO World Heritage Site known for its rich marine biodiversity and clear waters."));
        questionList.add(new Question("Which of the following is a famous spot in Davao known for its scenic view of Mount Apo?", "Eden Nature Park", "Banaue Rice Terraces", "Sirao Flower Garden", "Davao Crocodile Park", "Eden Nature Park", "Eden Nature Park offers a panoramic view of Mount Apo, the highest mountain in the Philippines."));
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
        builder.setTitle("Time's Up!")
                .setMessage("You ran out of time!")
                .setCancelable(false)
                .setPositiveButton("Restart", (dialog, which) -> restartQuiz())
                .setNegativeButton("Try Again", (dialog, which) -> {
                    timeRemaining = timeLimit; // Reset timer to 20 seconds
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
        builder.setTitle(title)
                .setMessage(message)
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
        dbHelper.clearPGNWFMProgress(userId);
        new AlertDialog.Builder(this)
                .setTitle("Quiz Finished!")
                .setMessage("You have completed the quiz.")
                .setCancelable(false)
                .setPositiveButton("Restart", (dialog, which) -> restartQuiz())
                .setNegativeButton("Exit", (dialog, which) -> exitToPGNWActivity())
                .show();
    }

    private void restartQuiz() {
        timerHandler.removeCallbacks(timerRunnable); // Stop any running timer
        currentQuestionIndex = 0;
        timeRemaining = timeLimit; // Reset the timer
        loadQuestions(); // Reload questions
        startQuiz(); // Restart the quiz
    }



    private void saveUserProgress() {
        dbHelper.savePGNWFMProgress(userId, currentQuestionIndex, timeRemaining);  // Save current timeRemaining
    }


    private void showExitConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Quiz")
                .setMessage("Do you want to exit? Your progress will be saved.")
                .setPositiveButton("Yes", (dialog, which) -> exitToPGNWActivity())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
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