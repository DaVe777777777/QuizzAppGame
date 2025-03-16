
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

public class PLD_FM_ACTIVITY extends AppCompatActivity {
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
    private static PLD_FM_ACTIVITY instance;

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
        Cursor cursor = dbHelper.getPLDFMProgress(userId);
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

        questionList.add(new Question("The word 'salamat' (thank you) in many Philippine languages comes from which foreign language?", "Sanskrit", "Malay", "Arabic", "Spanish", "Arabic", "'Salamat' comes from the Arabic word 'salaam' (peace), reflecting early Muslim influence."));
        questionList.add(new Question("The word 'kababayan' means what in Filipino?", "Foreigner", "Fellow countryman", "Government official", "Family", "Fellow countryman", "'Kababayan' is derived from 'bayan' (nation) and means someone from the same country."));
        questionList.add(new Question("The Philippine government officially recognizes how many major languages?", "5", "8", "13", "19", "8", "The government recognizes Tagalog, Cebuano, Ilocano, Hiligaynon, Bicolano, Waray, Kapampangan, and Pangasinense as major languages."));
        questionList.add(new Question("What was the main reason for introducing English in the Philippines?", "To replace Spanish", "To unite different Filipino ethnic groups", "To make the Philippines a U.S. state", "To prepare Filipinos for independence", "To unite different Filipino ethnic groups", "The use of English was meant to serve as a common language among diverse Filipino groups."));
        questionList.add(new Question("What system did the Americans establish to teach English?", "Public school system", "Religious institutions", "Private academies", "Military education", "Public school system", "The Americans created a public education system with English as the medium of instruction."));
        questionList.add(new Question("What was the first English newspaper in the Philippines?", "Manila Bulletin", "The Manila Times", "The Philippine Star", "La Solidaridad", "The Manila Times", "The Manila Times was the first English-language newspaper in the Philippines."));
        questionList.add(new Question("What government agency is responsible for language development in the Philippines?", "Department of Education", "Komisyon sa Wikang Filipino", "National Historical Commission", "National Commission for Culture and the Arts", "Komisyon sa Wikang Filipino", "The KWF oversees language policies and preservation."));
        questionList.add(new Question("What is the purpose of the 'Buwan ng Wika' celebration?", "Promote regional dialects", "Celebrate national identity", "Honor Jose Rizal", "Teach English", "Celebrate national identity", "Buwan ng Wika promotes Filipino culture and language."));
        questionList.add(new Question("Who wrote the first Tagalog dictionary, 'Vocabulario de la lengua tagala'?", "Marcelo H. Del Pilar", "Tomas Pinpin", "Pedro Chirino", "Juan de Noceda & Pedro de Sanlucar", "Juan de Noceda & Pedro de Sanlucar", "Their dictionary (1754) was a major reference for studying early Tagalog."));
        questionList.add(new Question("What law created the Komisyon sa Wikang Filipino (KWF)?", "Republic Act No. 7104", "Republic Act No. 10157", "Executive Order No. 210", "Batas Pambansa Blg. 232", "Republic Act No. 7104", "KWF was established to develop and promote Filipino as the national language."));
        questionList.add(new Question("Which Spanish decree established Spanish as the official language of the Philippines in the 16th century?", "Maura Law", "Spanish Royal Decree of 1593", "Treaty of Paris", "Jones Law", "Spanish Royal Decree of 1593", "This decree enforced Spanish as the official language during the Spanish colonization."));
        questionList.add(new Question("What was the primary purpose of using English in Philippine schools?", "To promote nationalism", "To unify the country linguistically", "To prepare Filipinos for self-governance", "To eradicate indigenous languages", "To prepare Filipinos for self-governance", "The Americans believed that educating Filipinos in English would prepare them for democracy and governance."));
        questionList.add(new Question("What is the difference between Tagalog and Filipino?", "No difference", "Filipino includes more borrowed words", "Filipino is only spoken in Manila", "Filipino is based on Cebuano", "Filipino includes more borrowed words", "Filipino is an evolving language that incorporates words from various Philippine and foreign languages."));
        questionList.add(new Question("Which law requires Filipino and English as the official languages of instruction?", "Education Act of 1982", "Bilingual Education Policy of 1974", "Magna Carta for Teachers", "Republic Act 10157", "Bilingual Education Policy of 1974", "This policy mandated both Filipino and English as mediums of instruction."));
        questionList.add(new Question("What is Mother Tongue-Based Multilingual Education (MTB-MLE)?", "Using only English in schools", "Teaching in students' native languages first", "Prohibiting local languages in schools", "Exclusive use of Filipino in education", "Teaching in students' native languages first", "MTB-MLE emphasizes early education in a child's first language for better comprehension."));
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
        dbHelper.clearPLDFMProgress(userId);

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
                .setNegativeButton("Exit", (dialog, which) -> exitToPLDActivity());

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
        dbHelper.savePLDFMProgress(userId, currentQuestionIndex, timeRemaining);  // Save current timeRemaining
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
