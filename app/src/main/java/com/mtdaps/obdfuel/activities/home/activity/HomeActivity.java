package com.mtdaps.obdfuel.activities.home.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mtdaps.obdfuel.R;
import com.mtdaps.obdfuel.util.ActivityInterface;
import com.mtdaps.obdfuel.activities.home.util.HomeTabAdapter;
import com.mtdaps.obdfuel.util.UiUtil;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements ActivityInterface {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ArrayList<Fragment> tabTitlesAndI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // hide the action bar
        getSupportActionBar().hide();

        UiUtil.showSnackbar(findViewById(R.id.home_parent_layout), getBaseContext(), "OBD Connected").show();

        setup();

        prepareAdapter(viewPager);
    }

    @Override
    public void setup() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
    }

    private void prepareAdapter(ViewPager2 viewPager) {
        HomeTabAdapter homeTabAdapter = new HomeTabAdapter(this);
        viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        viewPager.setAdapter(homeTabAdapter);
        viewPager.setPageTransformer(new MarginPageTransformer(5000));
        viewPager.animate();

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    if (position==0) {
                        tab.setText("Dashboard");
                        tab.setIcon(R.drawable.ic_sharp_dashboard_24);

                    }
                    else{
                        tab.setText("Send Data");
                        tab.setIcon(R.drawable.ic_baseline_cloud_upload_24);
                    }

                }
        ).attach();

    }


}