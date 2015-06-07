package com.nuron.minexpense;

/**
 * Created by sunil on 24-May-15.
 */
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class SQLiteDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Transactions";
    public static final String TRANSACTION_TABLE_NAME = "TRANSACTIONS";
    public static final String TRANSACTION_ID = "_id";
    public static final String TRANSACTION_NAME = "TRANSACTION_NAME";
    public static final String TRANSACTION_AMOUNT = "TRANSACTION_AMOUNT";
    public static final String TRANSACTION_CATEGORY = "TRANSACTION_CATEGORY";
    public static final String TRANSACTION_ARTID = "TRANSACTION_ARTID";
    public static final String TRANSACTION_TIME = "TRANSACTION_TIME";
    public static final String TRANSACTION_INCOMEOREXPENSE = "TRANSACTION_INCOMEOREXPENSE";

    private ContentResolver contentResolver;

    public SQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        contentResolver=context.getContentResolver();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table " + TRANSACTION_TABLE_NAME + " " +
                        "(" + TRANSACTION_ID + " integer primary key autoincrement, "
                        + TRANSACTION_NAME + " text not null, "
                        + TRANSACTION_AMOUNT + " text not null, "
                        + TRANSACTION_CATEGORY + " text not null, "
                        + TRANSACTION_ARTID + " text not null, "
                        + TRANSACTION_TIME + " text not null, "
                        + TRANSACTION_INCOMEOREXPENSE + " text not null)"
        );
        Log.d("4", "create table");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion, int newVersion)
    {
        if (newVersion == 1)
        {
            db.execSQL("DROP TABLE IF EXISTS " + TRANSACTION_TABLE_NAME);
            onCreate(db);
        }
    }

    public void insertTransaction(Transaction transaction) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(TRANSACTION_NAME, transaction.getName());
        contentValues.put(TRANSACTION_AMOUNT,transaction.getAmount());
        contentValues.put(TRANSACTION_CATEGORY, transaction.getCategory());
        contentValues.put(TRANSACTION_ARTID, transaction.getArtId());
        contentValues.put(TRANSACTION_TIME, transaction.getTime());
        contentValues.put(TRANSACTION_INCOMEOREXPENSE, transaction.getIncomeOrExpense());

        contentResolver.insert(TransactionProvider.CONTENT_URI, contentValues);
    }

    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TRANSACTION_TABLE_NAME);
        db.close();
    }

//
//    public void insertTransaction(Transaction transaction){
//        SQLiteDatabase db = this.getWritableDatabase();
//        Log.d("1", "insert transactions");
//        ContentValues contentValues = createRowContent(transaction);
//        db.insert(TRANSACTION_TABLE_NAME, null, contentValues);
//    }

    public ContentValues createRowContent(Transaction transaction){

        Log.d("3","create row content");
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRANSACTION_NAME, transaction.getName());
        contentValues.put(TRANSACTION_AMOUNT,transaction.getAmount());
        contentValues.put(TRANSACTION_CATEGORY, transaction.getCategory());
        contentValues.put(TRANSACTION_ARTID, transaction.getArtId());
        contentValues.put(TRANSACTION_TIME, transaction.getTime());
        contentValues.put(TRANSACTION_INCOMEOREXPENSE, transaction.getIncomeOrExpense());
        return contentValues;
    }


    public String getTransactionID(Cursor cursor,int position){
        SQLiteDatabase db = this.getWritableDatabase();
        cursor.moveToPosition(position);
        String id = cursor.getString(cursor.getColumnIndex(TRANSACTION_ID));
        return id;
    }

    public String getTransactionID(Cursor cursor){
        cursor.moveToFirst();
        String id = cursor.getString(cursor.getColumnIndex(TRANSACTION_ID));
        return id;
    }

    public Transaction getTransactionfromCursor(Cursor cursor)
    {
        Log.d("getItem","I am here 4");
        Transaction transaction = new Transaction(cursor.getString(cursor.getColumnIndex(TRANSACTION_NAME)),
                cursor.getString(cursor.getColumnIndex(TRANSACTION_AMOUNT)),
                cursor.getString(cursor.getColumnIndex(TRANSACTION_CATEGORY)),
                cursor.getString(cursor.getColumnIndex(TRANSACTION_ARTID)),
                cursor.getString(cursor.getColumnIndex(TRANSACTION_TIME)),
                cursor.getString(cursor.getColumnIndex(TRANSACTION_INCOMEOREXPENSE)));

        return transaction;
    }

}


