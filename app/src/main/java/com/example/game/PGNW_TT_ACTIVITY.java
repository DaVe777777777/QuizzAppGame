package com.example.game;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

public class PGNW_TT_ACTIVITY extends AppCompatActivity {
    private TextView questionText, progressText, timerText;
    private Button optionA, optionB, optionC, optionD, exitButton;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;
    private DBHelper dbHelper;
    private long startTime, elapsedTime;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private String userId = "1";
    private MediaPlayer quizMusic;
    private static PGNW_TT_ACTIVITY instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pca_tt_gm);



        // Initialize UI Elements
        questionText = findViewById(R.id.questionText);
        progressText = findViewById(R.id.progressText);
        timerText = findViewById(R.id.timerText);
        optionA = findViewById(R.id.optionA);
        optionB = findViewById(R.id.optionB);
        optionC = findViewById(R.id.optionC);
        optionD = findViewById(R.id.optionD);
        exitButton = findViewById(R.id.exitButton);
        Button restartButton = findViewById(R.id.restartButton);

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
        quizMusic = MediaPlayer.create(this, R.raw.quiz_music);
        quizMusic.setLooping(true);
        quizMusic.setVolume(isMuted ? 0.0f : 1.0f, isMuted ? 0.0f : 1.0f); // Apply mute state
        quizMusic.start();
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
        saveUserProgress(); // Ensure progress is saved before exiting
        stopQuizMusic();
        Intent intent = new Intent(this, PGNWActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadQuestions() {
        questionList = new ArrayList<>();

        questionList.add(new Question("What is the capital city of the Philippines?", "Davao", "Quezon City", "Manila", "Cebu", "Manila", "Manila is the capital city of the Philippines, located along the western shore of Luzon."));
        questionList.add(new Question("Which island is the largest in the Philippines?", "Luzon", "Mindanao", "Negros", "Palawan", "Luzon", "Luzon is the largest island in the Philippines and also the most populous."));
        questionList.add(new Question("Which Philippine region is known as the 'Rice Bowl of the Philippines'?", "Ilocos", "Central Luzon", "Bicol", "Eastern Visayas", "Central Luzon", "Central Luzon is known as the 'Rice Bowl' due to its vast rice fields and agricultural production."));
        questionList.add(new Question("What is the name of the longest river in the Philippines?", "Cagayan River", "Agusan River", "Pasig River", "Pampanga River", "Cagayan River", "The Cagayan River is the longest river in the Philippines, flowing in the northeastern part of Luzon."));
        questionList.add(new Question("Which mountain in the Philippines is known as the 'Roof of Luzon'?", "Mount Apo", "Mount Pulag", "Mount Mayon", "Mount Banahaw", "Mount Pulag", "Mount Pulag is known as the 'Roof of Luzon' because it is the third highest mountain in the country."));
        questionList.add(new Question("What body of water separates the Philippines from Taiwan?", "Sulu Sea", "Celebes Sea", "Philippine Sea", "Bashi Channel", "Bashi Channel", "The Bashi Channel separates the Philippines (specifically Batanes) from Taiwan."));
        questionList.add(new Question("Which island is known for its famous white sand beaches, particularly in Boracay?", "Palawan", "Cebu", "Bohol", "Aklan", "Aklan", "Aklan is the province where Boracay Island, known for its white sand beaches, is located."));
        questionList.add(new Question("Which island group is located to the south of the Philippines?", "Visayas", "Mindanao", "Luzon", "Mindoro", "Mindanao", "Mindanao is the second largest island group in the southern part of the Philippines."));
        questionList.add(new Question("Which body of water lies between the Philippines and Vietnam?", "Bohol Sea", "South China Sea", "Philippine Sea", "Sulu Sea", "South China Sea", "The South China Sea is the body of water between the Philippines and Vietnam."));
        questionList.add(new Question("What is the smallest province in the Philippines?", "Batanes", "Guimaras", "Siquijor", "Camiguin", "Batanes", "Batanes is the smallest province in terms of land area, located in the northernmost part of the Philippines."));
        questionList.add(new Question("Which region in the Philippines is famous for its rice terraces, known as the 'Eighth Wonder of the World'?", "Cordillera", "Ilocos", "Bicol", "Visayas", "Cordillera", "The rice terraces of the Cordillera region, particularly the Banaue Rice Terraces, are famous for their historical and cultural significance."));
        questionList.add(new Question("Which river is the main source of irrigation for the rice fields in Central Luzon?", "Cagayan River", "Agusan River", "Pampanga River", "Pasig River", "Pampanga River", "The Pampanga River serves as the main source of irrigation in Central Luzon, contributing to the region's agricultural economy."));
        questionList.add(new Question("Which Philippine province is known as the 'Land of the Promise'?", "Davao del Sur", "Cebu", "Palawan", "Camarines Sur", "Davao del Sur", "Davao del Sur is known as the 'Land of the Promise' due to its natural resources and potential for growth."));
        questionList.add(new Question("What is the name of the volcano that erupted in 1991 and is located in Zambales?", "Mount Apo", "Mount Pinatubo", "Mayon Volcano", "Taal Volcano", "Mount Pinatubo", "Mount Pinatubo erupted in 1991, creating one of the largest eruptions of the 20th century."));
        questionList.add(new Question("Which island group is known for its stunning underground river in Palawan?", "Visayas", "Mindanao", "Palawan", "Luzon", "Palawan", "Palawan is home to the Puerto Princesa Underground River, a UNESCO World Heritage Site."));
    }


    private void startQuiz() {
        startTime = System.currentTimeMillis() - elapsedTime;
        startTimer();
        showQuestion();
    }

    private void startTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                elapsedTime = System.currentTimeMillis() - startTime;
                int seconds = (int) (elapsedTime / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;

                timerText.setText(String.format(Locale.getDefault(), "Time: %02d:%02d", minutes, seconds));
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.postDelayed(timerRunnable, 0);
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
            showAnswerPopup("Correct!", currentQuestion.getExplanation(), true);
        } else {
            showAnswerPopup("Wrong Answer!", "Try Again!", false);
        }
    }

    private void showAnswerPopup(String title, String message, boolean isCorrect) {
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
        dialog.show();
    }

    private void showNextQuestion() {
        currentQuestionIndex++;
        if (currentQuestionIndex < questionList.size()) {
            showQuestion();
        } else {
            showFinishScreen();
        }
    }

    private void showFinishScreen() {
        timerHandler.removeCallbacks(timerRunnable);
        dbHelper.clearPGNWTTProgress(userId);

        // Calculate final elapsed time
        elapsedTime = System.currentTimeMillis() - startTime;
        int totalSeconds = (int) (elapsedTime / 1000);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        String timeTakenMessage = String.format(Locale.getDefault(),
                "You have completed the quiz in %02d minutes and %02d seconds.", minutes, seconds);

        new AlertDialog.Builder(this)
                .setTitle("Quiz Finished!")
                .setMessage(timeTakenMessage)
                .setCancelable(false)
                .setPositiveButton("Restart", (dialog, which) -> restartQuiz())
                .setNegativeButton("Exit", (dialog, which) -> exitToPGNWActivity())
                .show();
    }


    private void restartQuiz() {
        currentQuestionIndex = 0;
        elapsedTime = 0;
        startQuiz();
    }

    private void saveUserProgress() {
        dbHelper.savePGNWTTProgress(userId, currentQuestionIndex, elapsedTime);
    }

    private void loadUserProgress() {
        Cursor cursor = dbHelper.getPGNWTTProgress(userId);
        if (cursor != null && cursor.moveToFirst()) {
            int questionIndexColumn = cursor.getColumnIndex("question_index");
            int elapsedTimeColumn = cursor.getColumnIndex("elapsed_time");

            if (questionIndexColumn != -1) {
                currentQuestionIndex = cursor.getInt(questionIndexColumn);
                Log.d("Progress", "Loaded question index: " + currentQuestionIndex);
            }
            if (elapsedTimeColumn != -1) {
                elapsedTime = cursor.getLong(elapsedTimeColumn);
                Log.d("Progress", "Loaded elapsed time: " + elapsedTime);
            }
        }
        if (cursor != null) {
            cursor.close();
        }
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
