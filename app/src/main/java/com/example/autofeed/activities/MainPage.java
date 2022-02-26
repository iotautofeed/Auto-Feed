package com.example.autofeed.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.autofeed.R;
import com.example.autofeed.adapters.VPAdaptor;
import com.example.autofeed.fragments.Functions;
import com.example.autofeed.fragments.Guide;
import com.example.autofeed.fragments.Home;
import com.example.autofeed.fragments.Logs;
import com.example.autofeed.fragments.Settings;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainPage extends AppCompatActivity {

    //private Toolbar toolBar;
    private ViewPager2 viewPager2; // define variable for paging through a modifiable collection of fragments
    private TabLayout mTabLayout;  // define variable for creating a tablayout
    //private TabItem tiGuides, tiLogs, tiFunctions, tiSettings;
    private VPAdaptor vpAdapter;  //define variable as an adapter for viewpager2


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);


        setVariables();// function for variables setup

        vpAdapter = new VPAdaptor(getSupportFragmentManager(), getLifecycle()); //create VPAdapter class

        vpAdapter.addFragment(new Home());     //
        vpAdapter.addFragment(new Functions());//
        vpAdapter.addFragment(new Logs());     // Add Fragments to the adapter
        vpAdapter.addFragment(new Settings()); //
        vpAdapter.addFragment(new Guide());    //

        viewPager2.setAdapter(vpAdapter); //setting the adaptor

        new TabLayoutMediator(mTabLayout, viewPager2, (tab, position) -> {// crate a tablayot
            switch (position) {
                case 0:                                                         //first tab
                    tab.setIcon(R.drawable.ic_baseline_home_24);                // set icon
                    break;
                case 1:                                                         //second tab
                    tab.setIcon(R.drawable.ic_baseline_dog_bowl_24);            // set icon
                    break;
                case 2:                                                         //third tab
                    tab.setIcon(R.drawable.ic_baseline_format_list_bulleted_24);// set icon
                    break;
                case 3:                                                         //first tab
                    tab.setIcon(R.drawable.ic_baseline_settings_24);            // set icon
                    break;
                case 4 :                                                        //first tab
                    tab.setIcon(R.drawable.ic_baseline_help_outline_24);        // set icon

                }
        }).attach(); // link the TabLayout and the ViewPager2 together

    }

    private void setVariables() {                  //function for linking the UI to the code
        //     toolBar = findViewById(R.id.toolbar);
        viewPager2 = findViewById(R.id.viewpager2);//
        mTabLayout = findViewById(R.id.tabLayout); //
    }
}