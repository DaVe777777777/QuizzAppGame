package com.example.game;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.text.method.ScrollingMovementMethod;

public class PCA_SM_GM_ACTIVITY extends AppCompatActivity {
    private TextView questionText, progressText, lifeText;
    private Button optionA, optionB, optionC, optionD, exitButton, restartButton;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;
    private int lives = 2; // Start with 2 lives
    private DBHelper dbHelper;
    private String userId = "1";
    private MediaPlayer quizMusic;
    private static PCA_SM_GM_ACTIVITY instance;



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
        Cursor cursor = dbHelper.getSMProgress(userId);
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

        questionList.add(new Question("What is the term for pre-Hispanic Filipino epic poetry?", "Awit", "Kuratsa", "Epiko", "Harana", "Epiko", "Epiko refers to ancient oral literature that tells heroic tales, such as Hinilawod and Biag ni Lam-ang."));
        questionList.add(new Question("Which indigenous group is known for their brassware and intricate metalwork?", "T’boli", "Maranao", "Ifugao", "Mangyan", "Maranao", "The Maranao are skilled in brass-making, especially okir-designed gongs and metalworks."));
        questionList.add(new Question("What is the name of the traditional Muslim dance that uses fans and flowing movements?", "Pangalay", "Itik-itik", "Maglalatik", "Kuratsa", "Pangalay", "Pangalay is a traditional Tausug dance mimicking the graceful movements of waves."));
        questionList.add(new Question("Which traditional Filipino instrument is made of bamboo and played by blowing air through it?", "Kulintang", "Kudyapi", "Tongali", "Agung", "Tongali", "Tongali is a nose flute played by the Kalinga and Ifugao people."));
        questionList.add(new Question("Which national artist is known for the 'Oblation' sculpture?", "Guillermo Tolentino", "Fernando Amorsolo", "Juan Luna", "Vicente Manansala", "Guillermo Tolentino", "Guillermo Tolentino sculpted the Oblation, a symbol of the University of the Philippines."));
        questionList.add(new Question("What is the traditional headwear of the Cordilleran people?", "Bahag", "Salakot", "Tapis", "Suklong", "Suklong", "Suklong is a traditional woven headgear worn by men in the Cordillera."));
        questionList.add(new Question("Which festival in Bacolod is known for its smiling masks?", "Sinulog", "Kadayawan", "MassKara Festival", "Ati-Atihan", "MassKara Festival", "The MassKara Festival features colorful masks with smiling faces, symbolizing resilience."));
        questionList.add(new Question("What is the ancient burial jar shaped like a human figure found in Palawan?", "Manunggul Jar", "Maitum Jar", "Baybayin Jar", "Balangay Jar", "Manunggul Jar", "The Manunggul Jar is a secondary burial jar with intricate carvings found in Palawan."));
        questionList.add(new Question("What is the national leaf of the Philippines?", "Narra", "Anahaw", "Rattan", "Acacia", "Anahaw", "Anahaw is recognized as the national leaf due to its durability and aesthetic appeal."));
        questionList.add(new Question("Which weaving tradition is associated with the Yakan people?", "Inabel", "T'nalak", "Pis Siyabit", "Hablon", "Pis Siyabit", "Pis Siyabit is the handwoven fabric of the Yakan people in Basilan."));
        questionList.add(new Question("Which city is home to Calle Crisologo, a well-preserved Spanish colonial street?", "Vigan", "Cebu", "Davao", "Manila", "Vigan", "A well-preserved Spanish colonial street in Ilocos Sur, a UNESCO World Heritage Site."));
        questionList.add(new Question("What is the national gem of the Philippines?", "Pearl", "Jade", "Diamond", "Amethyst", "Pearl", "The South Sea Pearl, known for its high quality, is mainly found in Palawan."));
        questionList.add(new Question("Which indigenous group from Mindoro is known for their ambahan poetry?", "Mangyan", "Lumad", "Manobo", "Tausug", "Mangyan", "They write ambahan, a seven-syllable poetic form used for storytelling."));
        questionList.add(new Question("Which province is famous for the 'Balangay' boat?", "Tawi-Tawi", "Butuan", "Palawan", "Zamboanga", "Butuan", "The oldest Balangay boats were discovered here, showcasing early Filipino seafaring."));
        questionList.add(new Question("Who painted the famous artwork 'Spoliarium'?", "Juan Luna", "Botong Francisco", "Felix Hidalgo", "Fernando Amorsolo", "Juan Luna", "His masterpiece won a gold medal in Madrid (1884) and symbolizes Filipino struggle."));
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
        new AlertDialog.Builder(this)
                .setTitle("No More Lives!")
                .setMessage("You have no more lives left.")
                .setCancelable(false)
                .setPositiveButton("Try Again", (dialog, which) -> {
                    lives = 2;
                    updateLifeText();
                    showQuestion();
                })
                .setNegativeButton("Restart", (dialog, which) -> restartQuiz())
                .show();
    }

    private void showFinishScreen() {
        dbHelper.clearSMProgress(userId);
        new AlertDialog.Builder(this)
                .setTitle("Quiz Finished!")
                .setMessage("You have completed the quiz.")
                .setCancelable(false)
                .setPositiveButton("Restart", (dialog, which) -> restartQuiz())
                .setNegativeButton("Exit", (dialog, which) -> exitToPCAActivity())
                .show();
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
        dbHelper.saveSMProgress(userId, currentQuestionIndex, lives);
    }

    private void showExitConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Quiz")
                .setMessage("Do you want to exit? Your progress will be saved.")
                .setPositiveButton("Yes", (dialog, which) -> exitToPCAActivity())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
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
