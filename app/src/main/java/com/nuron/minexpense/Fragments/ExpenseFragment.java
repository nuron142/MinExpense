package com.nuron.minexpense.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nuron.minexpense.R;

/**
 * Created by sunil on 24-Jun-15.
 */
public class ExpenseFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.homepage_fragment, container, false);
    }


}
