package com.appspot.mindtrips.memoryletters.util;

import android.app.Activity;
import android.os.Build;

public final class ActivityUtil {

    public static void setupActionBar(final Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            activity.getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

}
