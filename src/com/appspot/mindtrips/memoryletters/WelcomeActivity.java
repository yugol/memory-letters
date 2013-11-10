package com.appspot.mindtrips.memoryletters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class WelcomeActivity extends Activity {

    public void onClickAbout(final View view) {
        final Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void onClickResults(final View view) {
        final Intent intent = new Intent(this, ResultsActivity.class);
        startActivity(intent);
    }

    public void onClickSettings(final View view) {
        final Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void onClickStartNewRound(final View view) {
        final Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.welcome, menu);
        return true;
    }

    public void onQuit(final MenuItem item) {
        finish();
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

}
