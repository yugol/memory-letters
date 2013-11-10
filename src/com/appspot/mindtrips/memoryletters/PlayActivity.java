package com.appspot.mindtrips.memoryletters;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import com.appspot.mindtrips.memoryletters.model.Board;
import com.appspot.mindtrips.memoryletters.model.Board.Tile;
import com.appspot.mindtrips.memoryletters.model.Board.Tile.State;
import com.appspot.mindtrips.memoryletters.model.Difficulty;
import com.appspot.mindtrips.memoryletters.model.persistence.DatabaseAdapter;
import com.appspot.mindtrips.memoryletters.model.persistence.Settings;

public class PlayActivity extends Activity {

    private DatabaseAdapter  db;

    private static final int TILE_MARGIN_SIZE = 2;
    private static final int BOARD_MARGINSIZE = 3;

    private Settings         settings;
    private Board            board;
    private Button[]         boardTiles;
    private final List<Tile> frontTiles       = new ArrayList<Tile>();
    private Calendar         startTime;
    private Calendar         endTime;
    private long             deltaMillis;

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        fit();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createUiBoard() {
        final TableRow.LayoutParams tileMargin = new TableRow.LayoutParams();
        tileMargin.setMargins(TILE_MARGIN_SIZE, TILE_MARGIN_SIZE, TILE_MARGIN_SIZE, TILE_MARGIN_SIZE);

        final TableLayout boardMatrix = (TableLayout) findViewById(R.id.boardMatrix);
        boardMatrix.removeAllViews();
        boardTiles = new Button[board.getTileCount()];
        for (int row = 0; row < board.getRowCount(); ++row) {
            final TableRow boardRow = new TableRow(this);
            for (int col = 0; col < board.getColumnCount(); ++col) {
                final Tile tile = board.getTile(row, col);
                final Button boardTile = new Button(this);
                boardTile.setTag(tile.getIndex());
                // boardTile.setBackgroundResource(R.layout.tile_button);
                boardTile.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View v) {
                        final Button boardTile = (Button) v;
                        final Tile tile = board.getTile((Integer) boardTile.getTag());
                        onTapTile(boardTile, tile);
                    }

                });
                boardRow.addView(boardTile, tileMargin);
                boardTiles[tile.getIndex()] = boardTile;
            }
            boardMatrix.addView(boardRow);
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    private void fit() {
        final int buttonCount = Math.max(board.getRowCount(), board.getColumnCount());
        final Point size = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            getWindowManager().getDefaultDisplay().getSize(size);
        } else {
            size.x = getWindowManager().getDefaultDisplay().getWidth();
            size.y = getWindowManager().getDefaultDisplay().getHeight();
        }
        final int boardSize = Math.min(size.x, size.y) - 2 * (BOARD_MARGINSIZE + buttonCount * TILE_MARGIN_SIZE);
        final int buttonSize = boardSize / buttonCount;
        final int fontSize = 2 * buttonSize / 3;
        final TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(buttonSize, buttonSize);
        for (final Button boardTile : boardTiles) {
            boardTile.setLayoutParams(layoutParams);
            boardTile.setGravity(Gravity.CENTER);
            boardTile.setPadding(0, 0, 0, 0);
            boardTile.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
        }
    }

    private void hideTile(final Tile tile) {
        tile.setState(State.HIDDEN);

        final Button boardTile = boardTiles[tile.getIndex()];
        final Animation spinAnim = AnimationUtils.loadAnimation(this, R.anim.shrink);
        spinAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(final Animation animation) {
                boardTile.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(final Animation animation) {
            }

            @Override
            public void onAnimationStart(final Animation animation) {
            }

        });
        boardTile.startAnimation(spinAnim);
    }

    private void swapTile(final Tile tile) {
        String temp = "";
        if (tile.getState() == State.BACK) {
            tile.setState(State.FRONT);
            temp = String.valueOf(tile.getContent());
        } else {
            tile.setState(State.BACK);
        }

        final Button boardTile = boardTiles[tile.getIndex()];
        final String content = temp;
        final Animation spinAnim = AnimationUtils.loadAnimation(this, R.anim.spin);
        spinAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(final Animation animation) {
                boardTile.setText(content);
            }

            @Override
            public void onAnimationRepeat(final Animation animation) {
            }

            @Override
            public void onAnimationStart(final Animation animation) {
            }

        });
        boardTile.startAnimation(spinAnim);
    }

    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_play);

        final View contentView = findViewById(R.id.fullscreen_content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            contentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }

        settings = Settings.getInstance(this);
        board = new Board(
                settings.getRowCount(),
                settings.getColumnCount(),
                settings.getDifficulty());
        createUiBoard();
        fit();
    }

    protected void onTapTile(final Button boardTile, final Tile tile) {
        if (endTime != null) { return; }
        if (startTime == null) {
            startTime = Calendar.getInstance();
        }
        if (frontTiles.size() == 0) {
            swapTile(tile);
            frontTiles.add(tile);
        } else if (frontTiles.size() == 1) {
            swapTile(tile);
            if (tile.getIndex() == frontTiles.get(0).getIndex()) { // same tile
                frontTiles.clear();
            } else {
                frontTiles.add(tile);
                if (frontTiles.get(0).getContent() == frontTiles.get(1).getContent()) {
                    hideTile(frontTiles.get(0));
                    hideTile(frontTiles.get(1));
                    frontTiles.clear();
                    if (board.isSolved()) {
                        endTime = Calendar.getInstance();
                        deltaMillis = endTime.getTimeInMillis() - startTime.getTimeInMillis();
                        // Save in DB
                        db = new DatabaseAdapter(getApplicationContext());
                        db.open();
                        db.insertGame(deltaMillis, settings.getBoardSize().name().charAt(0), settings.getDifficulty().equals(Difficulty.NORMAL) ? 0 : 1);
                        db.close();

                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                        alertDialogBuilder.setMessage(getResources().getString(R.string.board_solved_message, deltaMillis / 1000.));
                        alertDialogBuilder.setCancelable(false);
                        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(final DialogInterface dialog, final int which) {
                                final Intent intent = new Intent(PlayActivity.this, ResultsActivity.class);
                                startActivity(intent);
                                for (final Button tmpTile : boardTiles) {
                                    tmpTile.setText(String.valueOf(board.getTile((Integer) tmpTile.getTag()).getContent()));
                                    tmpTile.setVisibility(View.VISIBLE);
                                }
                            }

                        });
                        final AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }
            }
        } else { // frontTiles.size() == 2
            if (tile.getIndex() == frontTiles.get(0).getIndex()) { // same as first tile
                swapTile(frontTiles.get(0));
                frontTiles.remove(0);
                return;
            }
            if (tile.getIndex() == frontTiles.get(1).getIndex()) { // same as first tile
                swapTile(frontTiles.get(1));
                frontTiles.remove(1);
                return;
            }
            swapTile(frontTiles.get(0));
            swapTile(frontTiles.get(1));
            swapTile(tile);
            frontTiles.clear();
            frontTiles.add(tile);
        }
    }

}
