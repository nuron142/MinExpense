package com.nuron.minexpense;


import android.os.Bundle;

import com.nuron.minexpense.Activities.BaseActivity;
import com.nuron.minexpense.Fragments.HomePageFragment;


public class Homepage extends BaseActivity {


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        super.onCreateNavigationView();

        if (findViewById(R.id.fragment_container) != null) {


            HomePageFragment firstFragment = new HomePageFragment();
            firstFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
        }

    }
}

