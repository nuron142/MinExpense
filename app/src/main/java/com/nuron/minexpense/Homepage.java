package com.nuron.minexpense;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nuron.minexpense.Activities.BaseActivity;
import com.nuron.minexpense.Adapters.TransactionCursorAdaptor;
import com.nuron.minexpense.ContentProvider.TransactionProvider;
import com.nuron.minexpense.DBHelper.SQLiteDBHelper;

import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Homepage extends BaseActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    double income_sum_final = 0, expense_sum_final = 0, left_sum_final = 0;
    double income_sum_intial = 0, expense_sum_intial = 0, left_sum_intial = 0;
    private TransactionCursorAdaptor transactionCursorAdapter;
    private ListView mListView;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        super.onCreateNavigationView();
        handler = new Handler();

        mListView = (ListView) findViewById(R.id.list);

        getLoaderManager().initLoader(0, null, this);
        getLoaderManager().initLoader(1, null, this);
        transactionCursorAdapter = new TransactionCursorAdaptor(this, null);
        mListView.setAdapter(transactionCursorAdapter);


        Button add_expense  = (Button) findViewById(R.id.add_expense);
        add_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.nuron.minexpense.ADD_EXPENSE");
                startActivity(intent);
            }
        });

        Button add_income  = (Button) findViewById(R.id.add_income);
        add_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.nuron.minexpense.ADD_INCOME");
                startActivity(intent);
            }
        });

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
            return new CursorLoader(this, uri, null, selection, selectionArgs, null);
        } else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            String today_start = dateFormat.format(date) + " 00:00:00";
            String today_end = dateFormat.format(date) + " 23:59:59";

            String[] projection = new String[]{SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE,
                    "SUM(" + SQLiteDBHelper.TRANSACTION_AMOUNT + ") AS SUM_TOTAL"};
            String selection = SQLiteDBHelper.TRANSACTION_TIME + " BETWEEN ? AND ? " + " GROUP BY " + SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE;

            String[] selectionArgs = new String[]{today_start, today_end};

            Uri uri = TransactionProvider.CONTENT_URI;
            return new CursorLoader(this, uri, projection, selection, selectionArgs, null);
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
        else {
            TextView incomeText = (TextView) findViewById(R.id.income_sum);
            incomeText.setText("0");

            TextView expenseText = (TextView) findViewById(R.id.expense_sum);
            expenseText.setText("0");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
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

        if (transactionSumBundle != null) {
            income_sum_final = Double.parseDouble(transactionSumBundle.getString("income_sum"));
            expense_sum_final = Double.parseDouble(transactionSumBundle.getString("expense_sum"));
        }

        DecimalFormat formatter = new DecimalFormat("#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        formatter.setRoundingMode(RoundingMode.HALF_UP);

        TextView incomeText = (TextView) findViewById(R.id.income_sum);
        incomeText.setText(formatter.format(income_sum_final));

        TextView expenseText = (TextView) findViewById(R.id.expense_sum);
        expenseText.setText(formatter.format(expense_sum_final));



        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarLevel);
        left_sum_final = (100 * (income_sum_final - expense_sum_final)) / income_sum_final;
        if (left_sum_final <= 0)
            pb.setProgress(100);
        else if (left_sum_final >= 100)
            pb.setProgress(1);
        else
            pb.setProgress((int) left_sum_final);

        /*
        //Animate number change in textview

        animateTextView((int) income_sum_intial,(int) income_sum_final,incomeText);
        animateTextView((int) expense_sum_intial,(int) expense_sum_final,expenseText);

        income_sum_intial= income_sum_final;
        expense_sum_intial= expense_sum_final;
        left_sum_intial= left_sum_final;
        */

    }

    public void animateTextView(int initialValue, int finalValue, final TextView textview) {
        DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator(0.6f);
        int start = Math.min(initialValue, finalValue);
        int end = Math.max(initialValue, finalValue);
        int difference = Math.abs(finalValue - initialValue);
        Handler handler = new Handler();
        for (int count = start; count <= end; count++) {
            int time = Math.round(decelerateInterpolator.getInterpolation((((float) count) / difference)) * 100) * count;
            final int finalCount = ((initialValue > finalValue) ? initialValue - count : count);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    textview.setText(finalCount + "");
                }
            }, time);
        }
    }

}

