package com.nuron.minexpense.Fragments;


import android.app.Activity;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nuron.minexpense.Adapters.TransactionCursorAdaptor;
import com.nuron.minexpense.ContentProvider.TransactionProvider;
import com.nuron.minexpense.DBHelper.SQLiteDBHelper;
import com.nuron.minexpense.R;
import com.nuron.minexpense.Utilities.Utilities;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Created by sunil on 24-Jun-15.
 */
public class HomePageFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "MinExpenseFragment";
    double income_sum_final = 0, expense_sum_final = 0, left_sum_final = 0;
    AddExpenseClickListener mListener;
    View rootView;
    double monthlyExpenseTillYesterday = 0;
    private TransactionCursorAdaptor transactionCursorAdapter;
    private ListView mListView;
    private Handler handler;
    private Utilities utilities;

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
        utilities = new Utilities(getActivity());

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

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
        getLoaderManager().initLoader(1, null, this);
        getLoaderManager().initLoader(2, null, this);

        mListView = (ListView) rootView.findViewById(R.id.list);
        transactionCursorAdapter = new TransactionCursorAdaptor(getActivity(), null);
        mListView.setAdapter(transactionCursorAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            String[] today = utilities.getFirstAndLastDate(Utilities.TODAY_DATE);
            String today_start = today[0];
            String today_end = today[1];

            String selection = SQLiteDBHelper.TRANSACTION_TIME + " BETWEEN ? AND ? ";

            String[] selectionArgs = new String[]{today_start, today_end};

            Uri uri = TransactionProvider.CONTENT_URI;
            return new CursorLoader(getActivity(), uri, null, selection, selectionArgs, null);
        } else if (id == 1) {
            String[] month = utilities.getFirstAndLastDate(Utilities.MONTH_DATE_TILL_YESTERDAY);

            String month_start = month[0];
            String month_end = month[1];

            String[] projection = new String[]{"SUM(" + SQLiteDBHelper.TRANSACTION_AMOUNT + ") AS SUM_TOTAL"};

            String selection = SQLiteDBHelper.TRANSACTION_TIME + " >= ? AND " + SQLiteDBHelper.TRANSACTION_TIME + " <= ? AND "
                    + SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE + " == 1";

            String[] selectionArgs = new String[]{month_start, month_end};

            Uri uri = TransactionProvider.CONTENT_URI;
            return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, null);
        } else {
            String[] today = utilities.getFirstAndLastDate(Utilities.TODAY_DATE);

            String today_start = today[0];
            String today_end = today[1];

//            String[] projection = new String[]{SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE,
//                    "SUM(" + SQLiteDBHelper.TRANSACTION_AMOUNT + ") AS SUM_TOTAL"};
//
//            String selection = SQLiteDBHelper.TRANSACTION_TIME + " >= ? AND " + SQLiteDBHelper.TRANSACTION_TIME + " <= ? "
//                    + " GROUP BY " + SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE;

            String[] projection = new String[]{SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE,
                    "SUM(" + SQLiteDBHelper.TRANSACTION_AMOUNT + ") AS SUM_TOTAL"};
            String selection = SQLiteDBHelper.TRANSACTION_TIME + " BETWEEN ? AND ? " + " GROUP BY " + SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE;

            String[] selectionArgs = new String[]{today_start, today_end};

            Uri uri = TransactionProvider.CONTENT_URI;
            return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == 0) {
            transactionCursorAdapter.swapCursor(cursor);
        } else if (loader.getId() == 1) {
            final Cursor cursor1 = cursor;
            handler.post(new Runnable() {
                public void run() {
                    updateTodayExpenseMax(cursor1);
                }
            });
        } else {
            final Cursor cursor2 = cursor;
            handler.post(new Runnable() {
                public void run() {
                    updateSum(cursor2);
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
        getLoaderManager().restartLoader(2, null, this);
    }

    public void updateTodayExpenseMax(Cursor cursor) {
        double monthlyExpense_text;
        if (cursor != null && cursor.moveToFirst()) {
            if (cursor.getString(0) != null)
                monthlyExpenseTillYesterday = Double.parseDouble(cursor.getString(0));
        }

        monthlyExpense_text = utilities.getTodayExpenseMax(monthlyExpenseTillYesterday);

        DecimalFormat formatter = new DecimalFormat("#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        formatter.setRoundingMode(RoundingMode.HALF_UP);

        TextView today_max_text = (TextView) rootView.findViewById(R.id.today_expenseMax_text);
        today_max_text.setText(formatter.format(monthlyExpense_text));
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

        TextView expenseTodayText = (TextView) rootView.findViewById(R.id.today_expense_text);
        expenseTodayText.setText(formatter.format(expense_sum_final));

        double todayExpenseMax = Double.parseDouble(utilities.readFromSharedPref(R.string.Today_Expense_Max));


        utilities.writeToSharedPref(R.string.Monthly_Expense_Total, formatter.format(expense_sum_final + monthlyExpenseTillYesterday));

        ProgressBar pb = (ProgressBar) rootView.findViewById(R.id.progressBarLevel);
        left_sum_final = (100 * (todayExpenseMax - expense_sum_final)) / todayExpenseMax;
        if (left_sum_final >= 100)
            pb.setProgress(1);
        else
            pb.setProgress(100 - (int) left_sum_final);

    }

    public interface AddExpenseClickListener {
        void AddExpenseClick(int fragmentID);
    }
}

