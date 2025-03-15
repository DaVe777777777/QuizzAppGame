package com.example.game;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "quiz_game.db";
    private static final int DATABASE_VERSION = 5;

    private static final String TABLE_TT_PROGRESS = "progress_table";
    private static final String TABLE_FM_PROGRESS = "fm_progress_table";
    private static final String TABLE_SM_PROGRESS = "sm_progress_table";

    private static final String TABLE_PHNI_TT_PROGRESS = "phni_tt_progress_table";
    private static final String TABLE_PHNI_FM_PROGRESS = "phni_fm_progress_table";
    private static final String TABLE_PHNI_SM_PROGRESS = "phni_sm_progress_table";

    private static final String TABLE_PGNW_TT_PROGRESS = "pgnw_tt_progress_table";
    private static final String TABLE_PGNW_FM_PROGRESS = "pgnw_fm_progress_table";
    private static final String TABLE_PGNW_SM_PROGRESS = "pgnw_sm_progress_table";

    private static final String TABLE_PGS_TT_PROGRESS = "pgs_tt_progress_table";
    private static final String TABLE_PGS_FM_PROGRESS = "pgs_fm_progress_table";
    private static final String TABLE_PGS_SM_PROGRESS = "pgs_sm_progress_table";

    private static final String TABLE_PLD_TT_PROGRESS = "pld_tt_progress_table";
    private static final String TABLE_PLD_FM_PROGRESS = "pld_fm_progress_table";
    private static final String TABLE_PLD_SM_PROGRESS = "pld_sm_progress_table";

    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_QUESTION_INDEX = "question_index";
    private static final String COLUMN_ELAPSED_TIME = "elapsed_time";
    private static final String COLUMN_TIME_REMAINING = "time_remaining";
    private static final String COLUMN_LIVES = "lives";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTTTableQuery = "CREATE TABLE " + TABLE_TT_PROGRESS + " (" +
                COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                COLUMN_QUESTION_INDEX + " INTEGER, " +
                COLUMN_ELAPSED_TIME + " INTEGER);";
        db.execSQL(createTTTableQuery);

        String createFMTableQuery = "CREATE TABLE " + TABLE_FM_PROGRESS + " (" +
                COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                COLUMN_QUESTION_INDEX + " INTEGER, " +
                COLUMN_TIME_REMAINING + " INTEGER DEFAULT 0);";
        db.execSQL(createFMTableQuery);

        String createSMTableQuery = "CREATE TABLE " + TABLE_SM_PROGRESS + " (" +
                COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                COLUMN_QUESTION_INDEX + " INTEGER, " +
                COLUMN_LIVES + " INTEGER DEFAULT 3);";
        db.execSQL(createSMTableQuery);

        String createPHNITTTableQuery = "CREATE TABLE " + TABLE_PHNI_TT_PROGRESS + " (" +
                COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                COLUMN_QUESTION_INDEX + " INTEGER, " +
                COLUMN_ELAPSED_TIME + " INTEGER);";
        db.execSQL(createPHNITTTableQuery);

        String createPHNIFMTableQuery = "CREATE TABLE " + TABLE_PHNI_FM_PROGRESS + " (" +
                COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                COLUMN_QUESTION_INDEX + " INTEGER, " +
                COLUMN_TIME_REMAINING + " INTEGER DEFAULT 0);";
        db.execSQL(createPHNIFMTableQuery);

        String createPHNISMTableQuery = "CREATE TABLE " + TABLE_PHNI_SM_PROGRESS + " (" +
                COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                COLUMN_QUESTION_INDEX + " INTEGER, " +
                COLUMN_LIVES + " INTEGER DEFAULT 3);";
        db.execSQL(createPHNISMTableQuery);

        String createPGNWTTTableQuery = "CREATE TABLE " + TABLE_PGNW_TT_PROGRESS + " (" +
                COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                COLUMN_QUESTION_INDEX + " INTEGER, " +
                COLUMN_ELAPSED_TIME + " INTEGER);";
        db.execSQL(createPGNWTTTableQuery);

        String createPGNWFMTableQuery = "CREATE TABLE " + TABLE_PGNW_FM_PROGRESS + " (" +
                COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                COLUMN_QUESTION_INDEX + " INTEGER, " +
                COLUMN_TIME_REMAINING + " INTEGER DEFAULT 0);";
        db.execSQL(createPGNWFMTableQuery);

        String createPGNWSMTableQuery = "CREATE TABLE " + TABLE_PGNW_SM_PROGRESS + " (" +
                COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                COLUMN_QUESTION_INDEX + " INTEGER, " +
                COLUMN_LIVES + " INTEGER DEFAULT 3);";
        db.execSQL(createPGNWSMTableQuery);

        String createPGSTTTableQuery = "CREATE TABLE " + TABLE_PGS_TT_PROGRESS + " (" +
                COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                COLUMN_QUESTION_INDEX + " INTEGER, " +
                COLUMN_ELAPSED_TIME + " INTEGER);";
        db.execSQL(createPGSTTTableQuery);

        String createPGSFMTableQuery = "CREATE TABLE " + TABLE_PGS_FM_PROGRESS + " (" +
                COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                COLUMN_QUESTION_INDEX + " INTEGER, " +
                COLUMN_TIME_REMAINING + " INTEGER DEFAULT 0);";
        db.execSQL(createPGSFMTableQuery);

        String createPGSSMTableQuery = "CREATE TABLE " + TABLE_PGS_SM_PROGRESS + " (" +
                COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                COLUMN_QUESTION_INDEX + " INTEGER, " +
                COLUMN_LIVES + " INTEGER DEFAULT 3);";
        db.execSQL(createPGSSMTableQuery);

        String createPLDTTTableQuery = "CREATE TABLE " + TABLE_PLD_TT_PROGRESS + " (" +
                COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                COLUMN_QUESTION_INDEX + " INTEGER, " +
                COLUMN_ELAPSED_TIME + " INTEGER);";
        db.execSQL(createPLDTTTableQuery);

        String createPLDFMTableQuery = "CREATE TABLE " + TABLE_PLD_FM_PROGRESS + " (" +
                COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                COLUMN_QUESTION_INDEX + " INTEGER, " +
                COLUMN_TIME_REMAINING + " INTEGER DEFAULT 0);";
        db.execSQL(createPLDFMTableQuery);

        String createPLDSMTableQuery = "CREATE TABLE " + TABLE_PLD_SM_PROGRESS + " (" +
                COLUMN_USER_ID + " TEXT PRIMARY KEY, " +
                COLUMN_QUESTION_INDEX + " INTEGER, " +
                COLUMN_LIVES + " INTEGER DEFAULT 3);";
        db.execSQL(createPLDSMTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TT_PROGRESS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_FM_PROGRESS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SM_PROGRESS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHNI_TT_PROGRESS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHNI_FM_PROGRESS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHNI_SM_PROGRESS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PGNW_TT_PROGRESS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PGNW_FM_PROGRESS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PGNW_SM_PROGRESS);

            onCreate(db); // Recreate all tables
        }
    }

    //     Save TT Progress (Only 3 parameters)
    public void saveTTProgress(String userId, int questionIndex, long elapsedTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_QUESTION_INDEX, questionIndex);
        values.put(COLUMN_ELAPSED_TIME, elapsedTime);

        db.insertWithOnConflict(TABLE_TT_PROGRESS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void savePHNITTProgress(String userId, int questionIndex, long elapsedTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_QUESTION_INDEX, questionIndex);
        values.put(COLUMN_ELAPSED_TIME, elapsedTime);

        db.insertWithOnConflict(TABLE_PHNI_TT_PROGRESS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void savePGSTTProgress(String userId, int questionIndex, long elapsedTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_QUESTION_INDEX, questionIndex);
        values.put(COLUMN_ELAPSED_TIME, elapsedTime);

        db.insertWithOnConflict(TABLE_PGS_TT_PROGRESS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void savePLDTTProgress(String userId, int questionIndex, long elapsedTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_QUESTION_INDEX, questionIndex);
        values.put(COLUMN_ELAPSED_TIME, elapsedTime);

        db.insertWithOnConflict(TABLE_PLD_TT_PROGRESS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void savePGNWTTProgress(String userId, int questionIndex, long elapsedTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_QUESTION_INDEX, questionIndex);
        values.put(COLUMN_ELAPSED_TIME, elapsedTime);

        db.insertWithOnConflict(TABLE_PGNW_TT_PROGRESS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // Save FM Progress (3 parameters, timeRemaining is included)
    public void savePHNIFMProgress(String userId, int questionIndex, int timeRemaining) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_QUESTION_INDEX, questionIndex);
        values.put(COLUMN_TIME_REMAINING, timeRemaining);

        db.insertWithOnConflict(TABLE_PHNI_FM_PROGRESS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void savePGSFMProgress(String userId, int questionIndex, int timeRemaining) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_QUESTION_INDEX, questionIndex);
        values.put(COLUMN_TIME_REMAINING, timeRemaining);

        db.insertWithOnConflict(TABLE_PGS_FM_PROGRESS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void savePLDFMProgress(String userId, int questionIndex, int timeRemaining) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_QUESTION_INDEX, questionIndex);
        values.put(COLUMN_TIME_REMAINING, timeRemaining);

        db.insertWithOnConflict(TABLE_PLD_FM_PROGRESS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void savePGNWFMProgress(String userId, int questionIndex, int timeRemaining) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_QUESTION_INDEX, questionIndex);
        values.put(COLUMN_TIME_REMAINING, timeRemaining);

        db.insertWithOnConflict(TABLE_PGNW_FM_PROGRESS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void saveFMProgress(String userId, int questionIndex, int timeRemaining) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_QUESTION_INDEX, questionIndex);
        values.put(COLUMN_TIME_REMAINING, timeRemaining);

        db.insertWithOnConflict(TABLE_FM_PROGRESS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // Save SM Progress (3 parameters, lives is included)
    public void savePHNISMProgress(String userId, int questionIndex, int lives) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_QUESTION_INDEX, questionIndex);
        values.put(COLUMN_LIVES, lives);

        db.insertWithOnConflict(TABLE_PHNI_SM_PROGRESS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void savePGNWSMProgress(String userId, int questionIndex, int lives) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_QUESTION_INDEX, questionIndex);
        values.put(COLUMN_LIVES, lives);

        db.insertWithOnConflict(TABLE_PGNW_SM_PROGRESS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void savePGSSMProgress(String userId, int questionIndex, int lives) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_QUESTION_INDEX, questionIndex);
        values.put(COLUMN_LIVES, lives);

        db.insertWithOnConflict(TABLE_PGS_SM_PROGRESS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void savePLDSMProgress(String userId, int questionIndex, int lives) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_QUESTION_INDEX, questionIndex);
        values.put(COLUMN_LIVES, lives);

        db.insertWithOnConflict(TABLE_PLD_SM_PROGRESS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void saveSMProgress(String userId, int questionIndex, int lives) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_QUESTION_INDEX, questionIndex);
        values.put(COLUMN_LIVES, lives);

        db.insertWithOnConflict(TABLE_SM_PROGRESS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void clearTTProgress(String userId) {
        clearProgress(TABLE_TT_PROGRESS, userId);
    }
    public void clearPHNITTProgress(String userId) {
        clearProgress(TABLE_PHNI_TT_PROGRESS, userId);
    }
    public void clearPGNWTTProgress(String userId) {
        clearProgress(TABLE_PGNW_TT_PROGRESS, userId);
    }
    public void clearPGSTTProgress(String userId) {
        clearProgress(TABLE_PGS_TT_PROGRESS, userId);
    }
    public void clearPLDTTProgress(String userId) {
        clearProgress(TABLE_PLD_TT_PROGRESS, userId);
    }

    public void clearFMProgress(String userId) {
        clearProgress(TABLE_FM_PROGRESS, userId);
    }
    public void clearPHNIFMProgress(String userId) {
        clearProgress(TABLE_PHNI_FM_PROGRESS, userId);
    }
    public void clearPGNWFMProgress(String userId) {
        clearProgress(TABLE_PGNW_FM_PROGRESS, userId);
    }
    public void clearPGSFMProgress(String userId) {
        clearProgress(TABLE_PGS_FM_PROGRESS, userId);
    }
    public void clearPLDFMProgress(String userId) {
        clearProgress(TABLE_PLD_FM_PROGRESS, userId);
    }



    public void clearSMProgress(String userId) {
        clearProgress(TABLE_SM_PROGRESS, userId);
    }
    public void clearPHNISMProgress(String userId) {
        clearProgress(TABLE_PHNI_SM_PROGRESS, userId);
    }
    public void clearPGNWSMProgress(String userId) {
        clearProgress(TABLE_PGNW_SM_PROGRESS, userId);
    }
    public void clearPGSSMProgress(String userId) {
        clearProgress(TABLE_PGS_SM_PROGRESS, userId);
    }
    public void clearPLDSMProgress(String userId) {
        clearProgress(TABLE_PLD_SM_PROGRESS, userId);
    }

    private void clearProgress(String tableName, String userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tableName, COLUMN_USER_ID + "=?", new String[]{userId});
        db.close();
    }

    public Cursor getTTProgress(String userId) {
        return getProgress(TABLE_TT_PROGRESS, userId);
    }
    public Cursor getPHNITTProgress(String userId) {
        return getProgress(TABLE_PHNI_TT_PROGRESS, userId);
    }
    public Cursor getPGNWTTProgress(String userId) {
        return getProgress(TABLE_PGNW_TT_PROGRESS, userId);
    }
    public Cursor getPGSTTProgress(String userId) {
        return getProgress(TABLE_PGS_TT_PROGRESS, userId);
    }
    public Cursor getPLDTTProgress(String userId) {
        return getProgress(TABLE_PLD_TT_PROGRESS, userId);
    }

    public Cursor getFMProgress(String userId) {
        return getProgress(TABLE_FM_PROGRESS, userId);
    }
    public Cursor getPHNIFMProgress(String userId) {
        return getProgress(TABLE_PHNI_FM_PROGRESS, userId);
    }
    public Cursor getPGNWFMProgress(String userId) {
        return getProgress(TABLE_PGNW_FM_PROGRESS, userId);
    }
    public Cursor getPGSFMProgress(String userId) {
        return getProgress(TABLE_PGS_FM_PROGRESS, userId);
    }
    public Cursor getPLDFMProgress(String userId) {
        return getProgress(TABLE_PLD_FM_PROGRESS, userId);
    }


    public Cursor getSMProgress(String userId) {
        return getProgress(TABLE_SM_PROGRESS, userId);
    }
    public Cursor getPHNISMProgress(String userId) {
        return getProgress(TABLE_PHNI_SM_PROGRESS, userId);
    }
    public Cursor getPGNWSMProgress(String userId) {
        return getProgress(TABLE_PGNW_SM_PROGRESS, userId);
    }
    public Cursor getPGSSMProgress(String userId) {
        return getProgress(TABLE_PGS_SM_PROGRESS, userId);
    }
    public Cursor getPLDSMProgress(String userId) {
        return getProgress(TABLE_PLD_SM_PROGRESS, userId);
    }


    private Cursor getProgress(String tableName, String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + tableName + " WHERE " + COLUMN_USER_ID + "=?", new String[]{userId});
    }
}
