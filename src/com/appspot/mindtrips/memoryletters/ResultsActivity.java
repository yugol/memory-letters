package com.appspot.mindtrips.memoryletters;

import java.util.Locale;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.appspot.mindtrips.memoryletters.model.persistence.DatabaseAdapter;
import com.appspot.mindtrips.memoryletters.model.persistence.Settings;
import com.appspot.mindtrips.memoryletters.util.ActivityUtil;

public class ResultsActivity extends FragmentActivity implements ActionBar.TabListener {

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(final FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public Fragment getItem(final int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            final Fragment fragment = new TableSectionFragment();
            final Bundle args = new Bundle();
            args.putInt(TableSectionFragment.ARG_SECTION_NUMBER, position);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            final Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_results_small).toUpperCase(l);
                case 1:
                    return getString(R.string.title_results_medium).toUpperCase(l);
                case 2:
                    return getString(R.string.title_results_large).toUpperCase(l);
            }
            return null;
        }
    }

    public static class TableSectionFragment extends Fragment {

        /**
         * The fragment argument representing the section number for this fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";
        private DatabaseAdapter    db;

        public TableSectionFragment() {
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.results_table, container, false);
            final TableLayout ll = (TableLayout) rootView.findViewById(R.id.TableLayoutTopScores);
            final TableRow header = new TableRow(getActivity());
            final TextView h1 = new TextView(getActivity());
            h1.setText(R.string.results_date);
            header.addView(h1);
            final TextView h2 = new TextView(getActivity());
            h2.setText(R.string.results_time);
            header.addView(h2);
            ll.addView(header);

            /* Extract data from DB */
            db = new DatabaseAdapter(getActivity().getApplicationContext());
            db.open();
            final Cursor cursorEN = db.getTopGames('S', db.getNormalDiff());
            cursorEN.moveToFirst();

            final Cursor cursorEH = db.getTopGames(db.getSmallSize(), db.getHighDiff());
            cursorEH.moveToFirst();

            final Cursor cursorMN = db.getTopGames(db.getMediumSize(), db.getNormalDiff());
            cursorMN.moveToFirst();

            final Cursor cursorMH = db.getTopGames(db.getMediumSize(), db.getHighDiff());
            cursorMH.moveToFirst();

            final Cursor cursorLN = db.getTopGames(db.getLargeSize(), db.getNormalDiff());
            cursorLN.moveToFirst();

            final Cursor cursorLH = db.getTopGames(db.getLargeSize(), db.getHighDiff());
            cursorLH.moveToFirst();

            Cursor cursorActual;
            final int N = db.getLimitN();
            boolean italic = false;

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {

                case 0:
                    for (int i = 0, normal = 0, high = 0; i < 2 * N; i++) {
                        if (normal < N && high < N && cursorEN.getCount() > normal && cursorEH.getCount() > high) {
                            if (cursorEN.getLong(1) < cursorEH.getLong(1)) {
                                cursorActual = cursorEN;
                                normal++;
                                italic = false;
                            } else {
                                cursorActual = cursorEH;
                                high++;
                                italic = true;
                            }
                        } else if (normal < N && cursorEN.getCount() > normal) {
                            cursorActual = cursorEN;
                            normal++;
                            italic = false;
                        } else if (high < N && cursorEH.getCount() > high) {
                            cursorActual = cursorEH;
                            high++;
                            italic = true;
                        } else {
                            break;
                        }
                        final TableRow tbrow = new TableRow(getActivity());

                        final TextView tv1 = new TextView(getActivity());
                        tv1.setId(i * cursorActual.getCount());
                        tv1.setText(cursorActual.getString(0) + "   ");
                        if (italic) {
                            tv1.setTypeface(null, Typeface.BOLD_ITALIC);
                        }
                        tbrow.addView(tv1);

                        final TextView tv2 = new TextView(getActivity());
                        tv2.setId(i * cursorActual.getCount() + 1);
                        final float time = cursorActual.getLong(1) / (float) 1000;
                        tv2.setText(String.format("%.2f", time));
                        if (italic) {
                            tv2.setTypeface(null, Typeface.BOLD_ITALIC);
                        }
                        tbrow.addView(tv2);

                        ll.addView(tbrow);
                        cursorActual.moveToNext();
                    }
                    break;

                case 1:
                    for (int i = 0, normal = 0, high = 0; i < 2 * N; i++) {
                        if (normal < N && high < N && cursorMN.getCount() > normal && cursorMH.getCount() > high) {
                            if (cursorMN.getLong(1) < cursorMH.getLong(1)) {
                                cursorActual = cursorMN;
                                normal++;
                                italic = false;
                            } else {
                                cursorActual = cursorMH;
                                high++;
                                italic = true;
                            }
                        } else if (normal < N && cursorMN.getCount() > normal) {
                            cursorActual = cursorMN;
                            normal++;
                            italic = false;
                        } else if (high < N && cursorMH.getCount() > high) {
                            cursorActual = cursorMH;
                            high++;
                            italic = true;
                        } else {
                            break;
                        }
                        final TableRow tbrow = new TableRow(getActivity());

                        final TextView tv1 = new TextView(getActivity());
                        tv1.setId(i * cursorActual.getCount());
                        tv1.setText(cursorActual.getString(0) + "   ");
                        if (italic) {
                            tv1.setTypeface(null, Typeface.BOLD_ITALIC);
                        }
                        tbrow.addView(tv1);

                        final TextView tv2 = new TextView(getActivity());
                        tv2.setId(i * cursorActual.getCount() + 1);
                        final float time = cursorActual.getLong(1) / (float) 1000;
                        tv2.setText(String.format("%.2f", time));
                        if (italic) {
                            tv2.setTypeface(null, Typeface.BOLD_ITALIC);
                        }
                        tbrow.addView(tv2);

                        ll.addView(tbrow);
                        cursorActual.moveToNext();
                    }
                    break;

                case 2:
                    for (int i = 0, normal = 0, high = 0; i < 2 * N; i++) {
                        if (normal < N && high < N && cursorLN.getCount() > normal && cursorLH.getCount() > high) {
                            if (cursorLN.getLong(1) < cursorLH.getLong(1)) {
                                cursorActual = cursorLN;
                                normal++;
                                italic = false;
                            } else {
                                cursorActual = cursorLH;
                                high++;
                                italic = true;
                            }
                        } else if (normal < N && cursorLN.getCount() > normal) {
                            cursorActual = cursorLN;
                            normal++;
                            italic = false;
                        } else if (high < N && cursorLH.getCount() > high) {
                            cursorActual = cursorLH;
                            high++;
                            italic = true;
                        } else {
                            break;
                        }
                        final TableRow tbrow = new TableRow(getActivity());

                        final TextView tv1 = new TextView(getActivity());
                        tv1.setId(i * cursorActual.getCount());
                        tv1.setText(cursorActual.getString(0) + "   ");
                        if (italic) {
                            tv1.setTypeface(null, Typeface.BOLD_ITALIC);
                        }
                        tbrow.addView(tv1);

                        final TextView tv2 = new TextView(getActivity());
                        tv2.setId(i * cursorActual.getCount() + 1);
                        final float time = cursorActual.getLong(1) / (float) 1000;
                        tv2.setText(String.format("%.2f", time));
                        if (italic) {
                            tv2.setTypeface(null, Typeface.BOLD_ITALIC);
                        }
                        tbrow.addView(tv2);

                        ll.addView(tbrow);
                        cursorActual.moveToNext();
                    }
                    break;

            }
            db.close();

            return rootView;
        }
    }

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the sections.
     * We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every loaded fragment in memory.
     * If this becomes too memory intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter rSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager            rViewPager;

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
    public void onTabReselected(final ActionBar.Tab tab, final FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(final ActionBar.Tab tab, final FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        rViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(final ActionBar.Tab tab, final FragmentTransaction fragmentTransaction) {
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        ActivityUtil.setupActionBar(this);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three primary sections of the app.
        rSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        rViewPager = (ViewPager) findViewById(R.id.resultsPager);
        rViewPager.setAdapter(rSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding tab.
        // We can also use ActionBar.Tab#select() to do this if we have a reference to the Tab.
        rViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(final int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < rSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the callback (listener) for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(rSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        final Settings settings = Settings.getInstance(this);
        int currentItem = 0;
        switch (settings.getBoardSize()) {
            case LARGE:
                currentItem = 2;
                break;
            case SMALL:
                currentItem = 0;
                break;
            default:
                currentItem = 1;
                break;
        }
        rViewPager.setCurrentItem(currentItem);
    }

}
