package com.example.buscardXiAn.tools;

/*
 * ConfigurationFile.java
 *
 * Created on 2009年4月15日, 下午1:36
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.buscardXiAn.util.SiteMsg_Util;


/**
 * 这是个配置文档操作类，用来读取和配置ini配置文档
 *
 * @author 由月
 * @version 2004-08-18
 * @修改 2008-05-22
 */
public final class ConfigurationFile {
	/**
	 * 从ini配置文档中读取变量的值
	 *
	 * @param file
	 *            配置文档的路径
	 * @param section
	 *            要获取的变量所在段名称
	 * @param variable
	 *            要获取的变量名称
	 * @param defaultValue
	 *            变量名称不存在时的默认值
	 * @return 变量的值
	 * @throws IOException
	 *             抛出文档操作可能出现的io异常
	 */
	private static String TAG = "ConfigurationFile";

	public static String getProfileString(String file, String section,
										  String variable, String defaultValue) throws IOException {
		String strLine, value = "";
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file)));
		boolean isInSection = false;
		try {
			while ((strLine = bufferedReader.readLine()) != null) {
				strLine = strLine.trim();
				Pattern p;
				Matcher m;
				p = Pattern.compile("\\[" + section + "\\]");
				m = p.matcher((strLine));
				if (m.matches()) {

					p = Pattern.compile("\\[" + section + "\\]");
					m = p.matcher(strLine);
					if (m.matches()) {
						isInSection = true;
					} else {
						isInSection = false;
					}
				}
				if (isInSection == true) {
					strLine = strLine.trim();
					String[] strArray = strLine.split("=");
					isInSection = true;
					if (strArray.length == 1) {
						value = strArray[0].trim();
						if (value.equalsIgnoreCase(variable)) {
							value = null;
							return value;
						}
					} else if (strArray.length == 2) {
						value = strArray[0].trim();
						if (value.equalsIgnoreCase(variable)) {
							value = strArray[1].trim();
							return value;
						}
					} else if (strArray.length > 2) {
						value = strArray[0].trim();
						if (value.equalsIgnoreCase(variable)) {
							value = strLine.substring(strLine.indexOf("=") + 1)
									.trim();
							return value;
						}
					}
				}
			}
		} finally {
			bufferedReader.close();
		}
		return defaultValue;
	}

	/**
	 *
	 * @param file
	 * @param section
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<SiteMsg_Util> getSectionAll(String file) throws IOException {
		String strLine, value = "";
		SiteMsg_Util util = new SiteMsg_Util();
		int i = 1;
		String sectionname = "605" + "_" + i;
		ArrayList<SiteMsg_Util> list = new ArrayList<SiteMsg_Util>();
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file)));
		boolean isInSection = false;
		try {
			while ((strLine = bufferedReader.readLine()) != null) {
				strLine = strLine.trim();
				Pattern p;
				Matcher m;
				p = Pattern.compile("\\[" + sectionname + "\\]");
				m = p.matcher((strLine));
				if (m.matches()) {
					p = Pattern.compile("\\[" + sectionname + "\\]");
					m = p.matcher(strLine);
					if (m.matches()) {
						isInSection = true;
					} else {
						isInSection = false;
					}
				}
				if (isInSection == true) {
					strLine = strLine.trim();
					String[] strArray = strLine.split("=");
					if (strArray.length == 2) {
						value = strArray[0].trim();
						String value2 = strArray[1].trim();
						if (value.equalsIgnoreCase("StationName")) {
							util.setStationName(value2);
						} else if (value.equalsIgnoreCase("StationDULNo")) {
							util.setStationDULNo(value2);
						} else if (value.equalsIgnoreCase("StationSNGNo")) {
							util.setStationSNGNo(value2);
						} else if (value.equalsIgnoreCase("Longitude")) {
							util.setLongitude(value2);
						} else if (value.equalsIgnoreCase("Latitude")) {
							util.setLatitude(value2);
						} else if (value.equalsIgnoreCase("Longitudeout")) {
							util.setLongitudeout(value2);
						} else if (value.equalsIgnoreCase("Latitudeout")) {
							util.setLatitudeout(value2);
						} else if (value.equalsIgnoreCase("MicroDistance")) {
							util.setMicroDistance(value2);
							list.add(util);
							i++;
							util = new SiteMsg_Util();
						}
					}

				}
			}
		} catch (Exception e) {
			Log.e(TAG, "有异常");
		} finally {
			bufferedReader.close();
		}
		return list;
	}
}