package com.nuron.minexpense.DBHelper;

/**
 * Created by sunil on 24-May-15.
 */
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;

public class SQLiteDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Transactions";
    public static final String TRANSACTION_TABLE_NAME = "TRANSACTIONS";
    public static final String TRANSACTION_ID = "_id";
    public static final String TRANSACTION_NAME = "TRANSACTION_NAME";
    public static final String TRANSACTION_AMOUNT = "TRANSACTION_AMOUNT";
    public static final String TRANSACTION_CATEGORY = "TRANSACTION_CATEGORY";
    public static final String TRANSACTION_ARTID = "TRANSACTION_ARTID";
    public static final String TRANSACTION_TIME = "TRANSACTION_TIME";
    public static final String TRANSACTION_INCOMEOREXPENSE = "TRANSACTION_INCOMEOREXPENSE";
    private static final int DATABASE_VERSION = 1;
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


    public void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TRANSACTION_TABLE_NAME);
        db.close();
    }


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



    public Transaction getTransactionfromCursor(Cursor cursor)
    {

        return new Transaction(cursor.getString(cursor.getColumnIndex(TRANSACTION_NAME)),
                cursor.getString(cursor.getColumnIndex(TRANSACTION_AMOUNT)),
                cursor.getString(cursor.getColumnIndex(TRANSACTION_CATEGORY)),
                cursor.getString(cursor.getColumnIndex(TRANSACTION_ARTID)),
                cursor.getString(cursor.getColumnIndex(TRANSACTION_TIME)),
                cursor.getString(cursor.getColumnIndex(TRANSACTION_INCOMEOREXPENSE)));
    }

    public Bundle getSumAll() {

        Bundle transactionSumBundle = new Bundle();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT " + TRANSACTION_INCOMEOREXPENSE + ", sum(" + TRANSACTION_AMOUNT + ") FROM " + TRANSACTION_TABLE_NAME + " GROUP BY " + TRANSACTION_INCOMEOREXPENSE + " ;", null);

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

        cursor.close();
        db.close();
        return transactionSumBundle;
    }
}


