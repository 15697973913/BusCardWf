package com.example.buscardXiAn.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.example.buscardXiAn.MainActivity;
import com.example.buscardXiAn.tools.AppManager;
import com.example.buscardXiAn.tools.MySqlHelper;
import com.example.buscardXiAn.util.LineMsg_Util;
import com.example.buscardXiAn.util.SiteMsg_Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyApplication extends Application {
	private static final String TAG ="MyApplication" ;
	// 线路信息
	public static LineMsg_Util line_util = new LineMsg_Util();
	// 上行站点list
	public static List<SiteMsg_Util> sxlist = new ArrayList<>();
	// 下行站点list
	public static List<SiteMsg_Util> xxlist = new ArrayList<>();
	// activity对象列表,用于activity统一管理
	private List<Activity> activityList;
	// 异常捕获
	protected boolean isNeedCaughtExeption = true;// 是否捕获未知异常
	private MyUncaughtExceptionHandler uncaughtExceptionHandler;
	private String packgeName;
	public static Context context;
	// 服务用语的list
	public static List<String> fwyylist;

	public static SQLiteDatabase db;
	public static MySqlHelper helper;
	/** false 三全 M164
	 *  true  视新 M350
	 */
	public static boolean BOOTTYPE=false;
	public static int baudrate=19200;
	public static String device;
	public static float weight;
	public static  AppManager mAppManager;

	public void onCreate() {
		super.onCreate();
		mAppManager=AppManager.getAppManager();
		activityList = new ArrayList<>();
		packgeName = getPackageName();
		context = this;
		if (android.os.Build.MODEL.equals("SoftwinerEvb")){
			BOOTTYPE=true;
			device="/dev/ttyS5";
			weight=0;
		}else{
			BOOTTYPE=false;
			device="/dev/ttyS1";
			weight=0.9f;
		}
		qclisr();
		helper = new MySqlHelper(this, "linemessage.db", null, 1);
		db = helper.getWritableDatabase();
		if (isNeedCaughtExeption) {
			cauchException();
		}
	}

	public static void qclisr() {
		fwyylist = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			fwyylist.add("");
		}
	}

	public void chongqi() {
		// 关闭当前应用
		Toast.makeText(context, "启动服务", Toast.LENGTH_SHORT).show();
		// 关闭当前应用
		mAppManager.finishAllActivity();
		mAppManager.finishProgram();
		Intent newIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
		startActivity(newIntent);
	}


	// -------------------异常捕获-----捕获异常后重启系统-----------------//
	private void cauchException() {
		Intent intent = new Intent();
		// 参数1：包名，参数2：程序入口的activity
		intent.setClassName(packgeName, packgeName + ".LoginActivity");
		// 程序崩溃时触发线程
		uncaughtExceptionHandler = new MyUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
	}

	// 创建服务用于捕获崩溃异常
	private class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			// 保存错误日志
			saveCatchInfo2File(ex);
			Intent newIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
			startActivity(newIntent);
			// 关闭当前应用
			mAppManager.finishAllActivity();
			mAppManager.finishProgram();
		}
	}

	/**
	 * 保存错误信息到文件中
	 *
	 * @return 返回文件名称
	 */
	private String saveCatchInfo2File(Throwable ex) {
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String sb = writer.toString();
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			String time = formatter.format(new Date());
			String fileName = time + ".txt";
			System.out.println("fileName:" + fileName);
			String filePath = MainActivity.LogFilePath;
			Log.v(TAG,"filePath:"+filePath);
			File dir = new File(filePath);
			if (!dir.exists()) {
				if (!dir.mkdirs()) {
					Log.v(TAG,"目录创建失败:"+filePath);
					// 创建目录失败: 一般是因为SD卡被拔出了
					return "";
				}
			}
			System.out.println("filePath + fileName:" + filePath + fileName);
			FileOutputStream fos = new FileOutputStream(filePath + fileName);
			fos.write(sb.getBytes());
			fos.close();
			// 文件保存完了之后,在应用下次启动的时候去检查错误日志,发现新的错误日志,就发送给开发者
			return fileName;
		} catch (Exception e) {
			System.out.println("an error occured while writing file..." + e.getMessage());
		}
		return null;
	}

}