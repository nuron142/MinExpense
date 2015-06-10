package com.nuron.minexpense;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import java.util.Calendar;

/**
 * Created by sunil on 07-Jun-15.
 */
public class Expense extends AppCompatActivity {
    private SQLiteDBHelper sqLiteDBHelper;
    boolean update=false;
    Uri uri;
    Utilities utilities;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense);
        sqLiteDBHelper = new SQLiteDBHelper(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        utilities=new Utilities();

        final EditText name = (EditText) findViewById(R.id.add_name);
        final EditText amount = (EditText) findViewById(R.id.add_amount);
        final EditText category = (EditText) findViewById(R.id.add_category);
        final EditText date = (EditText) findViewById(R.id.add_date);


        Bundle transactionBundle = getIntent().getExtras();
        if(transactionBundle != null)
        {
            update=true;
            name.setText(transactionBundle.getString("name"));
            amount.setText(transactionBundle.getString("amount"));
            category.setText(transactionBundle.getString("category"));
            date.setText(utilities.getDateFormat(transactionBundle.getString("date"), utilities.FROM_DB_TO_EDIT_TEXT));
            uri = Uri.parse(TransactionProvider.CONTENT_URI + "/" + transactionBundle.getString("position"));
        }

        Button save  = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextInputLayout usernameTextInputLayout = (TextInputLayout) v.findViewById(R.id.add_name_textinput);
                
                Transaction transaction = new Transaction(name.getText().toString(),
                        amount.getText().toString(),
                        category.getText().toString(),
                        "b",
                        utilities.getDateFormat(date.getText().toString(), utilities.FROM_EDIT_TEXT_TO_DB),
                        Integer.toString(utilities.TYPE_EXPENSE));

                ContentValues contentValues = sqLiteDBHelper.createRowContent(transaction);
                if (update)
                    getContentResolver().update(uri, contentValues, null, null);
                else
                    getContentResolver().insert(TransactionProvider.CONTENT_URI, contentValues);
                finish();
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,int dayOfMonth)
        {
            String month,day;

            if(monthOfYear < 10)
                month = "0" + Integer.toString(monthOfYear+1);
            else
                month = Integer.toString(monthOfYear+1);

            if(dayOfMonth < 10)
                day  = "0" + dayOfMonth ;
            else
                day = Integer.toString(dayOfMonth);

            String selectedDate = utilities.getDateFormat(String.valueOf(year) + "-" + month + "-" + day, utilities.FROM_DATE_PICKER_TO_EDIT_TEXT);
            EditText date = (EditText) findViewById(R.id.add_date);
            date.setText(selectedDate);
        }
    };

    private void showDatePicker()
    {
        DatePickerFragment date = new DatePickerFragment();

        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        date.setCallBack(ondate);
        date.show(getSupportFragmentManager(), "Date Picker");
    }

}
