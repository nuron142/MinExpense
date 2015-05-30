package com.nuron.minexpense;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by sunil on 29-May-15.
 */
public class TransactionProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "com.nuron.minexpense.messageprovider";

    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/transactions" );

    private static final int TRANSACTIONS = 1;

    private static final UriMatcher uriMatcher ;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "transactions", TRANSACTIONS);
    }

    SQLiteDBHelper sqliteDBHelper;


    @Override
    public boolean onCreate() {
        sqliteDBHelper = new SQLiteDBHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if(uriMatcher.match(uri)== TRANSACTIONS){
            return sqliteDBHelper.getMessages();
        }else{
            return null;
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}

