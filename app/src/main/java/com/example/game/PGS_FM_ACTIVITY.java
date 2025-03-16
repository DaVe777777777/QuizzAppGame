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

public class PGS_FM_ACTIVITY extends AppCompatActivity {
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
    private static PGS_FM_ACTIVITY instance;

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
        Cursor cursor = dbHelper.getPGSFMProgress(userId);
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
        questionList.add(new Question("What is the local government unit (LGU) hierarchy from smallest to largest?", "Province → Municipality → Barangay", "Barangay → Municipality → Province", "Municipality → Barangay → Province", "Barangay → Province → Municipality", "Barangay → Municipality → Province", "The barangay is the smallest government unit, followed by the municipality/city and province."));
        questionList.add(new Question("What are the three branches of the Philippine government?", "Legislative, Executive, Judiciary", "Senate, Congress, Supreme Court", "Federal, Unitary, Autonomous", "President, Prime Minister, King", "Legislative, Executive, Judiciary", "The government follows the principle of separation of powers, where each branch has distinct roles."));
        questionList.add(new Question("What is the principle of 'separation of powers'?", "All government powers belong to one branch", "Powers are divided among the three branches", "Only the President has authority", "The Judiciary controls the government", "Powers are divided among the three branches", "The principle ensures that the Executive, Legislative, and Judiciary remain independent to prevent abuse of power."));
        questionList.add(new Question("What government agency handles corruption cases involving public officials?", "Department of Justice (DOJ)", "Sandiganbayan", "Civil Service Commission (CSC)", "Ombudsman", "Sandiganbayan", "The Sandiganbayan is a special court handling cases related to government corruption and misconduct."));
        questionList.add(new Question("Which government agency is responsible for peace and order in the Philippines?", "AFP", "PNP", "DOJ", "DSWD", "PNP", "The Philippine National Police (PNP) enforces laws and maintains public safety."));
        questionList.add(new Question("What is the national motto of the Philippines?", "Bayan Ko", "Isang Bansa, Isang Diwa", "Maka-Diyos, Maka-Tao, Makakalikasan, at Makabansa", "Tayo ang Pag-asa", "Maka-Diyos, Maka-Tao, Makakalikasan, at Makabansa", "This motto reflects Filipino values: Faith in God, Love for Humanity, Care for Nature, and Patriotism."));
        questionList.add(new Question("What is the primary duty of the Department of Foreign Affairs (DFA)?", "Manage the economy", "Handle international relations", "Enforce laws", "Oversee elections", "Handle international relations", "The DFA manages the Philippines' diplomatic relations and foreign policies."));
        questionList.add(new Question("What is the local government unit (LGU) system in the Philippines based on?", "Republic Act No. 7160", "Presidential Orders", "Supreme Court Rulings", "The Treaty of Manila", "Republic Act No. 7160", "The Local Government Code of 1991 (RA 7160) defines the powers and responsibilities of LGUs in governance."));
        questionList.add(new Question("What is the role of the Ombudsman in the Philippines?", "Pass laws", "Prosecute corrupt officials", "Lead the judiciary", "Manage national finances", "Prosecute corrupt officials", "The Ombudsman investigates and prosecutes government officials accused of corruption and misconduct."));
        questionList.add(new Question("Who is the current President of the Philippines?", "Rodrigo Duterte", "Bongbong Marcos", "Leni Robredo", "Gloria Macapagal-Arroyo", "Bongbong Marcos", "Ferdinand 'Bongbong' Marcos Jr. was elected as the 17th President of the Philippines in 2022 (Check for updates)."));
        questionList.add(new Question("What is the main economic system of the Philippines?", "Communism", "Mixed Economy", "Pure Capitalism", "Socialism", "Mixed Economy", "The Philippines has a mixed economy, combining private and government participation in economic activities."));
        questionList.add(new Question("What is the main function of the Ombudsman?", "Create laws", "Prosecute criminals", "Investigate government corruption", "Approve budgets", "Investigate government corruption", "The Ombudsman investigates and prosecutes corrupt government officials."));
        questionList.add(new Question("What is the role of the Local Government Units (LGUs)?", "Control foreign policies", "Manage local governance", "Enforce martial law", "Approve presidential decisions", "Manage local governance", "LGUs, including provinces, cities, and barangays, handle local development, public services, and community governance under the Local Government Code of 1991."));
        questionList.add(new Question("What is the responsibility of the Ombudsman?", "Defend the President", "Investigate government corruption", "Collect taxes", "Prosecute criminals", "Investigate government corruption", "The Ombudsman ensures government officials are accountable by investigating corruption and misconduct."));
        questionList.add(new Question("Which agency is responsible for taxation in the Philippines?", "Bureau of Customs (BOC)", "Commission on Audit (COA)", "Bureau of Internal Revenue (BIR)", "Bangko Sentral ng Pilipinas (BSP)", "Bureau of Internal Revenue (BIR)", "BIR collects taxes, ensuring revenue for national development and public services."));
    }

    private void startQuiz() {
        showQuestion();
    }

    private void startTimer() {
        timerHandler.removeCallbacks(timerRunnable);

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
        dbHelper.clearPGSFMProgress(userId);

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
                .setNegativeButton("Exit", (dialog, which) -> exitToPGSActivity());

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
        dbHelper.savePGSFMProgress(userId, currentQuestionIndex, timeRemaining);  // Save current timeRemaining
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
                .setPositiveButton("Yes", (dialog, which) -> exitToPGSActivity())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void exitToPGSActivity() {
        saveUserProgress();
        stopQuizMusic();
        Intent intent = new Intent(this, PGSActivity.class);
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