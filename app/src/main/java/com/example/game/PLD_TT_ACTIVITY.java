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

public class PLD_TT_ACTIVITY extends AppCompatActivity {
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
    private static PLD_TT_ACTIVITY instance;

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
                .setPositiveButton("Yes", (dialog, which) -> exitToPLDActivity())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void exitToPLDActivity() {
        saveUserProgress();
        stopQuizMusic();
        Intent intent = new Intent(this, PLDActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadQuestions() {
        questionList = new ArrayList<>();

        questionList.add(new Question("Which region in Mindanao has the most diverse languages?", "Zamboanga Peninsula", "CARAGA", "Bangsamoro Autonomous Region in Muslim Mindanao (BARMM)", "Davao Region", "Bangsamoro Autonomous Region in Muslim Mindanao (BARMM)", "BARMM has multiple languages, including Maranao, Maguindanaon, Tausug, and Yakan."));
        questionList.add(new Question("What is the official language of government communication in the Philippines?", "Spanish", "Cebuano", "Filipino and English", "Hiligaynon", "Filipino and English", "Filipino and English are the official languages used in government and education."));
        questionList.add(new Question("What is the major language spoken in Palawan?", "Kapampangan", "Cuyonon", "Ilocano", "Waray", "Cuyonon", "Cuyonon is the dominant language spoken in Palawan."));
        questionList.add(new Question("What language is spoken in Zamboanga that has strong Spanish influence?", "Hiligaynon", "Chavacano", "Maranao", "Pangasinense", "Chavacano", "Chavacano is a Spanish-based creole spoken in Zamboanga City."));
        questionList.add(new Question("How many officially recognized languages are there in the Philippines?", "8", "50", "120-187", "300+", "120-187", "The Philippines has between 120 to 187 languages, depending on classification."));
        questionList.add(new Question("What is the term for languages that are at risk of disappearing?", "Dead languages", "Endangered languages", "Extinct languages", "Hybrid languages", "Endangered languages", "Several Philippine languages, such as Inagta and Ayta languages, are classified as endangered due to declining speakers."));
        questionList.add(new Question("What is the difference between a language and a dialect?", "Languages have written forms, dialects do not.", "Dialects are informal versions of a language.", "Dialects are mutually intelligible variations of a language.", "A language is spoken by many people, while a dialect is rare.", "Dialects are mutually intelligible variations of a language.", "Dialects are variations of a language that speakers can still understand, whereas different languages are not mutually intelligible."));
        questionList.add(new Question("The language spoken by the Ivatan people of Batanes is called?", "Ivatan", "Ibanag", "Ilocano", "Bicolano", "Ivatan", "Ivatan is spoken in Batanes and is distinct from other Philippine languages."));
        questionList.add(new Question("What is the official alphabet used for writing Filipino today?", "Abakada", "Baybayin", "Modern Latin Alphabet", "Alibata", "Modern Latin Alphabet", "The Modern Filipino Alphabet (A-Z) replaced the Abakada system."));
        questionList.add(new Question("Which indigenous language is spoken by the Mangyan people?", "Yakan", "Tagbanwa", "Hanunuo", "Ibanag", "Hanunuo", "Hanunuo is one of the Mangyan languages spoken in Mindoro."));
        questionList.add(new Question("How is 'Thank you' said in Ilocano?", "Salamat", "Dios ti agngina", "Maraming salamat", "Daghang salamat", "Dios ti agngina", "In Ilocano, 'Thank you' is 'Dios ti agngina.'"));
        questionList.add(new Question("Which region is known for speaking Chavacano, a Spanish-based Creole?", "Bicol Region", "Zamboanga Peninsula", "Cordillera Region", "Ilocos Region", "Zamboanga Peninsula", "Chavacano is a Spanish-based Creole language spoken in Zamboanga and Cavite."));
        questionList.add(new Question("What does the Waray phrase 'Maupay nga aga' mean?", "Good evening", "Good morning", "Welcome", "How are you?", "Good morning", "'Maupay nga aga' means 'Good morning' in Waray."));
        questionList.add(new Question("What language is spoken in the Cordillera region?", "Ivatan", "Ilocano", "Kankanaey", "Bikol", "Kankanaey", "Kankanaey is spoken by some indigenous groups in the Cordillera region."));
        questionList.add(new Question("How many officially recognized languages are there in the Philippines?", "120", "134", "175", "187", "175", "The Philippines has about 175 languages, with most classified as Malayo-Polynesian languages."));
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
        dbHelper.clearPLDTTProgress(userId);

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
                .setNegativeButton("Exit", (dialog, which) -> exitToPLDActivity())
                .show();
    }


    private void restartQuiz() {
        currentQuestionIndex = 0;
        elapsedTime = 0;
        startQuiz();
    }

    private void saveUserProgress() {
        dbHelper.savePLDTTProgress(userId, currentQuestionIndex, elapsedTime);
    }

    private void loadUserProgress() {
        Cursor cursor = dbHelper.getPLDTTProgress(userId);
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

