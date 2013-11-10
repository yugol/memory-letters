package com.appspot.mindtrips.memoryletters.model.persistence;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Activity to handle local database
 */
public class DatabaseAdapter {

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(final Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(final SQLiteDatabase arg0) {
            createTable(arg0);
        }

        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + GAMES_TABLE);
            createTable(db);
        }

        /**
         * Create three tables for users, games and boards.
         * 
         * @param db
         */
        private void createTable(final SQLiteDatabase db) {
            final String query = "CREATE TABLE " + GAMES_TABLE + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DATE + " TEXT," +
                    TIME + " INTEGER," +
                    SIZE + " TEXT," +
                    DIFF + " INTEGER);";

            try {
                db.execSQL(query);
            } catch (final SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static final String   ID               = "ID";
    public static final String   DATE             = "DATE";
    public static final String   TIME             = "TIME";
    public static final String   SIZE             = "SIZE";

    public static final String   DIFF             = "DIFF";
    private static final int     limit_N          = 5;
    private static final char    S_BOARD          = 'S';
    private static final char    M_BOARD          = 'M';

    private static final char    L_BOARD          = 'L';
    private static final int     N_DIFF           = 0;

    private static final int     H_DIFF           = 1;
    private static final String  DATABASE_NAME    = "memoryLetters.db";
    private static final int     DATABASE_VERSION = 1;

    private static final String  GAMES_TABLE      = "games";
    private final Context        context;
    private final DatabaseHelper helper;

    private SQLiteDatabase       db;

    /**
     * Constructor
     * 
     * @param context
     */
    public DatabaseAdapter(final Context context) {
        this.context = context;
        helper = new DatabaseHelper(context);
    }

    public void close() {
        helper.close();
    }

    /* --- Getter required --- */
    public Context getContext() {
        return context;
    }

    //----------Methods for games table ------------

    public int getHighDiff() {
        return H_DIFF;
    }

    public char getLargeSize() {
        return L_BOARD;
    }

    public int getLimitN() {
        return limit_N;
    }

    /* --- Getters --- */

    public char getMediumSize() {
        return M_BOARD;
    }

    public int getNormalDiff() {
        return N_DIFF;
    }

    public char getSmallSize() {
        return S_BOARD;
    }

    /**
     * Obtains all games in DB
     * 
     * @return
     */
    public Cursor getTopGames(final char size, final int diff) {
        final Cursor cursor = db.query(GAMES_TABLE, new String[] { DATE, TIME }, SIZE + "=\"" + String.valueOf(size) + "\" AND " + DIFF + "=" + String.valueOf(diff), null, null, null, TIME + " ASC", String.valueOf(limit_N));
        return cursor;
    }

    public long insertGame(final long time, final char size, final int diff) {
        final ContentValues values = new ContentValues();
        values.put(DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        values.put(TIME, (int) time);
        values.put(SIZE, String.valueOf(size));
        values.put(DIFF, diff);
        return db.insert(GAMES_TABLE, null, values);
    }

    public DatabaseAdapter open() throws SQLException {
        db = helper.getWritableDatabase();
        return this;
    }

}