package com.nuron.minexpense.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by sunil on 28-Jun-15.
 */
public class IncomeFragment extends Fragment {
    public static final String TAG = "TransactionsFragment";
    boolean update = false;
    Uri uri;
    Utilities utilities;
    saveIncomeListener mListener;
    View rootView;
    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String month, day;

            if (monthOfYear < 10)
                month = "0" + Integer.toString(monthOfYear + 1);
            else
                month = Integer.toString(monthOfYear + 1);

            if (dayOfMonth < 10)
                day = "0" + dayOfMonth;
            else
                day = Integer.toString(dayOfMonth);

            String selectedDate = utilities.getDateFormat(String.valueOf(year) + "-" + month + "-" + day, Utilities.FROM_DATE_PICKER_TO_EDIT_TEXT);
            EditText date = (EditText) rootView.findViewById(R.id.add_date);
            date.setText(selectedDate);
        }
    };
    private SQLiteDBHelper sqLiteDBHelper;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (saveIncomeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement saveExpenseListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.expense_fragment, container, false);


        sqLiteDBHelper = new SQLiteDBHelper(getActivity());


        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.image_list);
        rv.setHasFixedSize(true);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        rv.setLayoutManager(layoutManager);

        List<Integer> artImageId = addImageId();

        final RecyclerAdapter recyclerAdapter = new RecyclerAdapter(artImageId);
        rv.setAdapter(recyclerAdapter);

        utilities = new Utilities(getActivity());

        final EditText name = (EditText) rootView.findViewById(R.id.add_name);
        //final EditText amount = (EditText)  rootView.findViewById(R.id.add_amount_material);
        final EditText amount = (EditText) rootView.findViewById(R.id.add_amount);
        final EditText category = (EditText) rootView.findViewById(R.id.add_category);
        final EditText date = (EditText) rootView.findViewById(R.id.add_date);


        Bundle transactionBundle = getArguments();
        if (transactionBundle != null) {
            update = true;
            name.setText(transactionBundle.getString("name"));
            amount.setText(transactionBundle.getString("amount"));
            category.setText(transactionBundle.getString("category"));
            int selectedId = Integer.parseInt(transactionBundle.getString("artId"));
            if (selectedId != -1)
                recyclerAdapter.setSelectedItem(selectedId);
            date.setText(utilities.getDateFormat(transactionBundle.getString("date"), Utilities.FROM_DB_TO_EDIT_TEXT));
            uri = Uri.parse(TransactionProvider.CONTENT_URI + "/" + transactionBundle.getString("position"));
        }

        Button save = (Button) rootView.findViewById(R.id.save);
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
                    getActivity().getContentResolver().update(uri, contentValues, null, null);
                else
                    getActivity().getContentResolver().insert(TransactionProvider.CONTENT_URI, contentValues);

                mListener.saveIncome();
            }
        });

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        return rootView;
    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();

        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        date.setCallBack(ondate);
        date.show(getActivity().getSupportFragmentManager(), "Date Picker");
    }

    private List<Integer> addImageId() {
        List<Integer> artImageId;
        artImageId = new ArrayList<>();

        artImageId.add(R.drawable.batman);
        artImageId.add(R.drawable.star);
        artImageId.add(R.drawable.batman);
        artImageId.add(R.drawable.trash);
        artImageId.add(R.drawable.star);
        artImageId.add(R.drawable.batman);
        artImageId.add(R.drawable.trash);

        return artImageId;
    }

    public interface saveIncomeListener {
        void saveIncome();
    }
}

