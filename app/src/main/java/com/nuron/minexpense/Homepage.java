package com.nuron.minexpense;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeDirections;


public class Homepage extends ListActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        SwipeActionAdapter.SwipeActionListener{

    private TransactionCursorAdaptor messageCursorAdapter;
    protected SwipeActionAdapter mAdapter;
    private SQLiteDBHelper sqliteDBHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        sqliteDBHelper = new SQLiteDBHelper(this);

        sqliteDBHelper.insertTransaction(new Transaction("Pari","10","a","b","c","0"));
        sqliteDBHelper.insertTransaction(new Transaction("Pari","100","a","b","c","1"));
        sqliteDBHelper.insertTransaction(new Transaction("Sunil","10","a","b","c","0"));
        Cursor cursor = sqliteDBHelper.getMessages();

        messageCursorAdapter = new TransactionCursorAdaptor(this, cursor);

        mAdapter = new SwipeActionAdapter(messageCursorAdapter);
        mAdapter.setSwipeActionListener(this)
                .setDimBackgrounds(true)
                .setListView(getListView());
        setListAdapter(mAdapter);

        mAdapter.addBackground(SwipeDirections.DIRECTION_FAR_LEFT,R.layout.row_bg_left_far)
                .addBackground(SwipeDirections.DIRECTION_NORMAL_LEFT,R.layout.row_bg_left)
                .addBackground(SwipeDirections.DIRECTION_FAR_RIGHT,R.layout.row_bg_right_far)
                .addBackground(SwipeDirections.DIRECTION_NORMAL_RIGHT,R.layout.row_bg_right);
        getLoaderManager().initLoader(0, null, this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = TransactionProvider.CONTENT_URI;
        return new CursorLoader(this, uri, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        messageCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        messageCursorAdapter.swapCursor(null);
    }

    @Override
    public boolean hasActions(int position){
        return true;
    }

    @Override
    public boolean shouldDismiss(int position, int direction){
        //return direction == SwipeDirections.DIRECTION_NORMAL_LEFT;
        return false;
    }

    @Override
    public void onSwipe(int[] positionList, int[] directionList){
        for(int i=0;i<positionList.length;i++) {
            int direction = directionList[i];
            int position = positionList[i];
            String dir = "";

            switch (direction) {
                case SwipeDirections.DIRECTION_FAR_LEFT:
                    dir = "Far left";
                    break;
                case SwipeDirections.DIRECTION_NORMAL_LEFT:
                    dir = "Left";
                    break;
                case SwipeDirections.DIRECTION_FAR_RIGHT:
                    Cursor cursor = ((TransactionCursorAdaptor)mAdapter.getAdapter()).getCursor();
                    Uri uri = Uri.parse(TransactionProvider.CONTENT_URI + "/" + sqliteDBHelper.getTransactionID(cursor,position));
                    getContentResolver().delete(uri, null, null);
                    Log.d("getItem", "I am here 6");
                    //mAdapter.notifyDataSetChanged();
                    break;
                case SwipeDirections.DIRECTION_NORMAL_RIGHT:
                    dir = "Right";
                    break;
            }
            Log.d("getItem","I am here 5");

        }
    }
}

