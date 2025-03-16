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

public class PGS_SM_ACTIVITY extends AppCompatActivity {
    private TextView questionText, progressText, lifeText;
    private Button optionA, optionB, optionC, optionD, exitButton, restartButton;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;
    private int lives = 2; // Start with 2 lives
    private DBHelper dbHelper;
    private String userId = "1";
    private MediaPlayer quizMusic;
    private static PGS_SM_ACTIVITY instance;


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
        Cursor cursor = dbHelper.getPGSSMProgress(userId);
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
        questionList.add(new Question("In an emergency situation where you need police assistance, what is the national emergency hotline in the Philippines?", "166", "8888", "117", "911", "911", "The official national emergency hotline in the Philippines is 911, which connects people to the police, fire department, and medical services."));
        questionList.add(new Question("If you are caught in a natural disaster and need to find temporary shelter, which government agency should you contact?", "PAGASA", "NDRRMC", "PNP", "DPWH", "NDRRMC", "The National Disaster Risk Reduction and Management Council (NDRRMC) coordinates disaster preparedness, response, and relief operations in the country."));
        questionList.add(new Question("What is the legal curfew for minors in most cities in the Philippines?", "8:00 PM - 5:00 AM", "10:00 PM - 4:00 AM", "9:00 PM - 6:00 AM", "7:00 PM - 7:00 AM", "10:00 PM - 4:00 AM", "While curfew laws may vary per local government, the most common curfew hours for minors (below 18) are 10 PM to 4 AM, as stated in various city ordinances."));
        questionList.add(new Question("If you are a victim of a crime, which government body should you report to for investigation and prosecution?", "DSWD", "DOJ", "CHR", "NBI", "NBI", "The National Bureau of Investigation (NBI) handles crime investigations, including fraud, cybercrime, and serious offenses. The Philippine National Police (PNP) is also a key contact."));
        questionList.add(new Question("What is the first thing you should do if you are unjustly arrested?", "Run away", "Sign any document to avoid further trouble", "Demand a lawyer and remain silent", "Explain your side immediately", "Demand a lawyer and remain silent", "Under the Miranda Rights in the Philippines, you have the right to remain silent and request legal counsel. Speaking without a lawyer can be used against you."));
        questionList.add(new Question("In case of workplace abuse, where can employees file a complaint?", "BIR", "DOLE", "SSS", "DSWD", "DOLE", "The Department of Labor and Employment (DOLE) handles labor disputes, workplace abuse, and employee rights violations."));
        questionList.add(new Question("During elections, how can you check if you are a registered voter?", "Visit the DSWD website", "Go to the PNP headquarters", "Check with the COMELEC website", "Ask a barangay official", "Check with the COMELEC website", "The Commission on Elections (COMELEC) provides an official online portal where voters can check their registration status."));
        questionList.add(new Question("If a government official is involved in corruption, where should you file a complaint?", "Sandiganbayan", "Ombudsman", "DOH", "SEC", "Ombudsman", "The Office of the Ombudsman investigates and prosecutes government officials involved in corruption, abuse of power, and illegal activities."));
        questionList.add(new Question("What is the penalty for not wearing a helmet while riding a motorcycle?", "₱500 fine", "₱1,500 fine", "₱2,500 fine", "No penalty", "₱2,500 fine", "Under the Motorcycle Helmet Act (RA 10054), riding a motorcycle without a helmet carries penalties starting at ₱1,500 for the first offense and ₱2,500 for the second."));
        questionList.add(new Question("What government program provides free tuition for state universities and colleges?", "TESDA Program", "Pantawid Pamilyang Pilipino Program (4Ps)", "Universal Access to Quality Tertiary Education Act", "DepEd Scholarship Program", "Universal Access to Quality Tertiary Education Act", "RA 10931 guarantees free tuition for students in state universities and colleges (SUCs) and local universities and colleges (LUCs)."));
        questionList.add(new Question("How many years must a natural-born Filipino reside in the Philippines before running for President?", "5 years", "10 years", "12 years", "20 years", "10 years", "According to the 1987 Constitution, a candidate must be a natural-born Filipino and a resident for at least 10 years before running for President."));
        questionList.add(new Question("If you are wrongly detained for a crime you did not commit, which legal remedy can be used to secure your release?", "Writ of Habeas Corpus", "Writ of Amparo", "Writ of Kalikasan", "Writ of Mandamus", "Writ of Habeas Corpus", "Habeas Corpus protects against illegal detention and ensures a person is brought before a court to determine the validity of their detention."));
        questionList.add(new Question("What is the minimum voting age in the Philippines?", "16 years old", "17 years old", "18 years old", "21 years old", "18 years old", "The legal voting age in the Philippines is 18 years old, as stated in the 1987 Constitution."));
        questionList.add(new Question("Which government agency issues passports in the Philippines?", "DFA", "DILG", "NBI", "BI", "DFA", "The Department of Foreign Affairs (DFA) is responsible for issuing Philippine passports and handling foreign affairs."));
        questionList.add(new Question("If a company is engaged in illegal recruitment, where should victims report the case?", "DTI", "DOLE", "POEA/DMW", "NBI", "POEA/DMW", "The Philippine Overseas Employment Administration (POEA), now merged into the Department of Migrant Workers (DMW), handles illegal recruitment complaints and oversees overseas employment."));
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
        dbHelper.clearPGSSMProgress(userId);

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
                .setNegativeButton("Exit", (dialog, which) -> exitToPGSActivity());

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
        dbHelper.savePGSSMProgress(userId, currentQuestionIndex, lives);
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

