package com.nuron.minexpense.Fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
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
    double income_sum_final = 0, expense_sum_final = 0, left_sum_final = 0;
    View rootView;
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

        mListView = (ListView) rootView.findViewById(R.id.list);
        transactionCursorAdapter = new TransactionCursorAdaptor(getActivity(), null);
        mListView.setAdapter(transactionCursorAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(0, null, this);
        getLoaderManager().restartLoader(1, null, this);
    }

    //region Loader Implementation
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == 0) {
            Log.d("startDate 2", startDate);
            Log.d("endDate 2", endDate);

            String selection = SQLiteDBHelper.TRANSACTION_TIME + " BETWEEN ? AND ? ";

            String[] selectionArgs = new String[]{startDate, endDate};

            Uri uri = TransactionProvider.CONTENT_URI;
            return new CursorLoader(getActivity(), uri, null, selection, selectionArgs, null);
        } else {
            String[] selectionArgs = new String[]{startDate, endDate};

            String[] projection = new String[]{SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE,
                    "SUM(" + SQLiteDBHelper.TRANSACTION_AMOUNT + ") AS SUM_TOTAL"};
            String selection = SQLiteDBHelper.TRANSACTION_TIME + " BETWEEN ? AND ? " + " GROUP BY " + SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE;

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
    //endregion


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
}

