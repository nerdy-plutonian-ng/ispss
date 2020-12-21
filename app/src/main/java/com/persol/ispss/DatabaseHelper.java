package com.persol.ispss;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static com.persol.ispss.Constants.ACCOUNT_NAME;
import static com.persol.ispss.Constants.ACCOUNT_NUMBER;
import static com.persol.ispss.Constants.ACCOUNT_TYPE;
import static com.persol.ispss.Constants.APPR;
import static com.persol.ispss.Constants.BANK_BRANCH;
import static com.persol.ispss.Constants.BANK_CODE;
import static com.persol.ispss.Constants.BANK_NAME;
import static com.persol.ispss.Constants.BANK_TABLE;
import static com.persol.ispss.Constants.BENEFICIARIES_TABLE;
import static com.persol.ispss.Constants.DATE;
import static com.persol.ispss.Constants.DB_NAME;
import static com.persol.ispss.Constants.DB_VERSION;
import static com.persol.ispss.Constants.EMAIL;
import static com.persol.ispss.Constants.FIRST_NAME;
import static com.persol.ispss.Constants.GENDER;
import static com.persol.ispss.Constants.HOMETOWN;
import static com.persol.ispss.Constants.LAST_NAME;
import static com.persol.ispss.Constants.MIDDLE_NAME;
import static com.persol.ispss.Constants.MOMO_CODE;
import static com.persol.ispss.Constants.MOMO_TABLE;
import static com.persol.ispss.Constants.NAME;
import static com.persol.ispss.Constants.OCCUPATION;
import static com.persol.ispss.Constants.PERCENTAGE;
import static com.persol.ispss.Constants.PERSONAL_TABLE;
import static com.persol.ispss.Constants.PHONE;
import static com.persol.ispss.Constants.PKID;
import static com.persol.ispss.Constants.PROVIDER;
import static com.persol.ispss.Constants.RELATIONSHIP;
import static com.persol.ispss.Constants.RESIDENCE;
import static com.persol.ispss.Constants.SCHEMES_TABLE;
import static com.persol.ispss.Constants.USER_CODE;

public class DatabaseHelper  extends SQLiteOpenHelper {

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + PERSONAL_TABLE + "(_id TEXT PRIMARY KEY," +
                USER_CODE + " INTEGER," +
                FIRST_NAME + " TEXT," +
                MIDDLE_NAME + " TEXT," +
                LAST_NAME + " TEXT," +
                GENDER + " TEXT," +
                DATE + " TEXT," +
                PHONE + " TEXT," +
                EMAIL + " TEXT," +
                RESIDENCE + " TEXT," +
                OCCUPATION + " TEXT," +
                HOMETOWN + " TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE " + BENEFICIARIES_TABLE + "(_id TEXT PRIMARY KEY," +
                PKID + " INTEGER," +
                FIRST_NAME + " INTEGER," +
                LAST_NAME + " TEXT," +
                GENDER + " TEXT," +
                DATE + " TEXT," +
                PHONE + " TEXT," +
                RELATIONSHIP + " TEXT," +
                PERCENTAGE + " REAL)");

        sqLiteDatabase.execSQL("CREATE TABLE " + BANK_TABLE + "(_id TEXT PRIMARY KEY," +
                BANK_CODE + " INTEGER," +
                ACCOUNT_NAME + " TEXT," +
                ACCOUNT_NUMBER + " TEXT," +
                ACCOUNT_TYPE + " TEXT," +
                BANK_NAME + " TEXT," +
                BANK_BRANCH + " TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE " + MOMO_TABLE + "(_id TEXT PRIMARY KEY," +
                MOMO_CODE + " INTEGER," +
                ACCOUNT_NAME + " TEXT," +
                ACCOUNT_NUMBER + " TEXT," +
                PROVIDER + " TEXT)");

        sqLiteDatabase.execSQL("CREATE TABLE " + SCHEMES_TABLE + "(_id TEXT PRIMARY KEY," +
                PKID + " INTEGER," +
                NAME + " TEXT," +
                APPR + " REAL)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}