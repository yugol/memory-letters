package com.appspot.mindtrips.memoryletters.model.persistence;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.appspot.mindtrips.memoryletters.model.BoardSize;
import com.appspot.mindtrips.memoryletters.model.Difficulty;

public final class Settings {

    private static final String      ML_PREFS   = Settings.class.getName() + ".SHARED_PREFERENCES";
    private static final String      BOARD_SIZE = "boardSize";
    private static final String      DIFFICULTY = "difficulty";

    private static Settings          instance;
    private static SharedPreferences sharedPreferences;

    public static Settings getInstance(final Activity activity) {
        if (instance == null) {
            instance = new Settings();
            sharedPreferences = activity.getSharedPreferences(ML_PREFS, Context.MODE_PRIVATE);
            instance.boardSize = BoardSize.valueOf(sharedPreferences.getString(BOARD_SIZE, BoardSize.MEDIUM.toString()));
            instance.difficulty = Difficulty.valueOf(sharedPreferences.getString(DIFFICULTY, Difficulty.NORMAL.toString()));
        }
        return instance;
    }

    private BoardSize  boardSize;
    private Difficulty difficulty;

    private Settings() {
    }

    public BoardSize getBoardSize() {
        return boardSize;
    }

    public int getColumnCount() {
        return boardSize.cols;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public int getRowCount() {
        return boardSize.rows;
    }

    public void setBoardSize(final BoardSize boardSize) {
        this.boardSize = boardSize;
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(BOARD_SIZE, boardSize.toString());
        editor.apply();
    }

    public void setDifficulty(final Difficulty difficulty) {
        this.difficulty = difficulty;
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DIFFICULTY, difficulty.toString());
        editor.apply();
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

}
