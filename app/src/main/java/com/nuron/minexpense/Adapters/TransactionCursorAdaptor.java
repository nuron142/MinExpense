package com.nuron.minexpense.Adapters;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.CursorSwipeAdapter;
import com.nuron.minexpense.ContentProvider.TransactionProvider;
import com.nuron.minexpense.DBHelper.SQLiteDBHelper;
import com.nuron.minexpense.DBHelper.Transaction;
import com.nuron.minexpense.Fragments.ExpenseFragment;
import com.nuron.minexpense.Fragments.IncomeFragment;
import com.nuron.minexpense.Homepage;
import com.nuron.minexpense.R;
import com.nuron.minexpense.Utilities.Utilities;

/**
 * Created by sunil on 24-May-15.
 */
public class TransactionCursorAdaptor extends CursorSwipeAdapter {

    private static final int TYPE_INCOME = 0;
    private static final int TYPE_EXPENSE = 1;
    private static final int TYPE_EXPENSE_HEADER = 2;
    private static final int TYPE_INCOME_HEADER = 3;
    Utilities utilities;


    public TransactionCursorAdaptor(Context context, Cursor cursor) {
        super(context, cursor, true);
        utilities = new Utilities(context);
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
        String messageType = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE));

        if (position == 0) {
            if (messageType.equals("0"))
                return TYPE_INCOME_HEADER;
            else
                return TYPE_EXPENSE_HEADER;
        } else {
            if (isNewGroup(cursor, position)) {
                if (messageType.equals("0"))
                    return TYPE_INCOME_HEADER;
                else
                    return TYPE_EXPENSE_HEADER;
            } else {
                if (messageType.equals("0"))
                    return TYPE_INCOME;
                else
                    return TYPE_EXPENSE;
            }
        }
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType;
        View view;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int position = cursor.getPosition();
        String transactionType = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE));

        if (position == 0) {
            if (transactionType.equals("0"))
                viewType = TYPE_INCOME_HEADER;
            else
                viewType = TYPE_EXPENSE_HEADER;
        } else {
            if (isNewGroup(cursor, position)) {
                if (transactionType.equals("0"))
                    viewType = TYPE_INCOME_HEADER;
                else
                    viewType = TYPE_EXPENSE_HEADER;
            } else {
                if (transactionType.equals("0"))
                    viewType = TYPE_INCOME;
                else
                    viewType = TYPE_EXPENSE;
            }
        }

        switch (viewType) {
            case TYPE_INCOME:
                view = inflater.inflate(R.layout.income_list, null);
                break;

            case TYPE_EXPENSE:
                view = inflater.inflate(R.layout.expense_list, null);
                break;

            case TYPE_INCOME_HEADER:
                view = inflater.inflate(R.layout.income_list_group_header, null);
                break;

            //TYPE_EXPENSE_HEADER
            default:
                view = inflater.inflate(R.layout.expense_list_group_header, null);
                break;
        }

        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        SQLiteDBHelper sqLiteDBHelper = new SQLiteDBHelper(context);

        final Transaction transaction = sqLiteDBHelper.getTransactionfromCursor(cursor);

        TextView dateHeaderText = (TextView) view.findViewById(R.id.date_header);
        if (dateHeaderText != null)
            dateHeaderText.setText(utilities.getDateFormat(transaction.getTime(), Utilities.FROM_DB_TO_LIST_VIEW));

        TextView nameText = (TextView)view.findViewById(R.id.name);
        nameText.setText(transaction.getName());

        TextView amountText = (TextView)view.findViewById(R.id.amount);
        amountText.setText(transaction.getAmount());

