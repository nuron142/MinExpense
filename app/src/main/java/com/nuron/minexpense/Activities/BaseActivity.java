package com.nuron.minexpense.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.nuron.minexpense.Fragments.TransactionFragment;
import com.nuron.minexpense.R;

/**
 * Created by sunil on 20-Jun-15.
 */
public abstract class BaseActivity extends AppCompatActivity {

    Intent intent;
    FragmentManager manager;
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
        navigationView.setNavigationItemSelectedListener
                (new NavigationView.OnNavigationItemSelectedListener() {

                     @Override
                     public boolean onNavigationItemSelected(MenuItem menuItem) {

                         drawerLayout.closeDrawers();
                         switch (menuItem.getItemId()) {
                             case R.id.navigation_item_1:
                                 manager = getSupportFragmentManager();
                                 if (manager != null) {
                                     int backStackEntryCount = manager.getBackStackEntryCount();
                                     if (backStackEntryCount > 0) {

                                         FragmentManager.BackStackEntry backEntry =
                                                 getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1);
                                         String backEntryName = backEntry.getName();
                                         if (backEntryName.equals(TransactionFragment.TAG)) {
                                             Log.d("1", "Home");
                                             new Handler().postDelayed(new Runnable() {
                                                 @Override
                                                 public void run() {
                                                     getSupportFragmentManager().popBackStack();
                                                     setDrawer("MinExpense");
                                                 }
                                             }, 200);

                                             return true;
                                         }
                                     }
                                 }
                                 return true;

                             case R.id.navigation_item_2:

                                 return true;

                             case R.id.navigation_item_3:

                                 manager = getSupportFragmentManager();
                                 if (manager != null) {
                                     int backStackEntryCount = manager.getBackStackEntryCount();
                                     if (backStackEntryCount == 0) {
                                         new Handler().postDelayed(new Runnable() {
                                             @Override
                                             public void run() {
                                                 if (findViewById(R.id.fragment_container) != null) {
                                                     TransactionFragment transactionFragment = new TransactionFragment();
                                                     transactionFragment.setArguments(getIntent().getExtras());

                                                     getSupportFragmentManager().beginTransaction()
                                                             .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out)
                                                             .replace(R.id.fragment_container, transactionFragment, TransactionFragment.TAG)
                                                             .addToBackStack(TransactionFragment.TAG)
                                                             .commit();

                                                     setDrawer("TRANSACTIONS");
                                                 }
                                             }
                                         }, 200);
                                     } else if (backStackEntryCount > 0) {
                                         FragmentManager.BackStackEntry backEntry =
                                                 getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1);
                                         String backEntryName = backEntry.getName();
                                         if (backEntryName.equals(TransactionFragment.TAG))
                                             return true;

//                                         new Handler().postDelayed(new Runnable()
//                                         {
//                                             @Override
//                                             public void run() {
//                                                 TransactionFragment transactionFragment =
//                                                         (TransactionFragment) getSupportFragmentManager().findFragmentByTag(TransactionFragment.TAG);
//                                                 if (transactionFragment != null)
//                                                 {
//                                                     getSupportFragmentManager().beginTransaction().remove(transactionFragment).commit();
//
//                                                     transactionFragment = new TransactionFragment();
//                                                     transactionFragment.setArguments(getIntent().getExtras());
//
//                                                     getSupportFragmentManager().beginTransaction()
//                                                             .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out)
//                                                             .replace(R.id.fragment_container, transactionFragment, TransactionFragment.TAG)
//                                                             .addToBackStack(TransactionFragment.TAG).commit();
//                                                     setDrawer("TRANSACTIONS");
//                                                 }
//                                             }
//                                         }, 200);
                                     }
                                 }

                                 return true;

                             default:
                                 return true;
                         }
                     }
                 }

                );

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

        hideSoftKeyboard();
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

        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            getSupportActionBar().setTitle(title);
        }

    }

    public void disableDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

}
