package com.nuron.minexpense.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import com.nuron.minexpense.Fragments.TransactionFragment;
import com.nuron.minexpense.R;

/**
 * Created by sunil on 20-Jun-15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    Intent intent;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;


    protected void onCreateNavigationView() {

        toolbar = (Toolbar) findViewById(R.id.tool_bar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_1:
//                        intent = new Intent("com.nuron.minexpense.ADD_EXPENSE");
//                        startActivity(intent);
                        return true;

                    case R.id.navigation_item_2:
//                        intent = new Intent("com.nuron.minexpense.ADD_INCOME");
//                        startActivity(intent);
                        return true;

                    case R.id.navigation_item_3:

                        FragmentManager manager = getSupportFragmentManager();
                        if (manager != null) {
                            int backStackEntryCount = manager.getBackStackEntryCount();
                            if (backStackEntryCount == 0) {
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (findViewById(R.id.fragment_container) != null) {
                                            TransactionFragment firstFragment = new TransactionFragment();
                                            firstFragment.setArguments(getIntent().getExtras());
                                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                                            fragmentTransaction.replace(R.id.fragment_container, firstFragment);
                                            fragmentTransaction.addToBackStack("MinExpense");

                                            fragmentTransaction.commit();
                                            setDrawer("TRANSACTIONS");
                                        }
                                    }
                                }, 200);
                            } else if (backStackEntryCount > 0) {
                                FragmentManager.BackStackEntry backStackEntry = getSupportFragmentManager().getBackStackEntryAt(backStackEntryCount - 1);
                                String str = backStackEntry.getName();

                                if (!str.equals("MinExpense")) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (findViewById(R.id.fragment_container) != null) {
                                                TransactionFragment firstFragment = new TransactionFragment();
                                                firstFragment.setArguments(getIntent().getExtras());
                                                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                                                fragmentTransaction.replace(R.id.fragment_container, firstFragment);
                                                fragmentTransaction.addToBackStack("MinExpense");

                                                fragmentTransaction.commit();
                                                setDrawer("TRANSACTIONS");
                                            }
                                        }
                                    }, 200);
                                }
                            }
                        }
                        return true;

                    default:
                        return true;
                }
            }
        });
        setDrawer("MinExpense");
    }

    protected boolean isNavDrawerOpen() {
        return drawerLayout != null && drawerLayout.isDrawerOpen(Gravity.LEFT);
    }

    protected void closeNavDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
    }

    @Override
    public void onBackPressed() {
        if (isNavDrawerOpen()) {
            closeNavDrawer();
        } else {

            super.onBackPressed();
            setDrawer("MinExpense");
        }
    }

    public void setDrawer(String title) {

        float elevation = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        toolbar.setElevation(elevation);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        actionBarDrawerToggle.syncState();

        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(title);
    }

    public void disableDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }
}
