package com.nuron.minexpense;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.nuron.minexpense.Activities.BaseActivity;
import com.nuron.minexpense.Fragments.ExpenseFragment;
import com.nuron.minexpense.Fragments.HomePageFragment;
import com.nuron.minexpense.Fragments.IncomeFragment;
import com.nuron.minexpense.Utilities.Utilities;


public class Homepage extends BaseActivity implements
        HomePageFragment.AddExpenseClickListener,
        FragmentManager.OnBackStackChangedListener,
        ExpenseFragment.saveExpenseListener,
        IncomeFragment.saveIncomeListener {


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        super.onCreateNavigationView();

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


    public void resumeDrawer(String title) {
        super.setDrawer(title);
    }

    public void disableDrawer() {
        super.disableDrawer();
    }

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

    @Override
    public void saveExpense() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        resumeDrawer("MinExpense");
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
                resumeDrawer("MinExpense");
            }
        });
    }

    @Override
    public void saveIncome() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStack();
        resumeDrawer("MinExpense");
    }
}

