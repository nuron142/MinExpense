package com.nuron.minexpense.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nuron.minexpense.R;

import io.karim.MaterialTabs;

/**
 * Created by Nuron on 11-Jul-15.
 */
public class TransactionParentFragment extends Fragment {

    public static final String TAG = "TransactionsFragment";
    View rootView;
    FragmentPagerAdapter adapterViewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.transactions, container, false);

        ViewPager vpPager = (ViewPager) rootView.findViewById(R.id.viewPager);
        adapterViewPager = new MyPagerAdapter(getChildFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        vpPager.setCurrentItem(1, true);


        // Bind the tabs to the ViewPager
        MaterialTabs tabs = (MaterialTabs) rootView.findViewById(R.id.tabs);
        tabs.setViewPager(vpPager);

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
    }


    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return TransactionFragment.newInstance("2015-06-01 00:00:00", "2015-06-30 23:59:59");
                case 1:
                    return TransactionFragment.newInstance("2015-07-01 00:00:00", "2015-07-31 23:59:59");
                case 2:
                    return TransactionFragment.newInstance("2015-08-01 00:00:00", "2015-08-30 23:59:59");
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }
}

