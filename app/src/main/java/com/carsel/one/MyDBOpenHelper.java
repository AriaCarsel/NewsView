package com.carsel.one;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyDBOpenHelper extends SQLiteOpenHelper {
    public MyDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "my.db", null, 1);
    }
    @Override
    //数据库第一次创建时被调用
    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE NEWS(" +
//                "news_id INTEGER PRIMARY KEY," +   //不能自增，否则会无限制添加
//                "news_title VARCHAR(200)," +       //新闻标题
//                "news_href VARCHAR(200)," +         //新闻链接
//                "news_detail VARCHARF(200))");        //新闻详细内容
//        Log.i("TAG","database is finished!");

        db.execSQL("CREATE TABLE USERS(" +
                "Users_name VARCHAR(50) PRIMARY KEY," +   //不能自增，否则会无限制添
                "Users_password VARCHAR(50))");        //新闻详细内容
        Log.i("TAG","User database is finished!");
    }
    //软件版本号发生改变时调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}