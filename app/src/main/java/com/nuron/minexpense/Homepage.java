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

import com.nuron.minexpense.Adapters.TransactionCursorAdaptor;
import com.nuron.minexpense.DBHelper.SQLiteDBHelper;
import com.nuron.minexpense.DBHelper.TransactionProvider;

public class Homepage extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private TransactionCursorAdaptor transactionCursorAdapter;
    private SQLiteDBHelper sqliteDBHelper;
    private  ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.list);

        sqliteDBHelper = new SQLiteDBHelper(this);
        Cursor cursor = getContentResolver().query(TransactionProvider.CONTENT_URI, null, null, null, null);

        transactionCursorAdapter = new TransactionCursorAdaptor(this, cursor);
        mListView.setAdapter(transactionCursorAdapter);
        getLoaderManager().initLoader(0, null, this);

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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        transactionCursorAdapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();
       // getContentResolver().notifyChange(TransactionProvider.CONTENT_URI,null);
    }
}

