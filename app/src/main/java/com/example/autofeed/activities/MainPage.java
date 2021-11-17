package com.example.autofeed.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;

import com.example.autofeed.classes.User;
import com.example.autofeed.fragments.Functions;
import com.example.autofeed.fragments.Guide;
import com.example.autofeed.fragments.Home;
import com.example.autofeed.fragments.Logs;
import com.example.autofeed.R;
import com.example.autofeed.fragments.Settings;
import com.example.autofeed.adapters.VPAdaptor;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainPage extends AppCompatActivity {

    //private Toolbar toolBar;
    private ViewPager2 viewPager2;
    private TabLayout mTabLayout;
    //private TabItem tiGuides, tiLogs, tiFunctions, tiSettings;
    private VPAdaptor vpAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);


        setVariables();
        // setSupportActionBar(toolBar);

        //get name of user for later use


        vpAdapter = new VPAdaptor(getSupportFragmentManager(), getLifecycle());
        //Add Fragments
        vpAdapter.addFragment(new Guide());     //0
        vpAdapter.addFragment(new Logs());      //1
        vpAdapter.addFragment(new Home());      //2
        vpAdapter.addFragment(new Functions()); //3
        vpAdapter.addFragment(new Settings());  //4

        viewPager2.setAdapter(vpAdapter);

        new TabLayoutMediator(mTabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setIcon(R.drawable.ic_baseline_help_outline_24);
                        break;
                    case 1:
                        tab.setIcon(R.drawable.ic_baseline_format_list_bulleted_24);
                        break;
                    case 2:
                        tab.setIcon(R.drawable.ic_baseline_home_24);
                        break;
                    case 3:
                        tab.setIcon(R.drawable.ic_baseline_dog_bowl_24);
                        break;
                    case 4 :
                        tab.setIcon(R.drawable.ic_baseline_settings_24);
                    }
            }
        }).attach();

    }

    private void setVariables() {
        //     toolBar = findViewById(R.id.toolbar);
        viewPager2 = findViewById(R.id.viewpager2);
        mTabLayout = findViewById(R.id.tabLayout);
    }
}