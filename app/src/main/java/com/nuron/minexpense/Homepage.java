package com.nuron.minexpense;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.nuron.minexpense.ContentProvider.TransactionProvider;
import com.nuron.minexpense.DBHelper.SQLiteDBHelper;
import com.nuron.minexpense.Fragments.BudgetFragment;
import com.nuron.minexpense.Fragments.ExpenseFragment;
import com.nuron.minexpense.Fragments.HomePageFragment;
import com.nuron.minexpense.Fragments.IncomeFragment;
import com.nuron.minexpense.Fragments.TransactionFragment;
import com.nuron.minexpense.Fragments.TransactionParentFragment;
import com.nuron.minexpense.Utilities.Utilities;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;


public class Homepage extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        HomePageFragment.AddExpenseClickListener,
        FragmentManager.OnBackStackChangedListener,
        ExpenseFragment.saveExpenseListener,
        IncomeFragment.saveIncomeListener,
        BudgetFragment.saveBudgetListener {

    FragmentManager manager;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Utilities utilities;
    private TextView current_budget, available_balance;
    private Handler handler;
    private int clickedDrawerItem = 1;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        handler = new Handler();
        getLoaderManager().initLoader(0, null, this);
        current_budget = (TextView) findViewById(R.id.current_budget_drawer);
        available_balance = (TextView) findViewById(R.id.available_balance_drawer);
        utilities = new Utilities(this);
        setNavigationDrawer();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        getSupportFragmentManager().addOnBackStackChangedListener(this);
        if (findViewById(R.id.fragment_container) != null) {

            HomePageFragment firstFragment = new HomePageFragment();
            firstFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment, HomePageFragment.TAG).commit();
        }
    }


    @Override
    public void AddExpenseClick(int fragmentID) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (fragmentID == Utilities.TYPE_EXPENSE) {
            ExpenseFragment expenseFragment = new ExpenseFragment();
            fragmentTransaction
                    .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out)
                    .replace(R.id.fragment_container, expenseFragment, ExpenseFragment.TAG);
            setToolbar("ADD EXPENSE");
        } else if (fragmentID == Utilities.TYPE_INCOME) {
            IncomeFragment incomeFragment = new IncomeFragment();
            fragmentTransaction
                    .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out)
                    .replace(R.id.fragment_container, incomeFragment, IncomeFragment.TAG);
            setToolbar("ADD INCOME");
        }
        fragmentTransaction.addToBackStack(HomePageFragment.TAG);
        fragmentTransaction.commit();
    }

    private void updateAvailableBalance(Cursor cursor) {

        Bundle transactionSumBundle = new Bundle();

        transactionSumBundle.putString("income_sum", "0");
        transactionSumBundle.putString("expense_sum", "0");

        while (cursor.moveToNext()) {
            switch (cursor.getString(0)) {
                case "0":
                    transactionSumBundle.putString("income_sum", cursor.getString(1));
                    break;
                case "1":
                    transactionSumBundle.putString("expense_sum", cursor.getString(1));
                    break;
            }
        }

        double income_sum_final = Double.parseDouble(transactionSumBundle.getString("income_sum"));
        double expense_sum_final = Double.parseDouble(transactionSumBundle.getString("expense_sum"));


        DecimalFormat formatter = new DecimalFormat("#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        formatter.setRoundingMode(RoundingMode.HALF_UP);

        double availableBalance = income_sum_final - expense_sum_final;
        available_balance.setText(formatter.format(availableBalance));
    }

    //region Fragment Listeners
    @Override
    public void saveExpense() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        setDrawer("MinExpense");
    }

    @Override
    public void saveIncome() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        setDrawer("MinExpense");
    }

    @Override
    public void saveNewBudget() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        setDrawer("MinExpense");
    }
    //endregion

    //region Navigation drawer
