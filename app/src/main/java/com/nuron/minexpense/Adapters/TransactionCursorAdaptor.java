package com.nuron.minexpense.Adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.CursorSwipeAdapter;
import com.nuron.minexpense.R;
import com.nuron.minexpense.DBHelper.SQLiteDBHelper;
import com.nuron.minexpense.DBHelper.Transaction;
import com.nuron.minexpense.DBHelper.TransactionProvider;
import com.nuron.minexpense.Utilities.Utilities;

/**
 * Created by sunil on 24-May-15.
 */
public class TransactionCursorAdaptor extends CursorSwipeAdapter {

    private static final int TYPE_INCOME = 0;
    private static final int TYPE_EXPENSE = 1;
    Utilities utilities;


    public TransactionCursorAdaptor(Context context, Cursor cursor) {
        super(context, cursor, true);
        utilities=new Utilities();
    }

    @Override
    public void closeAllItems() {
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public int getItemViewType(int position) {
        Cursor cursor = (Cursor)getItem(position);
        return getItemViewType(cursor);
    }


    private int getItemViewType(Cursor cursor){
        String type = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE));
        switch (type) {
            case "0":
                return Utilities.TYPE_INCOME;
            case "1":
                return Utilities.TYPE_EXPENSE;
            default:
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
    public void bindView(View view, final Context context, Cursor cursor) {

        SQLiteDBHelper sqLiteDBHelper = new SQLiteDBHelper(context);
        final SwipeLayout swipeLayout = (SwipeLayout)view.findViewById(getSwipeLayoutResourceId(cursor.getColumnIndex(SQLiteDBHelper.TRANSACTION_ID)));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, view.findViewById(R.id.bottom_layer1));
        swipeLayout.addDrag(SwipeLayout.DragEdge.Right, view.findViewById(R.id.bottom_layer));

        final Transaction transaction = sqLiteDBHelper.getTransactionfromCursor(cursor);

        TextView nameText = (TextView)view.findViewById(R.id.name);
        nameText.setText(transaction.getName());

        TextView amountText = (TextView)view.findViewById(R.id.amount);
        amountText.setText(transaction.getAmount());

        TextView dateText = (TextView)view.findViewById(R.id.date);
        dateText.setText(utilities.getDateFormat(transaction.getTime(), Utilities.FROM_DB_TO_LIST_VIEW));

        final String position = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.TRANSACTION_ID));
        final String incomeOrExpense = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE));
        final Uri uri = Uri.parse(TransactionProvider.CONTENT_URI + "/" + position);

        Button delete  = (Button) view.findViewById(R.id.delte_transaction);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context.getContentResolver().delete(uri, null, null);
            }
        });

        Button edit  = (Button) view.findViewById(R.id.edit_transaction);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (incomeOrExpense.equals("0")) {
                    Intent intent = new Intent("com.nuron.minexpense.ADD_INCOME");

                    Bundle transactionBundle = new Bundle();
                    transactionBundle.putString("position", position);
                    transactionBundle.putString("name", transaction.getName());
                    transactionBundle.putString("amount", transaction.getAmount());
                    transactionBundle.putString("category", transaction.getCategory());
                    transactionBundle.putString("artId", transaction.getArtId());
                    transactionBundle.putString("date", transaction.getTime());
                    transactionBundle.putString("incomeOrExpense", transaction.getIncomeOrExpense());

                    intent.putExtras(transactionBundle);
                    v.getContext().startActivity(intent);

                } else {
                    Intent intent = new Intent("com.nuron.minexpense.ADD_EXPENSE");

                    Bundle transactionBundle = new Bundle();
                    transactionBundle.putString("position", position);
                    transactionBundle.putString("name", transaction.getName());
                    transactionBundle.putString("amount", transaction.getAmount());
                    transactionBundle.putString("category", transaction.getCategory());
                    transactionBundle.putString("artId", transaction.getArtId());
                    transactionBundle.putString("date", transaction.getTime());
                    transactionBundle.putString("incomeOrExpense", transaction.getIncomeOrExpense());

                    intent.putExtras(transactionBundle);
                    v.getContext().startActivity(intent);

                }
                swipeLayout.close();
            }
        });

       swipeLayout.close();

    }


}


