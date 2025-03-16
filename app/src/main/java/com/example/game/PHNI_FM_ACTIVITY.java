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

public class PHNI_FM_ACTIVITY extends AppCompatActivity {
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
    private static PHNI_FM_ACTIVITY instance;

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
        Cursor cursor = dbHelper.getPHNIFMProgress(userId);
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

        questionList.add(new Question("What was the original name of Manila before the Spaniards arrived?", "Intramuros", "Rajahmatanda", "Kota Selurong", "Bagumbayan", "Kota Selurong", "Before the arrival of the Spaniards, Manila was called 'Kota Selurong,' a Muslim fort that was part of the Kingdom of Tondo."));
        questionList.add(new Question("Who is known as the 'Father of the Philippine Revolution'?", "Jose Rizal", "Emilio Aguinaldo", "Andres Bonifacio", "Apolinario Mabini", "Andres Bonifacio", "Andres Bonifacio founded the Katipunan (KKK), a secret revolutionary society that fought against Spanish rule. Because of his leadership in the revolution, he is regarded as the 'Father of the Philippine Revolution.'"));
        questionList.add(new Question("Which treaty ended the Spanish-American War and gave control of the Philippines to the Americans?", "Treaty of Paris", "Treaty of Biak-na-Bato", "Treaty of Tordesillas", "Treaty of Manila Bay", "Treaty of Paris", "The Treaty of Paris (1898) officially ended the Spanish-American War and resulted in Spain ceding the Philippines to the United States in exchange for $20 million."));
        questionList.add(new Question("What was the main goal of the Propaganda Movement?", "To demand reforms from Spain", "To establish an independent government", "To start a revolution", "To negotiate with the Americans", "To demand reforms from Spain", "The Propaganda Movement (1872–1892) was a peaceful movement led by Jose Rizal, Marcelo H. del Pilar, and Graciano López Jaena. Its main goal was to demand reforms from Spain rather than an immediate revolution."));
        questionList.add(new Question("What does 'KKK' stand for in Katipunan?", "Kataastaasan, Kagalanggalangan, Katipunan ng mga Anak ng Bayan", "Freedom, Justice, Peace", "Truth, Honor, Freedom", "Association of Fellow Countrymen", "Kataastaasan, Kagalanggalangan, Katipunan ng mga Anak ng Bayan", "This is the full name of Katipunan, a secret society that aimed to liberate the Philippines from Spanish rule."));
        questionList.add(new Question("Where did Jose Rizal write 'Mi Último Adiós'?", "Fort Santiago", "Dapitan", "Malacañang", "Cavite", "Fort Santiago", "Jose Rizal wrote his last poem, Mi Último Adiós (My Last Farewell), while imprisoned at Fort Santiago, just hours before his execution in Bagumbayan (Luneta) on December 30, 1896."));
        questionList.add(new Question("What is the economic system where colonies provide wealth to the mother country?", "Capitalism", "Colonialism", "Mercantilism", "Feudalism", "Mercantilism", "Mercantilism is an economic system in which colonies (like the Philippines) provide natural resources and products to their colonizers (Spain) to strengthen their economy."));
        questionList.add(new Question("What was the main purpose of the Jones Law of 1916?", "To grant independence to the Philippines", "To introduce English education to Filipinos", "To continue Spanish rule", "To establish a military force in the Philippines", "To grant independence to the Philippines", "The Jones Law (1916) was a U.S. law that promised to grant independence to the Philippines once it was deemed ready for self-governance."));
        questionList.add(new Question("Which city was the first capital of the Philippines under Spanish rule?", "Manila", "Cebu", "Vigan", "Iloilo", "Cebu", "When Miguel López de Legazpi arrived in 1565, he made Cebu the first capital of the Philippines before transferring it to Manila in 1571."));
        questionList.add(new Question("What was the main cause of Antonio Luna’s death?", "Heart disease", "Assassination by Aguinaldo’s men", "Accidental poisoning", "Fell off a cliff while fighting", "Assassination by Aguinaldo’s men", "Antonio Luna, a general of the Philippine Revolution, was assassinated in Cabanatuan, Nueva Ecija, in 1899 by soldiers under Emilio Aguinaldo due to internal conflicts within the revolutionary army."));
        questionList.add(new Question("What was the famous pen name of Marcelo H. del Pilar?", "Plaridel", "Dimasalang", "Laong Laan", "Jomapa", "Plaridel", "Marcelo H. del Pilar used the pen name Plaridel in his writings to conceal his identity from Spanish authorities."));
        questionList.add(new Question("Where was Philippine independence declared on June 12, 1898?", "Malolos, Bulacan", "Cavite, Cavite", "Kawit, Cavite", "Tarlac, Tarlac", "Kawit, Cavite", "The Declaration of Philippine Independence was proclaimed at the House of Emilio Aguinaldo in Kawit, Cavite, on June 12, 1898."));
        questionList.add(new Question("What was the main goal of 'La Liga Filipina'?", "To promote peaceful reforms in the Philippines", "To lead a revolution against Spain", "To cooperate with the Americans", "To establish a religious organization", "To promote peaceful reforms in the Philippines", "Jose Rizal founded La Liga Filipina in 1892 to unite Filipinos in seeking peaceful reforms from Spain."));
        questionList.add(new Question("Who was the first governor-general of the Philippines under American rule?", "William Howard Taft", "Douglas MacArthur", "Henry Allen", "Arthur MacArthur", "William Howard Taft", "In 1901, William Howard Taft became the first American governor-general of the Philippines."));
        questionList.add(new Question("What does the blue color on the Philippine flag represent?", "Bravery", "Freedom", "Peace and Justice", "Independence", "Peace and Justice", "The blue in the Philippine flag symbolizes peace, justice, and unity among Filipinos."));
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
        dbHelper.clearPHNIFMProgress(userId);

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
                .setNegativeButton("Exit", (dialog, which) -> exitToPHNIActivity());

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
        dbHelper.savePHNIFMProgress(userId, currentQuestionIndex, timeRemaining);  // Save current timeRemaining
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
                .setPositiveButton("Yes", (dialog, which) -> exitToPHNIActivity())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
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