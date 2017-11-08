package com.example.buscardXiAn.tools;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.example.buscardXiAn.util.LineMsg_Util;
import com.example.buscardXiAn.util.SiteMsg_Util;

import static com.example.buscardXiAn.application.MyApplication.db;


public class SqliteUtil {
    public static final String TAG="SqliteUtil";

    /**
     * 把线路号存起来
     *
     * @param db
     * @param LineWord
     * @param StationUpLast
     * @param StationDownLast
     */
    public static void insertMsg(SQLiteDatabase db, String LineWord, String StationUpLast, String StationDownLast) {
        db.execSQL("insert into stationline(LineWord,StationUpLast,StationDownLast) values(?,?,?)", new String[]{LineWord, StationUpLast,
                StationDownLast});

    }

    /**
     * 添加上行或者下行数据
     *
     * @param db
     * @param linetype     up：上行 else 下行
     * @param StationName  站点名
     * @param StationDULNo 线路号
     */
    public static void InsertLine(SQLiteDatabase db, String linetype, String StationName, String StationDULNo) {
        String sql;
        if (linetype.equals("up")) {
            sql = "insert into stationlines(StationName,StationDULNo) values(?,?)";
        } else {
            sql = "insert into stationlinex(StationName,StationDULNo) values(?,?)";
        }
        db.execSQL(sql, new String[]{StationName, StationDULNo});
    }

    /**
     * 删除上行或者下行线路
     *
     * @param db
     * @param linetype up：上行 else 下行
     */

    public static void DeleteLine(SQLiteDatabase db, String linetype) {
        String sql;
        if (linetype.equals("up")) {
            sql = "delete from stationlines";
        } else {
            sql = "delete from stationlinex";
        }
        db.execSQL(sql);
    }

    /**
     * 删除线路信息
     *
     * @param db
     */
    public static void DeleteLineNum(SQLiteDatabase db) {
        String sql = "delete from stationline";
        db.execSQL(sql);
    }

    /**
     * @param db
     * @param id      要修改的id
     * @param context 要修改为的内容
     */
    public static void UpdateServletMsg(SQLiteDatabase db, String id, String context) {
        String msg= QueryServletMsg(id);
        if (msg.equals("")){
            InsertServletMsg(db,id,context);
        }else {
            db.execSQL("update servletmsg set context=? where id=?",new String[]{context,id});
        }
    }

    /**
     * 插入服务用语
     * @param db
     * @param id
     * @param context  内容
     */
    public static void InsertServletMsg(SQLiteDatabase db, String id, String context) {
        db.execSQL("insert into servletmsg(id,context) values(?,?)", new String[]{id,
                context});
    }

    /**
     *  查询服务用语
     * @param id
     * @return
     */
    public static String QueryServletMsg(String id) {
        Cursor cursor = db.rawQuery("select * from servletmsg where id=?", new String[]{id});
        String msg = "";
        while (cursor.moveToNext()) {
            msg=cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1)));
        }
        return msg;
    }

    /**
     *  查询所有服务用语
     * @param db
     * @return
     */
    public static List<String> QueryAllServletMsg(SQLiteDatabase db) {
        Log.v("SqliteUtil","QueryAllServletMsg");
        Cursor cursor = db.rawQuery("select * from servletmsg", null);
        List<String> list = new ArrayList<String>();
        while (cursor.moveToNext()) {
            list.add(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));
        }
        return list;
    }
    /**
     * 查询上行或者下行数据
     *
     * @param linetype up：上行 else 下行
     * @param db
     * @return
     */
    public static List<SiteMsg_Util> queryallLine(SQLiteDatabase db, String linetype) {
        List<SiteMsg_Util> list = new ArrayList<SiteMsg_Util>();
        Cursor cursor;
        if (linetype.equals("up")) {
            cursor = db.rawQuery("select * from stationlines", null);
        } else {
            cursor = db.rawQuery("select * from stationlinex", null);
        }
        SiteMsg_Util msg_Util;
        while (cursor.moveToNext()) {
            msg_Util = new SiteMsg_Util();
            msg_Util.setStationName(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));
            msg_Util.setStationDULNo(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));
//            msg_Util.setLongitude(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3))));
//            msg_Util.setLatitude(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(4))));
//            msg_Util.setLongitudeout(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(5))));
//            msg_Util.setLatitudeout(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(6))));
//            msg_Util.setMicroDistance(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(7))));
            list.add(msg_Util);
        }
        return list;
    }

    /**
     *  查询线路
     * @param db
     * @return
     */
    public static LineMsg_Util queryLine(SQLiteDatabase db) {
        LineMsg_Util util = new LineMsg_Util();
        Cursor cursor = db.rawQuery("select * from stationline", null);
        while (cursor.moveToNext()) {
            util = new LineMsg_Util();
            util.setLineWord(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));
            util.setStationUpLast(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));
            util.setStationDownLast(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3))));
        }
        return util;
    }

}
