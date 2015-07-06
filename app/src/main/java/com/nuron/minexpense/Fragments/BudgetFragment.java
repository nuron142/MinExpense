package com.nuron.minexpense.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nuron.minexpense.ContentProvider.TransactionProvider;
import com.nuron.minexpense.R;
import com.nuron.minexpense.Utilities.Utilities;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Nuron on 05-Jul-15.
 */
public class BudgetFragment extends Fragment {
    public static final String TAG = "BudgetFragment";
    Utilities utilities;
    saveBudgetListener mListener;
    View rootView;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (saveBudgetListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement saveExpenseListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.budget_fragment, container, false);

        utilities = new Utilities(getActivity());

        setBudgetTextView();

        Button save = (Button) rootView.findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNewBudgetToPref();
                mListener.saveNewBudget();
            }
        });

        return rootView;
    }

    public void saveNewBudgetToPref() {
        EditText set_budget = (EditText) rootView.findViewById(R.id.set_budget_text);
        DecimalFormat formatter = new DecimalFormat("#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
        formatter.setRoundingMode(RoundingMode.HALF_UP);

        double newBudget = Double.parseDouble(set_budget.getText().toString());
        utilities.writeToSharedPref(R.string.Budget_value, formatter.format(newBudget));
        getActivity().getContentResolver().notifyChange(TransactionProvider.CONTENT_URI, null);

    }

    public void setBudgetTextView() {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        Date today = new Date();

        TextView month_text = (TextView) rootView.findViewById(R.id.month_text);
        month_text.setText(monthFormat.format(today));

        TextView year_text = (TextView) rootView.findViewById(R.id.year_text);
        year_text.setText(yearFormat.format(today));

        String currentBudget = utilities.readFromSharedPref(R.string.Budget_value);
        TextView current_budget_text = (TextView) rootView.findViewById(R.id.current_budget_drawer);
        current_budget_text.setText("Rs " + currentBudget);
    }

    public interface saveBudgetListener {
        void saveNewBudget();
    }
}

