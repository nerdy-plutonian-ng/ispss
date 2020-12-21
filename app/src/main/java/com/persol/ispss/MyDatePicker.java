package com.persol.ispss;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;

import static com.persol.ispss.Constants.Months;
import static com.persol.ispss.Constants.USER;

public class MyDatePicker {

    private Context context;
    private TextInputEditText DOBEt;
    private DatePickerDialog.OnDateSetListener myDateListener;
    private Calendar cal;



    public MyDatePicker(Context context, final TextInputEditText DOBEt){
        cal = Calendar.getInstance();
        this.context = context;
        this.DOBEt =  DOBEt;
        this.myDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year,monthOfYear,dayOfMonth);
                Date date = calendar.getTime();
                User.dobDate = date;
                DOBEt.setText(Utils.getSlashedDate(User.dobDate));
            }
        };
    }

    public void showDialog(){
        DatePickerDialog pickerDialog = new DatePickerDialog(context,myDateListener,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));
        pickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis() - (86400000L*6570L));
        pickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis() - (86400000L*36500L));
        pickerDialog.show();
    }
}
