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


public class PCA_TT_GM_ACTIVITY extends AppCompatActivity {
    private TextView questionText, progressText, timerText;
    private Button optionA, optionB, optionC, optionD, exitButton;
    private int currentQuestionIndex = 0;
    private List<Question> questionList;
    private DBHelper dbHelper;
    private long startTime, elapsedTime;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;
    private String userId = "1"; // Example user ID, replace with actual user data
    private MediaPlayer quizMusic;
    private static PCA_TT_GM_ACTIVITY instance;

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
                .setPositiveButton("Yes", (dialog, which) -> exitToPCAActivity())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void exitToPCAActivity() {
        saveUserProgress(); // Ensure progress is saved before exiting
        stopQuizMusic();
        Intent intent = new Intent(this, PCAActivity.class);
        startActivity(intent);
        finish();
    }



    private void loadQuestions() {
        questionList = new ArrayList<>();

        questionList.add(new Question("What is the national costume for Filipino women?", "Terno", "Baro’t Saya", "Kimona", "Malong", "Baro’t Saya", "The Baro’t Saya is the traditional Filipino dress for women, consisting of a blouse (baro) and a skirt (saya). It is considered the national costume. The Terno is a modernized version with butterfly sleeves."));
        questionList.add(new Question("Which province is famous for the Pahiyas Festival?", "Quezon", "Cebu", "Iloilo", "Baguio", "Quezon", "The Pahiyas Festival is celebrated in Lucban, Quezon, every May 15 in honor of San Isidro Labrador, the patron saint of farmers."));
        questionList.add(new Question("What is the national dance of the Philippines?", "Tinikling", "Pandanggo sa Ilaw", "Itik-itik", "Cariñosa", "Tinikling", "Tinikling is the national dance of the Philippines, where dancers step in and out of bamboo poles in rhythmic movements, mimicking the movement of tikling birds."));
        questionList.add(new Question("Which indigenous script was used in pre-colonial Philippines?", "Alibata", "Baybayin", "Kulitan", "Surat Mangyan", "Baybayin", "Baybayin is an ancient pre-colonial writing system used by early Filipinos before the arrival of the Spaniards."));
        questionList.add(new Question("Who composed the Philippine National Anthem?", "Julian Felipe", "Jose Palma", "Levi Celerio", "Francisco Balagtas", "Julian Felipe", "Julian Felipe composed the music for Lupang Hinirang, while Jose Palma wrote the lyrics."));
        questionList.add(new Question("What is the traditional headgear of Muslim Filipinos?", "Tapis", "T’boli Headdress", "Pis Siyabit", "Salakot", "Pis Siyabit", "Pis Siyabit is a woven headscarf worn by the Tausug people of Mindanao."));
        questionList.add(new Question("Which festival celebrates the fertility of the land and is famous for its giant floral floats?", "Panagbenga Festival", "MassKara Festival", "Ati-Atihan Festival", "Sinulog Festival", "Panagbenga Festival", "Panagbenga Festival is the flower festival in Baguio City, celebrated in February with floral floats and street dancing."));
        questionList.add(new Question("What is the name of the Filipino bamboo instrument played by striking it with a stick?", "Bandurria", "Kulintang", "Kalutang", "Gambang", "Kalutang", "Kalutang is a percussion instrument made of two bamboo sticks, played by striking them together."));
        questionList.add(new Question("Which traditional tattoo artist from Kalinga is famous worldwide?", "Apo Wang-od", "Lang Dulay", "Haja Amina Appi", "Kidlat Tahimik", "Apo Wang-od", "Apo Wang-od is the last mambabatok (traditional tattoo artist) of the Kalinga tribe."));
        questionList.add(new Question("What is the term for a communal rice-planting ritual dance in the Cordillera region?", "Hudhud", "Salip", "Bendian", "Talip", "Talip", "Talip is a dance performed by the Ifugao people to celebrate planting and harvesting seasons."));
        questionList.add(new Question("Which Visayan epic tells the story of the hero Labaw Donggon?", "Biag ni Lam-ang", "Ibalon", "Hinilawod", "Darangen", "Hinilawod", "Hinilawod is a Visayan epic poem about the adventures of Labaw Donggon, a hero with supernatural strength."));
        questionList.add(new Question("What is the famous rock fortress in Palawan, home to ancient inscriptions?", "Mt. Apo", "Kabayan Caves", "Tabon Caves", "Callao Cave", "Tabon Caves", "The Tabon Caves in Palawan are known as the 'Cradle of Civilization' in the Philippines, with ancient artifacts and inscriptions."));
        questionList.add(new Question("Which type of embroidery from Taal, Batangas, is made with fine pineapple fiber?", "Calado", "Pina Weaving", "Jusi Embroidery", "Banig Weaving", "Calado", "Calado is a delicate hand-embroidery technique using fine pineapple fiber (piña)."));
        questionList.add(new Question("Which National Artist is known for his modernist paintings and murals?", "Benedicto Cabrera", "Fernando Amorsolo", "Vicente Manansala", "Napoleon Abueva", "Vicente Manansala", "Vicente Manansala was a modernist painter known for his cubist style in painting Filipino subjects."));
        questionList.add(new Question("Which province is famous for making 'banig' mats?", "Samar", "Aklan", "Bohol", "Basilan", "Samar", "Banig mats, woven from dried leaves, are widely produced in Samar."));
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
        dbHelper.clearTTProgress(userId);

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
                .setNegativeButton("Exit", (dialog, which) -> exitToPCAActivity())
                .show();
    }


    private void restartQuiz() {
        currentQuestionIndex = 0;
        elapsedTime = 0;
        startQuiz();
    }

    private void saveUserProgress() {
        dbHelper.saveTTProgress(userId, currentQuestionIndex, elapsedTime);
    }

    private void loadUserProgress() {
        Cursor cursor = dbHelper.getTTProgress(userId);
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
