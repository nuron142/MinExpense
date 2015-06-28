package com.nuron.minexpense.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.nuron.minexpense.Adapters.TransactionCursorAdaptor;
import com.nuron.minexpense.Fragments.TransactionFragment;
import com.nuron.minexpense.R;

/**
 * Created by sunil on 20-Jun-15.
 */
public class Transactions extends BaseActivity {

    private TransactionCursorAdaptor transactionCursorAdapter;
    private ListView mListView;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transactions);

//        handler = new Handler();

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_up);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//
//        mListView = (ListView) findViewById(R.id.list);
//
//        getLoaderManager().initLoader(0, null, this);
//        getLoaderManager().initLoader(1, null, this);
//
//        transactionCursorAdapter = new TransactionCursorAdaptor(this, null);
//        mListView.setAdapter(transactionCursorAdapter);

        if (findViewById(R.id.fragment_container) != null) {

            TransactionFragment firstFragment = new TransactionFragment();
            firstFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();

        }
    }
//
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        if (id == 0) {
//            Uri uri = TransactionProvider.CONTENT_URI;
//            return new CursorLoader(this, uri, null, null, null, null);
//        } else {
//            String[] projection = new String[]{SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE,
//                    "SUM(" + SQLiteDBHelper.TRANSACTION_AMOUNT + ") AS SUM_TOTAL"};
//            String selection = " 0 == 0 GROUP BY " + SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE;
//
//            Uri uri = TransactionProvider.CONTENT_URI;
//            return new CursorLoader(this, uri, projection, selection, null, null);
//        }
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//        if (loader.getId() == 0) {
//            transactionCursorAdapter.swapCursor(cursor);
//        } else {
//            final Cursor cursor1 = cursor;
//            handler.post(new Runnable() {
//                public void run() {
//                    updateSum(cursor1);
//                }
//            });
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//        if (loader.getId() == 0)
//            transactionCursorAdapter.swapCursor(null);
//        else {
//            TextView incomeText = (TextView) findViewById(R.id.income_sum);
//            incomeText.setText("0");
//
//            TextView expenseText = (TextView) findViewById(R.id.expense_sum);
//            expenseText.setText("0");
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//    }
//
//    public void updateSum(Cursor cursor) {
//        double income_sum = 0, expense_sum = 0, left_sum = 0;
//        Bundle transactionSumBundle = new Bundle();
//
//        transactionSumBundle.putString("income_sum", "0");
//        transactionSumBundle.putString("expense_sum", "0");
//
//        while (cursor.moveToNext()) {
//            switch (cursor.getString(0)) {
//                case "0":
//                    transactionSumBundle.putString("income_sum", cursor.getString(1));
//                    break;
//                case "1":
//                    transactionSumBundle.putString("expense_sum", cursor.getString(1));
//                    break;
//            }
//        }
//
//        if (transactionSumBundle != null) {
//            income_sum = Double.parseDouble(transactionSumBundle.getString("income_sum"));
//            expense_sum = Double.parseDouble(transactionSumBundle.getString("expense_sum"));
//        }
//
//        DecimalFormat formatter = new DecimalFormat("#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
//        formatter.setRoundingMode(RoundingMode.HALF_UP);
//
//        TextView incomeText = (TextView) findViewById(R.id.income_sum);
//        incomeText.setText(formatter.format(income_sum));
//
//        TextView expenseText = (TextView) findViewById(R.id.expense_sum);
//        expenseText.setText(formatter.format(expense_sum));
//
//        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBarLevel);
//        left_sum = (100 * (income_sum - expense_sum)) / income_sum;
//        if (left_sum <= 0)
//            pb.setProgress(100);
//        else if (left_sum >= 100)
//            pb.setProgress(1);
//        else
//            pb.setProgress((int) left_sum);
//
//    }
}