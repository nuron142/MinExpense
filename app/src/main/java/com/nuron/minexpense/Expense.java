package com.nuron.minexpense;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by sunil on 07-Jun-15.
 */
public class Expense extends Activity {
    private SQLiteDBHelper sqLiteDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense);
        sqLiteDBHelper = new SQLiteDBHelper(this);

        final EditText name = (EditText) findViewById(R.id.add_name);
        final EditText amount = (EditText) findViewById(R.id.add_amount);
        final EditText category = (EditText) findViewById(R.id.add_category);
        final EditText date = (EditText) findViewById(R.id.add_date);

        Bundle transactionBundle = getIntent().getExtras();
        if(transactionBundle != null)
        {
            name.setText(transactionBundle.getString("name"));
            amount.setText(transactionBundle.getString("amount"));
            category.setText(transactionBundle.getString("category"));
            date.setText(transactionBundle.getString("time"));
        }

        Button save  = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Transaction transaction = new Transaction(name.getText().toString(),
                        amount.getText().toString(),
                        category.getText().toString(),
                        "b",
                        date.getText().toString(),"1");

                ContentValues contentValues = sqLiteDBHelper.createRowContent(transaction);
                getContentResolver().insert(TransactionProvider.CONTENT_URI, contentValues);
                finish();
            }
        });
    }
}
