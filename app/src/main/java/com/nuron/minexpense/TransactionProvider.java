package com.nuron.minexpense;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by sunil on 29-May-15.
 */
public class TransactionProvider extends ContentProvider {

    public static final String PROVIDER_NAME = "com.nuron.minexpense.messageprovider";
    private static final String TRANSACTION_TABLE = "transactions";

    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/"+ TRANSACTION_TABLE );

    private static final int TRANSACTIONS = 1;
    private static final int TRANSACTIONS_ID = 2;

    private static final UriMatcher uriMatcher ;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, TRANSACTION_TABLE, TRANSACTIONS);
        uriMatcher.addURI(PROVIDER_NAME, TRANSACTION_TABLE + "/#", TRANSACTIONS_ID);
    }

    SQLiteDBHelper sqliteDBHelper;

    @Override
    public boolean onCreate() {
        sqliteDBHelper = new SQLiteDBHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = sqliteDBHelper.getWritableDatabase();
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(sqliteDBHelper.TRANSACTION_TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case TRANSACTIONS:
                //do nothing
                break;
            case TRANSACTIONS_ID:
                String id = uri.getPathSegments().get(1);
                queryBuilder.appendWhere(sqliteDBHelper.TRANSACTION_ID + "=" + id);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        int uriType = uriMatcher.match(uri);

        SQLiteDatabase db = sqliteDBHelper.getWritableDatabase();

        long id = 0;
        switch (uriType) {
            case TRANSACTIONS:
                id = db.insert(sqliteDBHelper.TRANSACTION_TABLE_NAME,null, contentValues);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(TRANSACTION_TABLE + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase dB = sqliteDBHelper.getWritableDatabase();
        int rowsDeleted = 0;

        switch (uriType) {
            case TRANSACTIONS:
                rowsDeleted = dB.delete(sqliteDBHelper.TRANSACTION_TABLE_NAME,
                        selection,
                        selectionArgs);
                break;

            case TRANSACTIONS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = dB.delete(sqliteDBHelper.TRANSACTION_TABLE_NAME,
                            sqliteDBHelper.TRANSACTION_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = dB.delete(sqliteDBHelper.TRANSACTION_TABLE_NAME,
                            sqliteDBHelper.TRANSACTION_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        SQLiteDatabase db = sqliteDBHelper.getWritableDatabase();
        int updateCount = 0;

        switch (uriMatcher.match(uri)) {
            case TRANSACTIONS:
                //do nothing
                break;
            case TRANSACTIONS_ID:

                String id = uri.getLastPathSegment();
//                String where = sqliteDBHelper.TRANSACTION_ID + " = " + uri.getLastPathSegment();
//                if (TextUtils.isEmpty(selection)) {
//                    where += " AND "+ selection;
//                }

                updateCount = db.update(sqliteDBHelper.TRANSACTION_TABLE_NAME,contentValues,sqliteDBHelper.TRANSACTION_ID + "=" + id,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return updateCount;
    }
}

