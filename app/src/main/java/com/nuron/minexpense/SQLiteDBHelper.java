package com.nuron.minexpense;

/**
 * Created by sunil on 24-May-15.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class SQLiteDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Transactions.db";
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
                        "(" + TRANSACTION_ID + " text primary key, "
                        + TRANSACTION_NAME + " text, "
                        + TRANSACTION_AMOUNT + " text, "
                        + TRANSACTION_CATEGORY + " text, "
                        + TRANSACTION_ARTID + " text, "
                        + TRANSACTION_TIME + " text, "
                        + TRANSACTION_INCOMEOREXPENSE + " text)"
        );

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
        db.execSQL("DROP TABLE IF EXISTS "+ TRANSACTION_TABLE_NAME);
        db.close();
    }


    public void insertTransaction(SQLiteDatabase db, Transaction transactions){

        ContentValues contentValues = createRowContent(transactions);
        db.insert(TRANSACTION_TABLE_NAME, null, contentValues);
    }

    private ContentValues createRowContent(Transaction transactions){

        ContentValues contentValues = new ContentValues();
        contentValues.put(TRANSACTION_ID, transactions.getId());
        contentValues.put(TRANSACTION_NAME, transactions.getName());
        contentValues.put(TRANSACTION_AMOUNT, transactions.getAmount());
        contentValues.put(TRANSACTION_CATEGORY, transactions.getCategory());
        contentValues.put(TRANSACTION_ARTID, transactions.getArtId());
        contentValues.put(TRANSACTION_TIME, transactions.getTime());
        contentValues.put(TRANSACTION_INCOMEOREXPENSE, transactions.getIncomeOrExpense());
        return contentValues;
    }



    public Cursor getMessages(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "select * from "+ TRANSACTION_TABLE_NAME, null);
    }




}


