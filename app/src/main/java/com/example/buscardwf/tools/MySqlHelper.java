package com.example.buscardwf.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MySqlHelper extends SQLiteOpenHelper {
    /**
     * 书籍类型表: booktype id typename：类型
     */
    /**
     * 线路号表
     */
    private String sql1 = "create table stationline(id integer primary key autoincrement,LineWord,StationUpLast,StationDownLast)";
    /**
     * 上行线路
     */
    private String sql2 = "create table stationlines(id integer primary key autoincrement,StationName,StationDULNo)";
    /**
     * 下行线路
     */
    private String sql3 = "create table stationlinex(id integer primary key autoincrement,StationName,StationDULNo)";
    /**
     * 服务用语
     */
    private String sql4 = "create table servletmsg(id,context)";

    public MySqlHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        // TODO Auto-generated constructor stub
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
        db.execSQL(sql4);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }

}
