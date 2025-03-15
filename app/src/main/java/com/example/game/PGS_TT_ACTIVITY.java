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

public class PGS_TT_ACTIVITY extends AppCompatActivity {
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
    private static PGS_TT_ACTIVITY instance;

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
                .setPositiveButton("Yes", (dialog, which) -> exitToPGSActivity())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void exitToPGSActivity() {
        saveUserProgress();
        stopQuizMusic();
        Intent intent = new Intent(this, PGSActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadQuestions() {
        questionList = new ArrayList<>();

        questionList.add(new Question("What is the legislative district requirement for a congressional representative?", "1,000 people", "100,000 people", "250,000 people", "500,000 people", "250,000 people", "Each legislative district must represent at least 250,000 citizens."));
        questionList.add(new Question("What is the main role of the Ombudsman?", "Prosecute criminals", "Investigate corruption", "Enforce tax laws", "Approve budgets", "Investigate corruption", "The Ombudsman investigates misconduct in government agencies."));
        questionList.add(new Question("What is the term length of a Senator?", "3 years", "6 years", "9 years", "12 years", "6 years", "Senators serve six-year terms, with elections held every three years for half of the seats."));
        questionList.add(new Question("Which branch of government implements laws?", "Executive", "Legislative", "Judicial", "Local Government", "Executive", "The Executive branch, headed by the President, enforces laws and policies."));
        questionList.add(new Question("How many justices are in the Supreme Court?", "9", "12", "15", "21", "15", "The Supreme Court consists of one Chief Justice and 14 Associate Justices."));
        questionList.add(new Question("What principle ensures that no branch of government is too powerful?", "Centralization", "Separation of Powers", "Totalitarianism", "Absolute Monarchy", "Separation of Powers", "The Constitution ensures that power is divided among the Executive, Legislative, and Judicial branches to prevent abuse."));
        questionList.add(new Question("What is the minimum age requirement to run for President in the Philippines?", "25 years old", "30 years old", "35 years old", "40 years old", "40 years old", "A presidential candidate must be at least 40 years old, a natural-born citizen, and a resident of the Philippines for at least 10 years."));
        questionList.add(new Question("Which agency is responsible for conducting elections in the Philippines?", "Department of Justice", "Commission on Elections (COMELEC)", "Supreme Court", "Ombudsman", "Commission on Elections (COMELEC)", "COMELEC manages the electoral process, ensuring fair and free elections."));
        questionList.add(new Question("How many senators are elected in the Philippines?", "12", "24", "50", "100", "24", "The Philippine Senate consists of 24 members, elected at large by the entire nation."));
        questionList.add(new Question("What is the legislative body of the Philippines?", "National Assembly", "Congress", "Senate", "House of Representatives", "Congress", "The Congress consists of two chambers: the Senate (Upper House) and the House of Representatives (Lower House)."));
        questionList.add(new Question("What is the highest court in the Philippines?", "Court of Appeals", "Sandiganbayan", "Supreme Court", "Regional Trial Court", "Supreme Court", "The Supreme Court is the highest judicial authority in the Philippines, interpreting the law and ruling on constitutional matters."));
        questionList.add(new Question("How long is the term of a Philippine President?", "4 years", "6 years", "5 years", "7 years", "6 years", "Under the 1987 Constitution, the President is elected for a single six-year term without reelection."));
        questionList.add(new Question("How many branches of government does the Philippines have?", "One", "Two", "Three", "Four", "Three", "The Philippine government has three branches: Executive, Legislative, and Judicial, to ensure the separation of powers."));
        questionList.add(new Question("What was the role of the 'Babaylan' in pre-colonial Philippine society?", "Political leader", "Religious leader and healer", "Warrior", "Blacksmith", "Religious leader and healer", "The Babaylan was a shaman or priestess who performed rituals, healed the sick, and acted as a spiritual guide."));
        questionList.add(new Question("What was the Cavite Mutiny of 1872?", "A battle against American forces", "A rebellion of Filipino soldiers and workers", "A peaceful protest for independence", "A revolution led by Andres Bonifacio", "A rebellion of Filipino soldiers and workers", "The Cavite Mutiny was an uprising against Spanish rule that led to the execution of Gomburza."));
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
        dbHelper.clearPGSTTProgress(userId);

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
                .setNegativeButton("Exit", (dialog, which) -> exitToPGSActivity())
                .show();
    }


    private void restartQuiz() {
        currentQuestionIndex = 0;
        elapsedTime = 0;
        startQuiz();
    }

    private void saveUserProgress() {
        dbHelper.savePGSTTProgress(userId, currentQuestionIndex, elapsedTime);
    }

    private void loadUserProgress() {
        Cursor cursor = dbHelper.getPGSTTProgress(userId);
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
