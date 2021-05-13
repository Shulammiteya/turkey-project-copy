package com.example.turkeyproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private Context nowContext;
    private final String uID = "ID";
    private final String account = "Account";
    private final String doctorName = "DoctorName";

    private static final String databaseName = "LocalDB";
    private static final int databaseVersion = 1;
    private final String tableName = "TurkeyAccountBook";

    private final String createTableSQL ="CREATE TABLE IF NOT EXISTS "+this.tableName+"( "+this.uID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+this.account+" VARCHAR(255), "+this.doctorName+" VARCHAR(255) ) ;" ;
    private final String deleteTableSQL="DROP TABLE IF EXISTS "+this.tableName+" ;";

    public  DBHelper(Context context) {
        super(context, databaseName,null, databaseVersion);
        this.nowContext = context;
        Log.d("test","test");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL(this.createTableSQL);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        try {
            sqLiteDatabase.execSQL(this.deleteTableSQL);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertToLocalDB(String acc, String dn) {
        SQLiteDatabase myLocalDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(this.account, acc);
        contentValues.put(this.doctorName, dn);
        long nowID = myLocalDB.insert(this.tableName, null, contentValues);

        final String successlog = "新增成功!\n\n編號 : " + nowID + "\n帳戶 : " + acc + "\n醫生名字 : " + dn;
        //Toast.makeText(this.nowContext, successlog, Toast.LENGTH_LONG).show();
    }

    public void updateToLocalDB(int id, String newAcc, String newPw) {
//        SQLiteDatabase myLocalDB=this.getWritableDatabase();
//        ContentValues contentValues=new ContentValues();
//        contentValues.put(this.account, newAcc);
//        contentValues.put(this.password, newPw);
//
//        String[] argu={ String.valueOf(id) };
//
//        int affectRow=myLocalDB.update(this.tableName,contentValues,this.uID+" = ? ",argu);
//        if(affectRow==0){
//            Toast.makeText(this.nowContext, "更新失敗，請重新再試", Toast.LENGTH_LONG).show();
//        }
//        else{
//            final String successlog = "更新成功!\n\n編號 : " + id + "\n帳戶 : " + newAcc + "\n密碼 : " + newPw;
//            Toast.makeText(this.nowContext, successlog, Toast.LENGTH_LONG).show();
//        }
    }

    public void deleteFromLocalDB(int id) {
        SQLiteDatabase myLocalDB = this.getWritableDatabase();
        String[] argu = { String.valueOf(id) };
        int affectRow = myLocalDB.delete(this.tableName,this.uID+" = ? ", argu);
        if(affectRow == 0) {
            Toast.makeText(this.nowContext, "刪除失敗，請重新再試", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this.nowContext, "刪除成功", Toast.LENGTH_LONG).show();
        }
    }

    public String search(String se) {
        AccountInfo searchAccountInfo = new AccountInfo();
        SQLiteDatabase myLocalDB = this.getReadableDatabase();
//        String query="SELECT uID, userName, phoneNum FROM TelephoneBook WHERE userName = ? AND phoneNum = ?";
//        String[] selectionArgs = {"Amit","7"};
        int id = -1;
        String acc = "pupu";
        String dn = "";
//        String se = "yuki";
        Cursor testCursor = myLocalDB.query(this.tableName, null, "Account='"+se+"'", null, null, null, null, null);
//        Cursor testCursor=myLocalDB.rawQuery(query, selectionArgs);
        if (testCursor.getCount() > 0) {
            testCursor.moveToFirst();
            id = testCursor.getInt(testCursor.getColumnIndex(this.uID));
            acc = testCursor.getString(testCursor.getColumnIndex(this.account));
            dn = testCursor.getString(testCursor.getColumnIndex(this.doctorName));
        }
//        searchAccountInfo.init(id,acc,dn);
//        Toast.makeText(this.nowContext, acc, Toast.LENGTH_LONG).show();
//        return searchContactInfo; // Copy cursor props to custom obj
        return dn;
    }

    public ArrayList<AccountInfo> getTotalAccountInfo() {
        ArrayList<AccountInfo> totalContactInfo=new ArrayList<AccountInfo>();

        SQLiteDatabase myLocalDB = this.getReadableDatabase();
        String[] myColumn = {this.uID, this.account, this.doctorName};
        Cursor myCursor = myLocalDB.query(this.tableName, myColumn,null,null,null,null, uID);
        while (myCursor.moveToNext()) {
            int id = myCursor.getInt(myCursor.getColumnIndex(this.uID));
            String acc = myCursor.getString(myCursor.getColumnIndex(this.account));
            String dn = myCursor.getString(myCursor.getColumnIndex(this.doctorName));
            AccountInfo eachContactInfo = new AccountInfo();
            eachContactInfo.init(id,acc,dn);
            totalContactInfo.add(eachContactInfo);
        }
        return totalContactInfo;
    }

}
