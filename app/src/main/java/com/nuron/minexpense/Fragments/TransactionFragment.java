package com.nuron.minexpense.Fragments;

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
 * Created by sunil on 28-Jun-15.
 */
public class TransactionFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String TAG = "TransactionsFragment";
    double incomeCurrent = 0, expenseCurrent = 0;
    View rootView, headerView = null;
    private TransactionCursorAdaptor transactionCursorAdapter;
    private ListView mListView;
    private Handler handler;
    private Utilities utilities;
    private String startDate = "", endDate = "";

    public static TransactionFragment newInstance(String startDate, String endDate) {
        TransactionFragment transactionFragment = new TransactionFragment();
        Bundle args = new Bundle();
        args.putString("startDate", startDate);
        args.putString("endDate", endDate);
        transactionFragment.setArguments(args);
        return transactionFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.transactions_fragment, container, false);

        handler = new Handler();
        utilities = new Utilities(getActivity());

        Bundle transactionBundle = getArguments();
        if (transactionBundle != null) {
            startDate = transactionBundle.getString("startDate");
            endDate = transactionBundle.getString("endDate");
        }
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
        getLoaderManager().initLoader(1, null, this);
        getLoaderManager().initLoader(2, null, this);

        mListView = (ListView) rootView.findViewById(R.id.list);
        headerView = getActivity().getLayoutInflater().inflate(R.layout.transaction_header, mListView, false);
        mListView.addHeaderView(headerView);
        transactionCursorAdapter = new TransactionCursorAdaptor(getActivity(), null);
        mListView.setAdapter(transactionCursorAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
        getLoaderManager().restartLoader(1, null, this);
        getLoaderManager().restartLoader(2, null, this);
    }

    //region Loader Implementation
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            String selection = SQLiteDBHelper.TRANSACTION_TIME + " BETWEEN ? AND ? ";

            String[] selectionArgs = new String[]{startDate, endDate};

            Uri uri = TransactionProvider.CONTENT_URI;
            return new CursorLoader(getActivity(), uri, null, selection, selectionArgs, null);
        } else if (id == 1) {
            String[] selectionArgs = new String[]{startDate, endDate};

            String[] projection = new String[]{SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE,
                    "SUM(" + SQLiteDBHelper.TRANSACTION_AMOUNT + ") AS SUM_TOTAL"};
            String selection = SQLiteDBHelper.TRANSACTION_TIME + " BETWEEN ? AND ? " + " GROUP BY " + SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE;

            Uri uri = TransactionProvider.CONTENT_URI;
            return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, null);
        } else {
            String[] projection = new String[]{SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE,
                    "SUM(" + SQLiteDBHelper.TRANSACTION_AMOUNT + ") AS SUM_TOTAL"};
            String selection = SQLiteDBHelper.TRANSACTION_TIME + " < ? " + " GROUP BY " + SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE;
            String[] selectionArgs = new String[]{startDate};

            Uri uri = TransactionProvider.CONTENT_URI;
            return new CursorLoader(getActivity(), uri, projection, selection, selectionArgs, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (loader.getId() == 0) {
            transactionCursorAdapter.swapCursor(cursor);
            // mListView.setVisibility((transactionCursorAdapter.isEmpty()) ? View.GONE : View.VISIBLE);
            if (transactionCursorAdapter.isEmpty())
                mListView.setVisibility(View.GONE);
            else {
                mListView.setVisibility(View.VISIBLE);
                headerView.setVisibility(View.VISIBLE);
            }

        } else if (loader.getId() == 1) {
            final Cursor cursor1 = cursor;
            handler.post(new Runnable() {
                public void run() {
                    getIncomeAndExpense(cursor1);
                }
            });
        } else {
            final Cursor cursor1 = cursor;

            handler.postDelayed(new Runnable() {
                public void run() {
                    updateSum(cursor1);
                }
            }, 10);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == 0)
            transactionCursorAdapter.swapCursor(null);
    }
    //endregion

    public void getIncomeAndExpense(Cursor cursor) {

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

        incomeCurrent = Double.parseDouble(transactionSumBundle.getString("income_sum"));
        expenseCurrent = Double.parseDouble(transactionSumBundle.getString("expense_sum"));
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

        double incomePrevious = Double.parseDouble(transactionSumBundle.getString("income_sum"));
        double expensePrevious = Double.parseDouble(transactionSumBundle.getString("expense_sum"));

        double savedPrevious = incomePrevious - expensePrevious;

        DecimalFormat formatter = new DecimalFormat("#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        formatter.setRoundingMode(RoundingMode.HALF_UP);

        if (headerView != null) {
            TextView income_header = (TextView) headerView.findViewById(R.id.list_header_income);
            income_header.setText("₹ " + formatter.format(incomeCurrent));

            TextView expense_header = (TextView) headerView.findViewById(R.id.list_header_expense);
            expense_header.setText("₹ " + formatter.format(expenseCurrent));
        }

//        TextView savedPreviousText = (TextView) rootView.findViewById(R.id.saved_previous_text);
//        savedPreviousText.setText(formatter.format(savedPrevious));

        double incomeTotal = savedPrevious + incomeCurrent;
        TextView incomeTotalProgressBarText = (TextView) rootView.findViewById(R.id.income_progressbar_text);
        incomeTotalProgressBarText.setText(formatter.format(incomeTotal));

        TextView expenseProgressBarText = (TextView) rootView.findViewById(R.id.expense_progressbar_text);
        expenseProgressBarText.setText("₹ " + formatter.format(expenseCurrent) + "/");

        double saved = incomeTotal - expenseCurrent;

        TextView savedText = (TextView) rootView.findViewById(R.id.saved_text);
        savedText.setText(formatter.format(saved));

        ProgressBar pb = (ProgressBar) rootView.findViewById(R.id.progressBarLevel);
        double left_sum = (100 * (saved)) / (savedPrevious + incomeCurrent);
        if (left_sum <= 0)
            pb.setProgress(100);
        else if (left_sum >= 100)
            pb.setProgress(1);
        else
            pb.setProgress((int) left_sum);

    }
}

