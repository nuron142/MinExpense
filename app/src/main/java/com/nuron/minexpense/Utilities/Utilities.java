package com.nuron.minexpense.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.nuron.minexpense.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by sunil on 10-Jun-15.
 */
public class Utilities {

    public static final int FROM_DATE_PICKER_TO_EDIT_TEXT = 1;
    public static final int FROM_DB_TO_EDIT_TEXT = 2;
    public static final int FROM_EDIT_TEXT_TO_DB = 3;
    public static final int FROM_DB_TO_LIST_VIEW = 4;

    public static final int TYPE_INCOME = 0;
    public static final int TYPE_EXPENSE = 1;
    private Context context;

    public Utilities(Context contextActivity){
        context=contextActivity;
    }

    public String getDateFormat(String date,int option)
    {
        String formatedDate = "";

        SimpleDateFormat datePicker = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat database = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat editText = new SimpleDateFormat("EEEE, dd MMM yyyy");
        SimpleDateFormat listText = new SimpleDateFormat("EEEE, MMM dd");

        if (date.equals("")) {
            Date today = new Date();
            date = editText.format(today);
        }
        try {
            switch (option)
            {
                case FROM_DATE_PICKER_TO_EDIT_TEXT: // from date Picker
                    formatedDate = editText.format(datePicker.parse(date));
                    break;
                case FROM_DB_TO_EDIT_TEXT: // from dataBase
                    formatedDate = editText.format(database.parse(date));
                    break;
                case FROM_EDIT_TEXT_TO_DB: // from EditText to Database
                    formatedDate = database.format(editText.parse(date));
                    break;
                case FROM_DB_TO_LIST_VIEW: // from dataBase
                    formatedDate = listText.format(database.parse(date));
                    break;
                default:
                    break;
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return formatedDate;
    }

    public double getDayWeight(int day)
    {
        double dayWeight;

        List<Double> dayWeightList1 = new ArrayList<>();
        dayWeightList1.add(0.75); // Monday
        dayWeightList1.add(0.65);
        dayWeightList1.add(0.85);
        dayWeightList1.add(0.75);
        dayWeightList1.add(1.25);
        dayWeightList1.add(1.50); // Saturday
        dayWeightList1.add(1.25); // Sunday

        List<Double> dayWeightList2 = new ArrayList<>();
        dayWeightList2.add(0.75);
        dayWeightList2.add(0.70);
        dayWeightList2.add(0.85);
        dayWeightList2.add(0.70);
        dayWeightList2.add(1.0);
        dayWeightList2.add(1.50);
        dayWeightList2.add(1.50);

        List<Double> dayWeightList3 = new ArrayList<>();
        dayWeightList3.add(0.85);
        dayWeightList3.add(0.65);
        dayWeightList3.add(0.80);
        dayWeightList3.add(0.70);
        dayWeightList3.add(1.35);
        dayWeightList3.add(1.50);
        dayWeightList3.add(1.15);

        List<Double> dayWeightList4 = new ArrayList<>();
        dayWeightList4.add(0.70);
        dayWeightList4.add(0.85);
        dayWeightList4.add(0.70);
        dayWeightList4.add(0.75);
        dayWeightList4.add(1.15);
        dayWeightList4.add(1.50);
        dayWeightList4.add(1.35);

        Calendar now = Calendar.getInstance();
        int weekNumber = now.get(Calendar.WEEK_OF_YEAR);

        switch (weekNumber % 4)
        {
            case 0:
                dayWeight = dayWeightList1.get(day);
                break;
            case 1:
                dayWeight = dayWeightList2.get(day);
                break;
            case 2:
                dayWeight = dayWeightList3.get(day);
                break;
            case 3:
                dayWeight = dayWeightList4.get(day);
                break;
            default:
                dayWeight=0;
                break;
        }

        return dayWeight;
    }

    public double getTodayExpenseMax(Context context)
    {
        double todayWeight=0,todayExpenseMax=0;
        Calendar now = Calendar.getInstance();
        int today = now.get(Calendar.DAY_OF_WEEK);

        switch (today)
        {
            case Calendar.MONDAY:
                todayWeight = getDayWeight(0);
                break;
            case Calendar.TUESDAY:
                todayWeight = getDayWeight(1);
                break;
            case Calendar.WEDNESDAY:
                todayWeight = getDayWeight(2);
                break;
            case Calendar.THURSDAY:
                todayWeight = getDayWeight(3);
                break;
            case Calendar.FRIDAY:
                todayWeight = getDayWeight(4);
                break;
            case Calendar.SATURDAY:
                todayWeight = getDayWeight(5);
                break;
            case Calendar.SUNDAY:
                todayWeight = getDayWeight(6);
                break;
            default:
                todayWeight = 0;
                break;
        }

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.Saved_Values_File), Context.MODE_PRIVATE);
        String monthlyBudget = sharedPref.getString(context.getString(R.string.Budget_value), "0");
        if (monthlyBudget.equals("0"))
            return  0;

        int daysInMonth = now.getActualMaximum(Calendar.DAY_OF_MONTH);
        return todayWeight;
    }
}