//    public void setNavigationDrawer() {
//        toolbar = (Toolbar) findViewById(R.id.tool_bar);
//
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setHomeButtonEnabled(true);
//        }
//
//        navigationView = (NavigationView) findViewById(R.id.navigation_view);
//        navigationView.setNavigationItemSelectedListener
//                (new NavigationView.OnNavigationItemSelectedListener() {
//
//                     @Override
//                     public boolean onNavigationItemSelected(MenuItem menuItem) {
//
//                         drawerLayout.closeDrawers();
//                         switch (menuItem.getItemId()) {
//                             case R.id.navigation_item_1:
//                                 if(clickedDrawerItem == 1)
//                                     return true;
//                                 manager = getSupportFragmentManager();
//                                 if (manager != null) {
//                                     int backStackEntryCount = manager.getBackStackEntryCount();
//                                     if (backStackEntryCount > 0) {
//
//                                         FragmentManager.BackStackEntry backEntry =
//                                                 getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1);
//                                         String backEntryName = backEntry.getName();
//                                         if (backEntryName.equals(TransactionFragment.TAG)) {
//                                             new Handler().postDelayed(new Runnable() {
//                                                 @Override
//                                                 public void run() {
//                                                     getSupportFragmentManager().popBackStack();
//                                                     setDrawer("MinExpense");
//                                                 }
//                                             }, 200);
//
//                                             return true;
//                                         }
//                                     }
//                                 }
//                                 return true;
//
//                             case R.id.navigation_item_2:
//                                 manager = getSupportFragmentManager();
//                                 if (manager != null) {
//                                     int backStackEntryCount = manager.getBackStackEntryCount();
//                                     if (backStackEntryCount == 0) {
//                                         new Handler().postDelayed(new Runnable() {
//                                             @Override
//                                             public void run() {
//                                                 if (findViewById(R.id.fragment_container) != null) {
//                                                     BudgetFragment budgetFragment = new BudgetFragment();
//                                                     budgetFragment.setArguments(getIntent().getExtras());
//
//                                                     getSupportFragmentManager().beginTransaction()
//                                                             .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out)
//                                                             .replace(R.id.fragment_container, budgetFragment, BudgetFragment.TAG)
//                                                             .addToBackStack(BudgetFragment.TAG)
//                                                             .commit();
//
//                                                     setToolbar("SET BUDGET");
//                                                 }
//                                             }
//                                         }, 200);
//                                     } else if (backStackEntryCount > 0) {
//                                         FragmentManager.BackStackEntry backEntry =
//                                                 getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1);
//                                         String backEntryName = backEntry.getName();
//                                         if (backEntryName.equals(BudgetFragment.TAG))
//                                             return true;
//                                     }
//                                 }
//                                 return true;
//
//                             case R.id.navigation_item_3:
//
//                                 manager = getSupportFragmentManager();
//                                 if (manager != null) {
//                                     int backStackEntryCount = manager.getBackStackEntryCount();
//                                     if (backStackEntryCount == 0) {
//                                         new Handler().postDelayed(new Runnable() {
//                                             @Override
//                                             public void run() {
//                                                 if (findViewById(R.id.fragment_container) != null) {
//                                                     TransactionFragment transactionFragment = new TransactionFragment();
//                                                     transactionFragment.setArguments(getIntent().getExtras());
//
//                                                     getSupportFragmentManager().beginTransaction()
//                                                             .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out)
//                                                             .replace(R.id.fragment_container, transactionFragment, TransactionFragment.TAG)
//                                                             .addToBackStack(TransactionFragment.TAG)
//                                                             .commit();
//
//                                                     setDrawer("TRANSACTIONS");
//                                                 }
//                                             }
//                                         }, 200);
//                                     } else if (backStackEntryCount > 0) {
//                                         FragmentManager.BackStackEntry backEntry =
//                                                 getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1);
//                                         String backEntryName = backEntry.getName();
//                                         if (backEntryName.equals(TransactionFragment.TAG))
//                                             return true;
//                                     }
//                                 }
//
//                                 return true;
//
//                             default:
//                                 return true;
//                         }
//                     }
//                 }
//
//                );
//
//        setDrawer("MinExpense");
//    }

    public void setNavigationDrawer() {
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
//                                 if(clickedDrawerItem == 1)
//                                     return true;
                                 manager = getSupportFragmentManager();
                                 if (manager != null) {
                                     int backStackEntryCount = manager.getBackStackEntryCount();
                                     if (backStackEntryCount > 0) {

                                         FragmentManager.BackStackEntry backEntry =
                                                 getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1);
                                         String backEntryName = backEntry.getName();
                                         if (!backEntryName.equals(HomePageFragment.TAG)) {
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
                                 manager = getSupportFragmentManager();
                                 if (manager != null) {
                                     int backStackEntryCount = manager.getBackStackEntryCount();
                                     if (backStackEntryCount == 0) {
                                         new Handler().postDelayed(new Runnable() {
                                             @Override
                                             public void run() {
                                                 if (findViewById(R.id.fragment_container) != null) {
                                                     BudgetFragment budgetFragment = new BudgetFragment();
                                                     budgetFragment.setArguments(getIntent().getExtras());

                                                     getSupportFragmentManager().beginTransaction()
                                                             .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out)
                                                             .replace(R.id.fragment_container, budgetFragment, BudgetFragment.TAG)
                                                             .addToBackStack(BudgetFragment.TAG)
                                                             .commit();

                                                     setToolbar("SET BUDGET");
                                                 }
                                             }
                                         }, 200);
                                     } else if (backStackEntryCount > 0) {
                                         FragmentManager.BackStackEntry backEntry =
                                                 getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1);
                                         String backEntryName = backEntry.getName();
                                         if (backEntryName.equals(BudgetFragment.TAG))
                                             return true;
                                     }
                                 }
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
                                                     TransactionParentFragment transactionParentFragment = new TransactionParentFragment();
                                                     //TransactionFragment transactionFragment = TransactionFragment.newInstance("2015-07-01 00:00:00","2015-07-30 23:59:59");

                                                     getSupportFragmentManager().beginTransaction()
                                                             .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out, R.anim.abc_fade_in, R.anim.abc_fade_out)
                                                             .replace(R.id.fragment_container, transactionParentFragment, TransactionParentFragment.TAG)
                                                             .addToBackStack(TransactionParentFragment.TAG)
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
    //endregion

    //region Drawer and Toolbar
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

        String currentBudget = utilities.readFromSharedPref(R.string.Budget_value);
        current_budget.setText("Rs " + currentBudget);

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

    public void setToolbar(String title) {
        disableDrawer();
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setElevation(0);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);
        }

        toolbar.setNavigationIcon(R.drawable.ic_up);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStack();
                setDrawer("MinExpense");
            }
        });
    }
    //endregion

    //region Loader Implementation
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = new String[]{SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE,
                "SUM(" + SQLiteDBHelper.TRANSACTION_AMOUNT + ") AS SUM_TOTAL"};
        String selection = " 0 == 0 GROUP BY " + SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE;

        Uri uri = TransactionProvider.CONTENT_URI;
        return new CursorLoader(this, uri, projection, selection, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        final Cursor cursor1 = cursor;
        handler.post(new Runnable() {
            public void run() {
                updateAvailableBalance(cursor1);
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
    //endregion

    //region Misc
    @Override
    public void onBackStackChanged() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager != null) {
            int backStackEntryCount = manager.getBackStackEntryCount();
            if (backStackEntryCount == 0)
                return;

            Fragment fragment = manager.getFragments()
                    .get(backStackEntryCount - 1);
            fragment.onResume();
        }
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
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
    //endregion

}