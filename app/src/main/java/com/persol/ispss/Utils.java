package com.persol.ispss;

import android.content.Context;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import static com.persol.ispss.Constants.DATE_FORMAT;
import static com.persol.ispss.Constants.DATE_NO_TIME_FORMAT;
import static com.persol.ispss.Constants.ISPSS;
import static com.persol.ispss.Constants.Months;

public class Utils {

    private Context context;

    public Utils(Context context) {
        this.context = context;
    }

    public static String getISODate(Date date){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
            return simpleDateFormat.format(date);
        } catch (Exception e){
            Log.d(ISPSS, "getISODate: "+e.toString());
            return null;
        }
    }

    public static String getISODateNoTime(Date date){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_NO_TIME_FORMAT);
            return simpleDateFormat.format(date);
        } catch (Exception e){
            Log.d(ISPSS, "getISODate: "+e.toString());
            return null;
        }
    }
    public static Date getDate(String date){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);
            return simpleDateFormat.parse(date);
        } catch (Exception e){
            Log.d(ISPSS, "getDate: "+e.toString());
            return null;
        }
    }

    public static Date getDateNoTime(String date){
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_NO_TIME_FORMAT);
            return simpleDateFormat.parse(date);
        } catch (Exception e){
            Log.d(ISPSS, "getDate: "+e.toString());
            return null;
        }
    }

    public static boolean isValidEmail(String email){
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    //show snackbar
    public static void showSnack(CoordinatorLayout coordinatorLayout, final String message){
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    //show toast
    public void showToast(final String message){
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static String getHumanDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE dd MMM yyyy");
        return simpleDateFormat.format(date);
    }

    public static String getSlashedDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return simpleDateFormat.format(date);
    }

    public static String formatMoney(double amount){
        if(amount == 0){
            return "0.00";
        }
        return new DecimalFormat("#,###.00").format(amount);
    }

    public static double removeFormatting(String amount){
        try{
            return Double.parseDouble(amount.replace(",",""));
        } catch (Exception e){
            return 0.00;
        }

    }
}
