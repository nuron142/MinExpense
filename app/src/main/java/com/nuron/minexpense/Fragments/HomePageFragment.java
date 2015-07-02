package com.nuron.minexpense.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nuron.minexpense.Adapters.TransactionCursorAdaptor;
import com.nuron.minexpense.ContentProvider.TransactionProvider;
import com.nuron.minexpense.DBHelper.SQLiteDBHelper;
import com.nuron.minexpense.R;
import com.nuron.minexpense.Utilities.Utilities;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by sunil on 24-Jun-15.
 */
public class HomePageFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "MinExpenseFragment";
    double income_sum_final = 0, expense_sum_final = 0, left_sum_final = 0;
    AddExpenseClickListener mListener;
    View rootView;
    private TransactionCursorAdaptor transactionCursorAdapter;
    private ListView mListView;
    private Handler handler;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (AddExpenseClickListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement AddExpenseClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.homepage_fragment, container, false);

        handler = new Handler();

        Button add_expense = (Button) rootView.findViewById(R.id.add_expense);
        add_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.AddExpenseClick(Utilities.TYPE_EXPENSE);
            }
        });

        Button add_income = (Button) rootView.findViewById(R.id.add_income);
        add_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.AddExpenseClick(Utilities.TYPE_INCOME);
            }
        });

        final EditText budget = (EditText) rootView.findViewById(R.id.budget_edittext);
        TextView budget_text = (TextView) rootView.findViewById(R.id.budget_text);
        Button budget_save = (Button) rootView.findViewById(R.id.budget_save);
        budget_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateBudget(budget.getText().toString(),true);
            }
        });

        updateBudget("",false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
        getLoaderManager().initLoader(1, null, this);

        mListView = (ListView) rootView.findViewById(R.id.list);
        transactionCursorAdapter = new TransactionCursorAdaptor(getActivity(), null);
        mListView.setAdapter(transactionCursorAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String today_start = dateFormat.format(date) + " 00:00:00";
            String today_end = dateFormat.format(date) + " 23:59:59";

            String selection = SQLiteDBHelper.TRANSACTION_TIME + " BETWEEN ? AND ? ";

            String[] selectionArgs = new String[]{today_start, today_end};

            Uri uri = TransactionProvider.CONTENT_URI;
            return new CursorLoader(getActivity(), uri, null, selection, selectionArgs, null);
        } else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String today_start = dateFormat.format(date) + " 00:00:00";
            String today_end = dateFormat.format(date) + " 23:59:59";

            String[] projection = new String[]{SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE,
                    "SUM(" + SQLiteDBHelper.TRANSACTION_AMOUNT + ") AS SUM_TOTAL"};
            String selection =
                    SQLiteDBHelper.TRANSACTION_TIME + " BETWEEN ? AND ? "
                            + " GROUP BY " + SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE;

            String[] selectionArgs = new String[]{today_start, today_end};

            Uri uri = TransactionProvider.CONTENT_URI;
            return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == 0) {
            transactionCursorAdapter.swapCursor(cursor);
        } else {
            final Cursor cursor1 = cursor;
            handler.post(new Runnable() {
                public void run() {
                    updateSum(cursor1);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == 0)
            transactionCursorAdapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
        getLoaderManager().restartLoader(1, null, this);
    }

    public void updateSum(Cursor cursor) {

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

        income_sum_final = Double.parseDouble(transactionSumBundle.getString("income_sum"));
        expense_sum_final = Double.parseDouble(transactionSumBundle.getString("expense_sum"));


        DecimalFormat formatter = new DecimalFormat("#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        formatter.setRoundingMode(RoundingMode.HALF_UP);

        TextView incomeText = (TextView) rootView.findViewById(R.id.income_sum);
        incomeText.setText(formatter.format(income_sum_final));

        TextView expenseText = (TextView) rootView.findViewById(R.id.expense_sum);
        expenseText.setText(formatter.format(expense_sum_final));


        ProgressBar pb = (ProgressBar) rootView.findViewById(R.id.progressBarLevel);
        left_sum_final = (100 * (income_sum_final - expense_sum_final)) / income_sum_final;
        if (left_sum_final <= 0)
            pb.setProgress(100);
        else if (left_sum_final >= 100)
            pb.setProgress(1);
        else
            pb.setProgress((int) left_sum_final);

    }

    public interface AddExpenseClickListener {
        void AddExpenseClick(int fragmentID);
    }

    public void updateBudget(String value,boolean update)
    {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        if(update)
        {
            if(value.equals(""))
                value="0";
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.Budget_value), value);
            editor.commit();

        }

        TextView budget_text = (TextView) rootView.findViewById(R.id.budget_text);
        String budget_saved_value = sharedPref.getString(getString(R.string.Budget_value), "0");
        budget_text.setText(budget_saved_value);

    }
}

