package com.nuron.minexpense;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirections;

import java.util.jar.Attributes;


public class Homepage extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

    private TransactionCursorAdaptor transactionCursorAdapter;
    protected SwipeActionAdapter mAdapter;
    private SQLiteDBHelper sqliteDBHelper;
    private  ListView mListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        mListView = (ListView) findViewById(R.id.list);

        sqliteDBHelper = new SQLiteDBHelper(this);
        Cursor cursor = getContentResolver().query(TransactionProvider.CONTENT_URI, null, null, null, null);;

        transactionCursorAdapter = new TransactionCursorAdaptor(this, cursor);
        mListView.setAdapter(transactionCursorAdapter);
        getLoaderManager().initLoader(0, null, this);

        Button add_expense  = (Button) findViewById(R.id.add_expense);
        add_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent = new Intent("com.nuron.minexpense.EXPENSE");

//                intent.putExtra("message", "Hello From MainActivity");
//
//                // 3. or you can add data to a bundle
//                Bundle extras = new Bundle();
//                extras.putString("status", "Data Received!");
//
//                // 4. add bundle to intent
//                intent.putExtras(extras);

                // 5. start the activity
                startActivity(intent);
            }
        });

        Button add_income  = (Button) findViewById(R.id.add_income);
        add_income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Homepage.this, "Income", Toast.LENGTH_SHORT).show();
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

}

