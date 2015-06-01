package com.nuron.minexpense;

/**
 * Created by sunil on 24-May-15.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

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


    public SQLiteDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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


    public void insertTransactions(SQLiteDatabase db, ArrayList<Transaction> transactions){

        for(Transaction transaction : transactions){
            ContentValues contentValues = createRowContent(transaction.getName(),
                    transaction.getAmount(),transaction.getCategory(),transaction.getArtId(),transaction.getTime(),transaction.getIncomeOrExpense());
            db.insert(TRANSACTION_TABLE_NAME, null, contentValues);
        }

    }

    public void insertTransaction(Transaction transaction){
        SQLiteDatabase db = this.getWritableDatabase();
            Log.d("1","insert transactions");
            ContentValues contentValues = createRowContent(transaction.getName(),
                    transaction.getAmount(),transaction.getCategory(),transaction.getArtId(),transaction.getTime(),transaction.getIncomeOrExpense());
            db.insert(TRANSACTION_TABLE_NAME, null, contentValues);


    }

    private ContentValues createRowContent(String transactionName, String transactionAmount, String transactionCategory,
                                           String transactionArtId,String transactionTime,String transactionIncomeOrExpense){

        Log.d("3","create row content");
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRANSACTION_NAME, transactionName);
        contentValues.put(TRANSACTION_AMOUNT,transactionAmount );
        contentValues.put(TRANSACTION_CATEGORY, transactionCategory);
        contentValues.put(TRANSACTION_ARTID, transactionArtId);
        contentValues.put(TRANSACTION_TIME, transactionTime);
        contentValues.put(TRANSACTION_INCOMEOREXPENSE, transactionIncomeOrExpense);
        return contentValues;
    }

    public Cursor getMessages(){
        Log.d("2","get transactions");
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "select * from "+ TRANSACTION_TABLE_NAME, null);
    }




}


