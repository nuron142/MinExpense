package com.nuron.minexpense.Utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;

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

    public Utilities(){
    }

    public String getDateFormat(String date,int option)
    {
        String formatedDate = "";

        SimpleDateFormat datePicker = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat database = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat editText = new SimpleDateFormat("EEEE, dd MMM yyyy");
        SimpleDateFormat listText = new SimpleDateFormat("EEEE, MMM dd");

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

}