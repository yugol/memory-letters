package com.appspot.mindtrips.memoryletters;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.RadioGroup;
import com.appspot.mindtrips.memoryletters.model.BoardSize;
import com.appspot.mindtrips.memoryletters.model.Difficulty;
import com.appspot.mindtrips.memoryletters.model.persistence.Settings;
import com.appspot.mindtrips.memoryletters.util.ActivityUtil;

public class SettingsActivity extends Activity {

    private Settings settings;

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ActivityUtil.setupActionBar(this);

        settings = Settings.getInstance(this);

        final RadioGroup boardSizeGroup = (RadioGroup) findViewById(R.id.boardSizeGroup);
        switch (settings.getBoardSize()) {
            case LARGE:
                boardSizeGroup.check(R.id.rbBoardSizeLarge);
                break;
            case MEDIUM:
                boardSizeGroup.check(R.id.rbBoardSizeMedium);
                break;
            case SMALL:
                boardSizeGroup.check(R.id.rbBoardSizeSmall);
                break;
        }
        boardSizeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(final RadioGroup group, final int checkedId) {
                switch (checkedId) {
                    case R.id.rbBoardSizeLarge:
                        settings.setBoardSize(BoardSize.LARGE);
                        break;
                    case R.id.rbBoardSizeMedium:
                        settings.setBoardSize(BoardSize.MEDIUM);
                        break;
                    case R.id.rbBoardSizeSmall:
                        settings.setBoardSize(BoardSize.SMALL);
                        break;
                }
            }

        });

        final RadioGroup difficultyGroup = (RadioGroup) findViewById(R.id.difficultyGroup);
        switch (settings.getDifficulty()) {
            case NORMAL:
                difficultyGroup.check(R.id.rbDifficultyNormal);
                break;
            case HIGH:
                difficultyGroup.check(R.id.rbDifficultyHigh);
                break;
        }
        difficultyGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(final RadioGroup group, final int checkedId) {
                switch (checkedId) {
                    case R.id.rbDifficultyNormal:
                        settings.setDifficulty(Difficulty.NORMAL);
                        break;
                    case R.id.rbDifficultyHigh:
                        settings.setDifficulty(Difficulty.HIGH);
                        break;
                }
            }

        });
    }

}
