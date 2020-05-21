package com.sagnik.covidnotifier;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.card.MaterialCardView;
import com.sagnik.covidnotifier.dagger.DaggerServiceDaggerComponent;
import com.sagnik.covidnotifier.loaders.DataLoader;
import com.sagnik.covidnotifier.models.CovidData;
import com.sagnik.covidnotifier.sync.SyncActivator;
import com.sagnik.covidnotifier.utils.Consts;
import com.sagnik.covidnotifier.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.sagnik.covidnotifier.utils.Consts.CONFIRMED_TXT;
import static com.sagnik.covidnotifier.utils.Consts.DECEASED_TXT;
import static com.sagnik.covidnotifier.utils.Consts.RECOVERED_TXT;
import static com.sagnik.covidnotifier.utils.Consts.TOTAL_KEY;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Map<String, CovidData.Statewise>> {
    private static final int LOAD_DATA = 0;
    public static final String DELTA_PREFIX = " ( ";
    public static final String DELTA_SUFFIX = ")";
    public static final int DEFAULT_MARGIN_ELEVATION_DP = 10;
    public static final String TOTAL_CARD_COLOR = "#fffaf5";
    public static final String DEFAULT_CARD_COLOR = "#f5f9ff";

    private LinearLayout scrollViewLayout;

    @Inject
    SyncActivator syncActivator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerServiceDaggerComponent.builder().build().inject(this);

        this.syncActivator.activate(this);

        setContentView(R.layout.activity_main);
        scrollViewLayout = findViewById(R.id.scroll_view_layout);

        addTextToScrollViewLayout("Loading Data...");
        LoaderManager.getInstance(this).restartLoader(LOAD_DATA, new Bundle(), this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_button:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
        CovidData.Statewise total = data.get(TOTAL_KEY);

        addStatewise(total, true);
        data.values().stream().filter(statewise -> !TOTAL_KEY.equals(statewise.state))
                .sorted((a, b) -> {
                    if (a.active > b.active) return -1;
                    else if (a.active < b.active) return 1;
                    else return 0;
                }).forEach(statewise -> addStatewise(statewise, false)); // todo move sorting logic to a separate class
    }

    private void addStatewise(CovidData.Statewise statewise, boolean special) {
        List<String> contents = new ArrayList<>();
        contents.add(Consts.ACTIVE_TXT + Utils.formatNumber(statewise.active));
        contents.add(CONFIRMED_TXT + Utils.formatNumber(statewise.confirmed) + DELTA_PREFIX + Utils.formatNumber(statewise.deltaconfirmed, true) + DELTA_SUFFIX);
        contents.add(RECOVERED_TXT + Utils.formatNumber(statewise.recovered) + DELTA_PREFIX + Utils.formatNumber(statewise.deltarecovered, true) + DELTA_SUFFIX);
        contents.add(DECEASED_TXT + Utils.formatNumber(statewise.deaths) + DELTA_PREFIX + Utils.formatNumber(statewise.deltadeaths, true) + DELTA_SUFFIX);

        addCard(statewise.state, contents, special);
    }

    private void addCard(String heading, Collection<String> otherContents, boolean special) {
        final float scale = this.getResources().getDisplayMetrics().density;

        int defaultMarginElevationPx = (int) (DEFAULT_MARGIN_ELEVATION_DP * scale);

        MaterialCardView materialCardView = new MaterialCardView(this);
        if (special) {
            materialCardView.setCardBackgroundColor(Color.parseColor(TOTAL_CARD_COLOR));
        } else {
            materialCardView.setCardBackgroundColor(Color.parseColor(DEFAULT_CARD_COLOR));
        }
        ViewGroup.MarginLayoutParams cardLayout = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cardLayout.setMargins(defaultMarginElevationPx, defaultMarginElevationPx, defaultMarginElevationPx, defaultMarginElevationPx);
        materialCardView.setLayoutParams(cardLayout);
        materialCardView.setCardElevation(defaultMarginElevationPx);

        FlexboxLayout flexboxLayout = new FlexboxLayout(this);
        flexboxLayout.setLayoutParams(cardLayout);
        flexboxLayout.setFlexWrap(FlexWrap.WRAP);

        TextView textView = new TextView(this);
        ViewGroup.MarginLayoutParams headerLayout = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(headerLayout);
        textView.setText(heading);
        TypedValue textAppearanceHeadline6 = new TypedValue();
        getTheme().resolveAttribute(R.attr.textAppearanceHeadline6, textAppearanceHeadline6, true);
        textView.setTextAppearance(textAppearanceHeadline6.data);
        flexboxLayout.addView(textView);

        otherContents.forEach(text -> {
            TextView otherContentText = new TextView(this);
            ViewGroup.MarginLayoutParams otherContentLayout = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            otherContentLayout.setMargins(0, (int) (8 * scale), (int) (12 * scale), 0);
            otherContentText.setLayoutParams(otherContentLayout);
            otherContentText.setText(text);
            TypedValue textAppearanceBody2 = new TypedValue();
            getTheme().resolveAttribute(R.attr.textAppearanceBody2, textAppearanceBody2, true);
            otherContentText.setTextAppearance(textAppearanceBody2.data);

            TypedArray textColorSecondaryAttributes = obtainStyledAttributes(new int[]{android.R.attr.textColorSecondary});
            ColorStateList color = textColorSecondaryAttributes.getColorStateList(textColorSecondaryAttributes.getIndex(0));
            textColorSecondaryAttributes.recycle();

            otherContentText.setTextColor(color);

            flexboxLayout.addView(otherContentText);
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
}
