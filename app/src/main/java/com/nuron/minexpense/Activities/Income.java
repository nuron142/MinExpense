package com.nuron.minexpense.Activities;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.nuron.minexpense.Adapters.RecyclerAdapter;
import com.nuron.minexpense.ContentProvider.TransactionProvider;
import com.nuron.minexpense.DBHelper.SQLiteDBHelper;
import com.nuron.minexpense.DBHelper.Transaction;
import com.nuron.minexpense.R;
import com.nuron.minexpense.Utilities.DatePickerFragment;
import com.nuron.minexpense.Utilities.Utilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by sunil on 07-Jun-15.
 */
public class Income extends AppCompatActivity{
    boolean update=false;
    Uri uri;
    Utilities utilities;
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

            String selectedDate = utilities.getDateFormat(String.valueOf(year) + "-" + month + "-" + day, Utilities.FROM_DATE_PICKER_TO_EDIT_TEXT);
            EditText date = (EditText) findViewById(R.id.add_date);
            date.setText(selectedDate);
        }
    };
    private SQLiteDBHelper sqLiteDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense);
        sqLiteDBHelper = new SQLiteDBHelper(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        RecyclerView rv = (RecyclerView)findViewById(R.id.image_list);
        rv.setHasFixedSize(true);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        rv.setLayoutManager(layoutManager);

        List<Integer> artImageId = addImageId();

        final RecyclerAdapter recyclerAdapter = new RecyclerAdapter(artImageId);
        rv.setAdapter(recyclerAdapter);

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
            int selectedId=Integer.parseInt(transactionBundle.getString("artId"));
            if(selectedId != -1)
                recyclerAdapter.setSelectedItem(selectedId);
            date.setText(utilities.getDateFormat(transactionBundle.getString("date"), Utilities.FROM_DB_TO_EDIT_TEXT));
            uri = Uri.parse(TransactionProvider.CONTENT_URI + "/" + transactionBundle.getString("position"));
        }

        Button save  = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Transaction transaction = new Transaction(name.getText().toString(),
                        amount.getText().toString(),
                        category.getText().toString(),
                        Integer.toString(recyclerAdapter.getSelectedItem()),
                        utilities.getDateFormat(date.getText().toString(), Utilities.FROM_EDIT_TEXT_TO_DB),
                        Integer.toString(Utilities.TYPE_INCOME));

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

    private List<Integer> addImageId(){
        List<Integer> artImageId;
        artImageId = new ArrayList<>();
        artImageId.add(R.drawable.star);
        artImageId.add(R.drawable.star);
        artImageId.add(R.drawable.trash);
        artImageId.add(R.drawable.star);
        artImageId.add(R.drawable.star);
        artImageId.add(R.drawable.trash);
        return artImageId;
    }
}
