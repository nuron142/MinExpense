package com.nuron.minexpense;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by sunil on 24-May-15.
 */
public class TransactionCursorAdaptor extends CursorAdapter {

    private static final int TYPE_INCOME = 0;
    private static final int TYPE_EXPENSE = 1;


    public TransactionCursorAdaptor(Context context, Cursor cursor) {
        super(context, cursor, true);
    }


    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor)getItem(position);
        return getItemViewType(cursor);
    }


    private int getItemViewType(Cursor cursor){
        String type = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE));
        if (type.equals("0")) {
            return TYPE_INCOME;
        } else if(type.equals("1")){
            return TYPE_EXPENSE;
        }else{
            return -1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        String messageType = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE));
        View view;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(messageType.equals("0")){
            view = inflater.inflate(R.layout.income_list, null);
            view.setTag(TYPE_INCOME);
        }else{
            view = inflater.inflate(R.layout.expense_list, null);
            view.setTag(TYPE_EXPENSE);
        }
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        int tag = (int)view.getTag();

        if(tag == TYPE_INCOME)
        {
            String name = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.TRANSACTION_NAME));
            TextView nameText = (TextView)view.findViewById(R.id.name);
            nameText.setText(name);
            String amount = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.TRANSACTION_AMOUNT));
            TextView amountText = (TextView)view.findViewById(R.id.amount);
            amountText.setText(amount);
        }
        else if(tag == TYPE_EXPENSE)
        {
            String name = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.TRANSACTION_NAME));
            TextView nameText = (TextView)view.findViewById(R.id.name);
            nameText.setText(name);
            String amount = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.TRANSACTION_AMOUNT));
            TextView amountText = (TextView)view.findViewById(R.id.amount);
            amountText.setText(amount);
        }
    }
}

