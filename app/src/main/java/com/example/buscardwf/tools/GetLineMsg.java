package com.example.buscardwf.tools;

import android.os.Environment;
import android.util.Log;

import com.example.buscardwf.MainActivity;
import com.example.buscardwf.application.MyApplication;
import com.example.buscardwf.util.LineMsg_Util;
import com.example.buscardwf.util.SiteMsg_Util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author 杰 获取线路信息和站点列表
 */
public class GetLineMsg {
	// 线路信息配置文件
	public static String filepath = MainActivity.ConfigureFilePath
			+ "stationlines.ini";
	public static String filepath1 = MainActivity.ConfigureFilePath
			+ "stationlinex.ini";
	public static String filepath3 = MainActivity.ConfigureFilePath
			+ "stationline.ini";
	public static String TAG = "GetLineMsg";

	// 线路信息
	public static LineMsg_Util line_util = new LineMsg_Util();
	// 上行站点list
	public static List<SiteMsg_Util> sxlist = new ArrayList<SiteMsg_Util>();
	// 下行站点list
	public static List<SiteMsg_Util> xxlist = new ArrayList<SiteMsg_Util>();

	// 获取线路信息
	public static void getline() {
		MyApplication.line_util=new LineMsg_Util();
		MyApplication.line_util = SqliteUtil.queryLine(MyApplication.db);

	}

	// 获取站点信息
	public static void getsxsite() {
		try {
			MyApplication.sxlist = new ArrayList<>();
			MyApplication.sxlist=SqliteUtil.queryallLine(MyApplication.db,"up");
			Log.v(TAG,"sxlistSize:"+MyApplication.sxlist.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void getxxsite(){
		try {
			MyApplication.xxlist = new ArrayList<>();
			MyApplication.xxlist=SqliteUtil.queryallLine(MyApplication.db,"down");
			Log.v(TAG,"xxlistSize:"+MyApplication.xxlist.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//读取本地线路信息文件并导入到数据库
	public static void readLineMsgToSqlite(){
		try {
			line_util.setStationId(ConfigurationFile
					.getProfileString(filepath3, "StationId"));
			line_util.setLineWord(ConfigurationFile
					.getProfileString(filepath3, "LineWord"));
			line_util.setStationUpLast(ConfigurationFile
					.getProfileString(filepath3, "StationUpLast"
					));
			line_util.setStationDownLast(ConfigurationFile
					.getProfileString(filepath3, "StationDownLast"
					));
			line_util.setStationUpStartTime(ConfigurationFile
					.getProfileString(filepath3,
                            "StationUpStartTime"));
			line_util.setTicket(ConfigurationFile
					.getProfileString(filepath3, "Ticket"));
			line_util.setStationUpEndTime(ConfigurationFile
					.getProfileString(filepath3, "StationUpEndTime"
					));
			line_util.setStationDownStartTime(ConfigurationFile
					.getProfileString(filepath3,
                            "StationDownStartTime"));
			line_util.setStationDownEndTime(ConfigurationFile
					.getProfileString(filepath3,
                            "StationDownEndTime"));
			Log.v(TAG,"line_util:"+line_util.toString());
			SqliteUtil.DeleteLineNum(MyApplication.db);
			SqliteUtil.insertMsg(MyApplication.db,line_util.getLineWord(),line_util.getStationUpLast(),line_util.getStationDownLast());
		} catch (Exception e) {
			Log.v(TAG, "读取配置文件失败");
			e.printStackTrace();
		}

	}
	//读取本地上行文件并导入到数据库
	public static void readsxLineMsgToSqlite(){
		try {
			SqliteUtil.DeleteLine(MyApplication.db,"up");
			sxlist = ConfigurationFile.getSectionAll(filepath);
			for (int i=0;i<sxlist.size();i++){
				SqliteUtil.InsertLine(MyApplication.db,"up",sxlist.get(i).getStationName(),sxlist.get(i).getStationDULNo());
			};
		} catch (Exception e) {
			Log.v(TAG, "读取配置文件失败");
			e.printStackTrace();
		}
	}
	//读取本地下行文件并导入到数据库
	public static void readxxLineMsgToSqlite(){
		try {
			SqliteUtil.DeleteLine(MyApplication.db,"down");
			xxlist = ConfigurationFile.getSectionAll(filepath1);
			for (int i=0;i<xxlist.size();i++){
				SqliteUtil.InsertLine(MyApplication.db,"down",xxlist.get(i).getStationName(),xxlist.get(i).getStationDULNo());
			}
		} catch (Exception e) {
			Log.v(TAG, "读取配置文件失败");
			e.printStackTrace();
		}
	}

	/**
	 * 把服务用语导入到数据库
	 */
	public static void readfwyyMsgToSqlite(){
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			try {
				// 获取文件输入流
				FileInputStream fis = new FileInputStream(MainActivity.fwyyfile);
				BufferedReader buff = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
				String lien = null;
				int i = 0;
				while ((lien = buff.readLine()) != null) {
					SqliteUtil.UpdateServletMsg(MyApplication.db,i+"",lien);
					i++;
				}
				buff.close();
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {

		}
	}
}