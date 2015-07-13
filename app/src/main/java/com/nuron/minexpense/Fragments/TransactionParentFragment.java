package com.nuron.minexpense.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nuron.minexpense.R;
import com.nuron.minexpense.Utilities.Utilities;

import io.karim.MaterialTabs;

/**
 * Created by Nuron on 11-Jul-15.
 */
public class TransactionParentFragment extends Fragment {

    public static final String TAG = "TransactionsParentFragment";
    View rootView;
    FragmentStatePagerAdapter adapterViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.transactions, container, false);

        ViewPager vpPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        adapterViewPager = new MyPagerAdapter(getChildFragmentManager(), getActivity());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setCurrentItem(12, true);

        MaterialTabs tabs = (MaterialTabs) rootView.findViewById(R.id.tabs);
        tabs.setViewPager(vpPager);

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        //adapterViewPager.notifyDataSetChanged();
    }


    public static class MyPagerAdapter extends FragmentStatePagerAdapter implements MaterialTabs.CustomTabProvider {
        private final static Integer[] monthPosition = new Integer[]{-12, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        private Context context;
        private Utilities utilities;

        public MyPagerAdapter(FragmentManager fragmentManager, Context c) {
            super(fragmentManager);
            context = c;
            utilities = new Utilities(context);
        }

        @Override
        public int getCount() {
            return monthPosition.length;
        }

        @Override
        public Fragment getItem(int position) {
            String[] firstLastDate = utilities.getFirstAndLastDateFragment(monthPosition[position]);
            return TransactionFragment.newInstance(firstLastDate[0], firstLastDate[1]);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "" + position;
        }

        @Override
        public View getCustomTabView(ViewGroup parent, int position) {
            View rootView = LayoutInflater.from(context).inflate(R.layout.tab_item, parent, false);

            String[] monthAndYear = utilities.getMonthAndYear(monthPosition[position]);

            TextView month_text = (TextView) rootView.findViewById(R.id.month_text);
            month_text.setText(monthAndYear[0]);

            TextView year_text = (TextView) rootView.findViewById(R.id.year_text);
            year_text.setText(monthAndYear[1]);

            return rootView;
        }

//        @Override
//        public int getItemPosition(Object object) {
//            return POSITION_NONE;
//        }

    }
}

