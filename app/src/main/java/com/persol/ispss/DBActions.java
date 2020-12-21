package com.persol.ispss;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import static com.persol.ispss.Constants.ISPSS;

public class DBActions {

    private Context context;
    private SQLiteDatabase database;


    public DBActions(Context context) {
        this.context = context;
        database = new DatabaseHelper(this.context).getWritableDatabase();
    }

    public String genericGetSingleItem(String table,String valueColumn, String whereColumn, String whereValue){
        try{
            Cursor cursor = database.query(table,
                    new String[]{valueColumn},
                    whereColumn + " = ?",
                    new String[]{whereValue},
                    null,null,null);
            if(cursor.moveToFirst()){
                String result = cursor.getString(0);
                cursor.close();
                return result;
            }
            return "";
        } catch (Exception e){
            Log.d(ISPSS, "genericGetSingleItem: "+e.toString());
            return null;
        }
    }

    public void clearTables(String[] tables){
        for (String table : tables) {
            clearTable(table);
        }
    }

    public boolean clearTable(String table){
        try{
            database.execSQL("DELETE FROM "+table);
            return true;
        }catch (Exception e){
            Log.d(ISPSS, "clearTable: "+e.toString());
            return false;
        }
    }

    public boolean singleInsert(String table, ContentValues contentValues){
        try{
            database.insert(table,null,contentValues);
            return true;
        } catch (Exception e){
            Log.d(ISPSS, "singleInsert: "+e.toString());
            return false;
        }
    }

    public boolean singleUpdate(String table, String whereColumn, String whereValue, ContentValues contentValues){
        try{
            database.update(table,contentValues,whereColumn + " = ?",new String[]{whereValue});
            return true;
        } catch (Exception e){
            Log.d(ISPSS, "singleUpdate: "+e.toString());
            return false;
        }
    }

    public boolean isTableEmpty(String table, String whereColumn){
        try{
            Cursor cursor = database.query(table,
                    new String[]{whereColumn},
                    null,null,null,null,null);
            if(cursor.moveToFirst()){
                cursor.close();
                return false;
            }
            return true;
        } catch (Exception e){
            Log.d(ISPSS, "isTableEmpty: "+e.toString());
            return true;
        }
    }

    public Cursor getCursor(String table,String[] columns){
        return database.query(table,
                columns,
                null,null,null,null,null);
    }

    public Cursor getSpecificCursor(String table,String[] columns, String whereColumn, String whereValue){
        return database.query(table,
                columns,
                whereColumn+" = ?",
                new String[]{whereValue}
                ,null,null,null);
    }

    public Cursor getGenericCursorSql(String sql){
        return database.rawQuery(sql,null);
    }

    public boolean deleteFromTable(String table, String whereColumn, String whereValue){
        try {
            database.delete(table, whereColumn + " = ?", new String[]{whereValue});
            return true;
        } catch (Exception e){
            Log.d(ISPSS, "genericDelete: "+e.toString());
            return false;
        }
    }

    public void multipleDeleteFromTable(String[] ids, String table,String whereColumn){
        for (String id : ids){
            deleteFromTable(table,whereColumn,id);
        }
    }


}
