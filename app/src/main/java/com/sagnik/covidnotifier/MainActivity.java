package com.sagnik.covidnotifier;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.work.WorkManager;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.card.MaterialCardView;
import com.sagnik.covidnotifier.androidservices.CovidDataAndroidService;
import com.sagnik.covidnotifier.loaders.DataLoader;
import com.sagnik.covidnotifier.models.CovidData;
import com.sagnik.covidnotifier.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Map<String, CovidData.Statewise>> {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    private static final int LOAD_DATA = 0;

    private LinearLayout scrollViewLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scrollViewLayout = findViewById(R.id.scroll_view_layout);

        try {
            WorkManager.getInstance(this).cancelAllWorkByTag("covid-worker-task").getResult().get();
        } catch (ExecutionException | InterruptedException e) {
            logger.log(Level.SEVERE, "Exception while canelling worker task", e);
        }

        addTextToScrollViewLayout("Loading Data...");
        LoaderManager.getInstance(this).restartLoader(LOAD_DATA, new Bundle(), this);

//        WorkManager.getInstance(this).enqueue(new OneTimeWorkRequest.Builder(SimpleWorker.class)
//                .addTag("covid-worker-task")
//                .setInitialDelay(5, TimeUnit.SECONDS)
//                .build());

        if (!isMyServiceRunning(CovidDataAndroidService.class)) {
            startForegroundService(new Intent(getApplicationContext(), CovidDataAndroidService.class));
        }

    }

    @NonNull
    @Override
    public Loader<Map<String, CovidData.Statewise>> onCreateLoader(int id, @Nullable Bundle args) {
        if (id == LOAD_DATA) {
            return new DataLoader(this);
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Map<String, CovidData.Statewise>> loader, Map<String, CovidData.Statewise> data) {
        scrollViewLayout.removeAllViews();
        if (data == null || data.size() == 0) {
            addTextToScrollViewLayout("Data unavailable");
            return;
        }
        CovidData.Statewise total = data.get("Total");

        addStatewise(total, true);
        data.values().stream().filter(statewise -> !"Total".equals(statewise.state))
                .sorted((a, b) -> {
                    if (a.active > b.active) return -1;
                    else if (a.active < b.active) return 1;
                    else return 0;
                }).forEach(statewise -> addStatewise(statewise, false));
    }

    private void addStatewise(CovidData.Statewise statewise, boolean special) {
        List<String> contents = new ArrayList<>();
        contents.add("Active: "+ Utils.formatNumber(statewise.active));
        contents.add("Confirmed: "+ Utils.formatNumber(statewise.confirmed) + " (" + Utils.formatNumber(statewise.deltaconfirmed, true) + ")");
        contents.add("Recovered: "+ Utils.formatNumber(statewise.recovered) + " (" + Utils.formatNumber(statewise.deltarecovered, true) + ")");
        contents.add("Deceased: "+ Utils.formatNumber(statewise.deaths) + " (" + Utils.formatNumber(statewise.deltadeaths, true) + ")");

        addCard(statewise.state, contents, special);
    }

    private void addCard(String heading, Collection<String> otherContents, boolean special) {
        final float scale = this.getResources().getDisplayMetrics().density;

        int dp10InPx = (int) (10 * scale);

        MaterialCardView materialCardView = new MaterialCardView(this);
        if (special) {
            materialCardView.setCardBackgroundColor(Color.parseColor("#fffaf5"));
        } else {
            materialCardView.setCardBackgroundColor(Color.parseColor("#f5f9ff"));
        }
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(dp10InPx, dp10InPx, dp10InPx, dp10InPx);
        materialCardView.setLayoutParams(layoutParams);
        materialCardView.setCardElevation(dp10InPx);

        FlexboxLayout flexboxLayout = new FlexboxLayout(this);
        flexboxLayout.setLayoutParams(layoutParams);
        flexboxLayout.setFlexWrap(FlexWrap.WRAP);

        TextView textView = new TextView(this);
        ViewGroup.MarginLayoutParams layoutParams2 = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams2);
        textView.setText(heading);
        TypedValue textAppearanceHeadline6 = new TypedValue();
        getTheme().resolveAttribute(R.attr.textAppearanceHeadline6, textAppearanceHeadline6, true);
        textView.setTextAppearance(textAppearanceHeadline6.data);
        flexboxLayout.addView(textView);

        otherContents.forEach(text -> {
            TextView textView2 = new TextView(this);
            ViewGroup.MarginLayoutParams layoutParams3 = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams3.setMargins(0, (int) (8 * scale), (int) (12 * scale), 0);
            textView2.setLayoutParams(layoutParams3);
            textView2.setText(text);
            TypedValue textAppearanceBody2 = new TypedValue();
            getTheme().resolveAttribute(R.attr.textAppearanceBody2, textAppearanceBody2, true);
            textView2.setTextAppearance(textAppearanceBody2.data);

            TypedArray a = obtainStyledAttributes(new int[] { android.R.attr.textColorSecondary });
            ColorStateList color = a.getColorStateList(a.getIndex(0));
            a.recycle();

            textView2.setTextColor(color);

            flexboxLayout.addView(textView2);
        });

        materialCardView.addView(flexboxLayout);
        scrollViewLayout.addView(materialCardView);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Map<String, CovidData.Statewise>> loader) {

    }

    private void addTextToScrollViewLayout(String msg) {
        TextView tv = new TextView(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(25, 0, 25, 25);
        tv.setLayoutParams(lp);
        tv.setText(msg);

        scrollViewLayout.addView(tv);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
