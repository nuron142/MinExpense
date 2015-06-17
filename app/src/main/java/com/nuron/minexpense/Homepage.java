package com.nuron.minexpense;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.nuron.minexpense.Adapters.TransactionCursorAdaptor;
import com.nuron.minexpense.ContentProvider.TransactionProvider;
import com.nuron.minexpense.DBHelper.SQLiteDBHelper;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Homepage extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private TransactionCursorAdaptor transactionCursorAdapter;
    private SQLiteDBHelper sqliteDBHelper;
    private ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.list);

        sqliteDBHelper = new SQLiteDBHelper(this);
        Cursor cursor = getContentResolver().query(TransactionProvider.CONTENT_URI, null, null, null, null);

        transactionCursorAdapter = new TransactionCursorAdaptor(this, cursor);
        mListView.setAdapter(transactionCursorAdapter);
        getLoaderManager().initLoader(0, null, this);

        //updateSum();

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
        Uri uri = TransactionProvider.CONTENT_URI;
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        transactionCursorAdapter.swapCursor(data);
        updateSum();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        transactionCursorAdapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void updateSum() {

        double income_sum = 0, expense_sum = 0, left_sum = 0;

        Bundle transactionSumBundle = sqliteDBHelper.getSumAll();
        if (transactionSumBundle != null) {
            income_sum = Double.parseDouble(transactionSumBundle.getString("income_sum"));
            expense_sum = Double.parseDouble(transactionSumBundle.getString("expense_sum"));
        }

        DecimalFormat formatter = new DecimalFormat("#.##", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        formatter.setRoundingMode(RoundingMode.DOWN);

        TextView incomeText = (TextView) findViewById(R.id.income_sum);
        incomeText.setText(String.format("%1$,.2f", income_sum));

        TextView expenseText = (TextView) findViewById(R.id.expense_sum);
        expenseText.setText(formatter.format(expense_sum));

    }
}