//        TextView dateText = (TextView)view.findViewById(R.id.date);
//        dateText.setText(utilities.getDateFormat(transaction.getTime(), Utilities.FROM_DB_TO_LIST_VIEW));

        TextView categoryText = (TextView)view.findViewById(R.id.category);
        categoryText.setText(transaction.getCategory());

        ImageView artImage = (ImageView)view.findViewById(R.id.artImage);
        artImage.setImageResource(getResourceId(Integer.parseInt(transaction.getArtId())));

        final String position = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.TRANSACTION_ID));
        final String incomeOrExpense = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.TRANSACTION_INCOMEOREXPENSE));
        final Uri uri = Uri.parse(TransactionProvider.CONTENT_URI + "/" + position);

        final SwipeLayout swipeLayout = (SwipeLayout) view.findViewById(getSwipeLayoutResourceId(cursor.getColumnIndex(SQLiteDBHelper.TRANSACTION_ID)));
        swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        swipeLayout.addDrag(SwipeLayout.DragEdge.Left, view.findViewById(R.id.bottom_layer1));
        swipeLayout.addDrag(SwipeLayout.DragEdge.Right, view.findViewById(R.id.bottom_layer));

        Button delete = (Button) view.findViewById(R.id.delte_transaction);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context.getContentResolver().delete(uri, null, null);
            }
        });

        Button edit = (Button) view.findViewById(R.id.edit_transaction);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (incomeOrExpense.equals("0")) {

                    Bundle transactionBundle = new Bundle();
                    transactionBundle.putString("position", position);
                    transactionBundle.putString("name", transaction.getName());
                    transactionBundle.putString("amount", transaction.getAmount());
                    transactionBundle.putString("category", transaction.getCategory());
                    transactionBundle.putString("artId", transaction.getArtId());
                    transactionBundle.putString("date", transaction.getTime());
                    transactionBundle.putString("incomeOrExpense", transaction.getIncomeOrExpense());

                    IncomeFragment incomeFragment = new IncomeFragment();
                    incomeFragment.setArguments(transactionBundle);

                    FragmentTransaction fragmentTransaction =
                            ((FragmentActivity) v.getContext())
                                    .getSupportFragmentManager()
                                    .beginTransaction();

                    fragmentTransaction.replace(R.id.fragment_container, incomeFragment);
                    fragmentTransaction.addToBackStack(null);

                    fragmentTransaction.commit();

                    ((Homepage) v.getContext()).setToolbar("EDIT INCOME");


                } else {

                    Bundle transactionBundle = new Bundle();
                    transactionBundle.putString("position", position);
                    transactionBundle.putString("name", transaction.getName());
                    transactionBundle.putString("amount", transaction.getAmount());
                    transactionBundle.putString("category", transaction.getCategory());
                    transactionBundle.putString("artId", transaction.getArtId());
                    transactionBundle.putString("date", transaction.getTime());
                    transactionBundle.putString("incomeOrExpense", transaction.getIncomeOrExpense());

                    ExpenseFragment expenseFragment = new ExpenseFragment();
                    expenseFragment.setArguments(transactionBundle);

                    FragmentTransaction fragmentTransaction =
                            ((FragmentActivity) v.getContext())
                                    .getSupportFragmentManager()
                                    .beginTransaction();

                    fragmentTransaction.replace(R.id.fragment_container, expenseFragment);
                    fragmentTransaction.addToBackStack(null);

                    fragmentTransaction.commit();

                    ((Homepage) v.getContext()).setToolbar("EDIT EXPENSE");
                }
                swipeLayout.close();
            }
        });

        swipeLayout.close();

    }

    private int getResourceId(int artID)
    {
        switch (artID)
        {
            case 0:
                return R.drawable.batman;
            case 1:
                return R.drawable.star;
            case 2:
                return R.drawable.batman;
            case 3:
                return R.drawable.trash;
            case 4:
                return R.drawable.star;
            case 5:
                return R.drawable.batman;
            case 6:
                return R.drawable.trash;
            default:
                return R.drawable.batman;
        }
    }

    private boolean isNewGroup(Cursor cursor, int position) {
        String currentDate = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.TRANSACTION_TIME));

        cursor.moveToPosition(position - 1);
        String previousDate = cursor.getString(cursor.getColumnIndex(SQLiteDBHelper.TRANSACTION_TIME));

        cursor.moveToPosition(position);
        return !currentDate.equals(previousDate);
    }

}


