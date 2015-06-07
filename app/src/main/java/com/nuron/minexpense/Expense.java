package com.nuron.minexpense;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by sunil on 07-Jun-15.
 */
public class Expense extends Activity {
    private SQLiteDBHelper sqliteDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense);
        sqliteDBHelper = new SQLiteDBHelper(this);

        Button save_expense  = (Button) findViewById(R.id.save_expense);
        save_expense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name = (EditText) findViewById(R.id.add_expense_name);
                EditText amount = (EditText) findViewById(R.id.add_expense_amount);
                EditText category = (EditText) findViewById(R.id.add_expense_category);
                EditText date = (EditText) findViewById(R.id.add_expense_date);

                Transaction transaction = new Transaction(name.getText().toString(),
                        amount.getText().toString(),
                        category.getText().toString(),
                        "b",
                        date.getText().toString(),"1");

                ContentValues contentValues = sqliteDBHelper.createRowContent(transaction);
                getContentResolver().insert(TransactionProvider.CONTENT_URI, contentValues);
                finish();
            }
        });

    }

}
