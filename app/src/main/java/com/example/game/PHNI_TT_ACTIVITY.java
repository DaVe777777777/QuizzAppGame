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

public class PHNI_TT_ACTIVITY extends AppCompatActivity {
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
    private static PHNI_TT_ACTIVITY instance;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phni_tt);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create a custom TextView for the title
        TextView titleView = new TextView(this);
        titleView.setText("Exit Quiz");
        titleView.setTextSize(30); // Bigger title size
        titleView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
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

    private void loadQuestions() {
        questionList = new ArrayList<>();

        questionList.add(new Question("Who is known as the 'Father of the Katipunan'?", "Emilio Aguinaldo", "Jose Rizal", "Andres Bonifacio", "Apolinario Mabini", "Andres Bonifacio", "Andres Bonifacio founded the Katipunan in 1892, a secret revolutionary society that fought for Philippine independence from Spain."));
        questionList.add(new Question("On what date was Philippine Independence declared?", "June 12, 1898", "July 4, 1946", "August 30, 1896", "December 30, 1896", "June 12, 1898", "Emilio Aguinaldo declared Philippine independence from Spain on June 12, 1898, in Kawit, Cavite."));
        questionList.add(new Question("What is the official national anthem of the Philippines?", "Bayan Ko", "Lupang Hinirang", "Pilipinas Kong Mahal", "Ako ay Pilipino", "Lupang Hinirang", "The national anthem of the Philippines, Lupang Hinirang, was composed by Julian Felipe in 1898."));
        questionList.add(new Question("What is the name of the ancient Philippine script?", "Alibata/Baybayin", "Sanskrit", "Kawi", "Latin", "Alibata/Baybayin", "Baybayin (often mistakenly called Alibata) is the ancient writing system of the pre-colonial Filipinos."));
        questionList.add(new Question("What was the primary goal of the Katipunan?", "To become a part of Spain", "To gain independence from Spain", "To spread Catholicism", "To continue Spanish colonization", "To gain independence from Spain", "The Katipunan was founded to achieve independence from Spain through armed revolution."));
        questionList.add(new Question("Who is known as the 'Brains of the Revolution'?", "Emilio Aguinaldo", "Andres Bonifacio", "Apolinario Mabini", "Antonio Luna", "Apolinario Mabini", "Apolinario Mabini was called the 'Brains of the Revolution' because he served as Emilio Aguinaldo’s chief adviser and wrote the first Philippine Constitution."));
        questionList.add(new Question("What was the name of the secret society founded by Andres Bonifacio?", "Katipunan", "La Solidaridad", "Propaganda Movement", "Sanduguan", "Katipunan", "The Katipunan was a secret revolutionary society founded by Andres Bonifacio to overthrow Spanish rule."));
        questionList.add(new Question("What was the name of Ferdinand Magellan’s ship?", "Victoria", "Santa Maria", "Trinidad", "San Juan Bautista", "Victoria", "Victoria was the only ship from Magellan’s fleet that successfully completed the first circumnavigation of the world."));
        questionList.add(new Question("Who was the first female hero who fought against the Spaniards?", "Gabriela Silang", "Gregoria de Jesus", "Melchora Aquino", "Teresa Magbanua", "Gabriela Silang", "Gabriela Silang led the Ilocano resistance against Spanish forces after her husband, Diego Silang, was killed."));
        questionList.add(new Question("Who wrote 'El Filibusterismo'?", "Emilio Aguinaldo", "Andres Bonifacio", "Jose Rizal", "Marcelo H. del Pilar", "Jose Rizal", "Jose Rizal wrote 'El Filibusterismo' (1891) as a sequel to 'Noli Me Tangere' to expose the injustices under Spanish rule."));
        questionList.add(new Question("Which national symbol features the sun with eight rays?", "Philippine Flag", "Katipunan Flag", "Seal of Manila", "Magdiwang Banner", "Philippine Flag", "The sun in the Philippine flag has eight rays, representing the first eight provinces that revolted against Spain."));
        questionList.add(new Question("Who is known as the 'Sublime Paralytic'?", "Apolinario Mabini", "Emilio Aguinaldo", "Jose Rizal", "Manuel Quezon", "Apolinario Mabini", "Apolinario Mabini was called the 'Sublime Paralytic' because he was paralyzed due to polio but still played a key role in the Philippine Revolution."));
        questionList.add(new Question("Where was Jose Rizal executed?", "Luneta", "Intramuros", "Cavite", "Pampanga", "Luneta", "Jose Rizal was executed on December 30, 1896, in Bagumbayan (now Luneta/Rizal Park) for inspiring the revolution against Spain."));
        questionList.add(new Question("What was the original name of Manila before the Spaniards arrived?", "Tondo", "Rajahnate of Cebu", "Kingdom of Manila", "Kota Selurong", "Kota Selurong", "Before Spanish colonization, Manila was called 'Kota Selurong,' a fortified Muslim settlement under the Kingdom of Tondo."));
        questionList.add(new Question("What does 'KKK' stand for in Philippine history?", "Kataastaasan, Kagalang-galangang Katipunan ng mga Anak ng Bayan", "Katipunan ng Kabataang Kababaihan", "Kasunduan ng Kalayaan at Karapatan", "Katipunan ng mga Kapatid na Katoliko", "Kataastaasan, Kagalang-galangang Katipunan ng mga Anak ng Bayan", "This is the full name of the Katipunan, the secret organization that led the Philippine Revolution against Spain."));
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

        // Create a TextView for the title (Correct/Wrong Answer)
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(30); // Bigger title size
        titleView.setTextColor(isCorrect ? getResources().getColor(android.R.color.holo_green_dark)
                : getResources().getColor(android.R.color.holo_red_dark));
        titleView.setPadding(50, 40, 50, 20);
        titleView.setGravity(View.TEXT_ALIGNMENT_CENTER); // Center align

        // Create a TextView for the message (Explanation)
        TextView messageView = new TextView(this);
        messageView.setText(message);
        messageView.setTextSize(22); // Set text size
        messageView.setTextColor(getResources().getColor(android.R.color.black));
        messageView.setPadding(50, 20, 50, 20);
        messageView.setGravity(View.TEXT_ALIGNMENT_CENTER); // Center align

        builder.setCustomTitle(titleView) // Use custom title
                .setView(messageView) // Set custom view with bigger text
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
        dbHelper.clearPHNITTProgress(userId);

        // Calculate final elapsed time
        elapsedTime = System.currentTimeMillis() - startTime;
        int totalSeconds = (int) (elapsedTime / 1000);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        String timeTakenMessage = String.format(Locale.getDefault(),
                "You have completed the quiz in %02d minutes and %02d seconds.", minutes, seconds);

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
        messageView.setText(timeTakenMessage);
        messageView.setTextSize(22); // Bigger message size
        messageView.setTextColor(getResources().getColor(android.R.color.black));
        messageView.setPadding(50, 20, 50, 20);
        messageView.setGravity(View.TEXT_ALIGNMENT_CENTER); // Center align

        builder.setCustomTitle(titleView) // Use custom title
                .setView(messageView) // Set custom view for the message
                .setCancelable(false)
                .setPositiveButton("Restart", (dialog, which) -> restartQuiz())
                .setNegativeButton("Exit", (dialog, which) -> exitToPHNIActivity());

        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void restartQuiz() {
        currentQuestionIndex = 0;
        elapsedTime = 0;
        startQuiz();
    }

    private void saveUserProgress() {
        dbHelper.savePHNITTProgress(userId, currentQuestionIndex, elapsedTime);
    }

    private void loadUserProgress() {
        Cursor cursor = dbHelper.getPHNITTProgress(userId);
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