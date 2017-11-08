package com.example.buscardXiAn.server;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.buscardXiAn.MainActivity;
import com.example.buscardXiAn.application.MyApplication;
import com.example.buscardXiAn.tools.CopyFile;
import com.example.buscardXiAn.tools.MyFunc;
import com.example.buscardXiAn.tools.SqliteUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import android_serialport_api.SerialPort;


public class SerialPortService extends Service {

	protected SerialPort mSerialPort;
	protected OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	private String TAG = "SerialPortService";
	public String strResponse = "";
	/**
	 * 判断是否保存
	 */
	public boolean isbaocun = false;
	/**
	 * 线路名称，保存线路的文件夹名称
	 */
	public String xlname;

	private class ReadThread extends Thread {

		public void run() {
			super.run();
			while (!isInterrupted()) {
				int size;
				try {
					byte[] buffer = new byte[512];
					if (mInputStream == null)
						return;
					size = mInputStream.read(buffer);
					byte[] bRec = new byte[size];
					for (int i = 0; i < size; i++) {
						bRec[i] = buffer[i];
					}
					// 截取1E与1F中间的字符
					if (size > 0) {
						String getmsg = MyFunc.ByteArrToHex(bRec);
						getmsg(getmsg);
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

	/**
	 * 截取1e-1f之间的数据
	 *
	 * @return 截取的数据
	 */
	public void getmsg(String str) {
		strResponse += str;
		Log.v(TAG, "strResponse:"+strResponse);
		if (strResponse.length() < 25) {
			return;
		} else {
			int btindex = strResponse.indexOf("1E60");
			if (btindex == -1) {
				strResponse = "";
				return;
			}
			strResponse = strResponse.substring(btindex, strResponse.length());
			btindex = strResponse.indexOf("1E60");
			// 截取"1E60"后面的数据
			String strtemp = strResponse.substring(btindex + 4, strResponse.length());
			// 数据帧长度
			if (strtemp.length() < 15) {
				return;
			}
			int sjzcd = MyFunc.HexToInt(strtemp.substring(8, 12));
			if (sjzcd > 1200) {
				strResponse = "";
				return;
			}
			if (strResponse.length() < (sjzcd + 10) * 2) {
				return;
			}

			// 判断最后一位十是否是“1F”
			if (strResponse.length() == (sjzcd + 10) * 2) {
				// 截取最后两位
				String wfzhlw = strResponse.substring(strResponse.length() - 2, strResponse.length());
				if (!wfzhlw.equalsIgnoreCase("1F")) {
					strResponse = "";
					return;
				}
				if (jiaoyan(strResponse)) {
					Message message = new Message();
					message.obj = strResponse.substring(4, strResponse.length());
					message.what = 0x1313;
					handler.sendMessage(message);
				}
				strResponse = "";
				return;
			} else {
				// 如果不是就截取对应长度，保留后面的数据
				String aa = strResponse.substring(0, (sjzcd + 10) * 2);
				String wfzhlw1 = aa.substring(aa.length() - 2, aa.length());
				if (!wfzhlw1.equalsIgnoreCase("1F")) {
					strResponse = strResponse.substring(strResponse.indexOf("1F1E") + 4, strResponse.length());
					return;
				}
				if (strResponse.length() > (sjzcd + 10) * 2) {
					// 把多余的截取出来
					strResponse = strResponse.substring((sjzcd + 10) * 2, strResponse.length());
				}
				if (jiaoyan(aa)) {
					Message message = new Message();
					message.obj = aa.substring(4, aa.length());
					message.what = 0x1313;
					handler.sendMessage(message);
				}
				return;
			}
		}
	}

	/**
	 * 判断校验位是否正确的方法
	 *
	 * @param str
	 *            要校验的数据
	 * @return 校验结果
	 */
	public boolean jiaoyan(String str) {
		Log.v(TAG, "校验前的数据为：" + str);
		int jiaoyan = MyFunc.HexToInt(str.substring(str.length() - 4, str.length() - 2));
		int yhzhi = Integer.parseInt(str.substring(0, 2), 16);
		for (int i = 2; i < str.length() - 4; i += 2) {
			int bb = Integer.parseInt(str.substring(i, i + 2), 16);
			yhzhi = yhzhi ^ bb;
		}
		if ((jiaoyan ^ yhzhi) == 0) {
			return true;
		} else {
			Log.v(TAG, "校验失败");
			return false;
		}
	}

	/**
	 *
	 * @param str
	 * @return 算出校验位
	 */
	public String JiaoYan(String str) {
		int yhzhi = Integer.parseInt(str.substring(0, 2), 16);
		for (int i = 2; i < str.length(); i += 2) {
			int bb = Integer.parseInt(str.substring(i, i + 2), 16);
			yhzhi = yhzhi ^ bb;
		}
		return Integer.toHexString(yhzhi);
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0x1313:
					// try {
					String message = msg.obj.toString();
					String lsh = message.substring(6, 8);
					String ydz = message.substring(2, 4);
					// 目标地址
					String mbdz = message.substring(0, 2);
					if ((mbdz.equals("20") || mbdz.equals("10") || mbdz.equalsIgnoreCase("ff"))) {
						if (message.substring(0, 2).equals("20") || message.substring(0, 2).equals("02")) {
							String sjz = "1E60" + ydz + "2005" + lsh + "00040C000101";
							if (message.equalsIgnoreCase("1E602003010100005D1F")) {
								// 巡检回复
								sjz = "1E60" + ydz + "2005" + lsh + "0008090001010A000101";
							}
							String jyw = JiaoYan(sjz);
							String sendmsg = sjz + jyw + "1F";
							Log.v(TAG, "发送的串口数据为：" + sendmsg);
							sendHex(sendmsg);
						}
						// 将路牌调为下行
						if (message.equalsIgnoreCase("FF020701000402000101831F")) {
							((MainActivity) MainActivity.context).setsxx(2);
							// 将路牌调为上行
						} else if (message.equalsIgnoreCase("FF020701000402000100821F")) {
							((MainActivity) MainActivity.context).setsxx(1);
						} else {
							// 截取流水号
							String xxz = message.substring(4, 6);
							if (xxz.equals("03")) {
								int xxzleng = MyFunc.HexToInt(message.substring(8, 12));
								// 截取消息帧
								String msgz = message.substring(12, 12 + xxzleng * 2);
								Log.v(TAG, "消息帧:" + msgz);
								Log.v(TAG, "中文消息帧" + MyFunc.HexStringTOString(msgz));
								// 到、离站
								int islz = getxxz(getleng(msgz, 4), "04");
								// 截取站点号
								int sxxbz = getxxz(msgz, "02");
								if (sxxbz == 0) {
									((MainActivity) MainActivity.context).setsxx(1);
								} else if (sxxbz == 1) {
									((MainActivity) MainActivity.context).setsxx(2);
								}
								// 站点序号
								int dzxh = getxxz(getleng(msgz, 5), "05");
								Log.v(TAG, "到站序号:" + dzxh);
								// 下行
								if (sxxbz == 1) {
									((MainActivity) MainActivity.context).setdaozhen(MyApplication.xxlist.size() - dzxh + 1, islz, MyApplication.xxlist, 1);
								} else {
									((MainActivity) MainActivity.context).setdaozhen(dzxh, islz, MyApplication.sxlist, 0);
								}
								// 校验前的数据为：1E60 200106 01 0151 0E 014E01
								// 04B9FED5BE000206D6D8BBF5C3C500
								// 校验前的数据为：1E60 200106 01 000F 0F 000C00
								// 0708D0C2BDAEB4F3BDD600761F
								// 1E60 200106 01 000D 0E 000A
								// 000206D6D8BBF5C3C500131F
							} else if (xxz.equals("06")) {
								// 判断是上行还是下行 上行：0e 下行：0f
								String sxx = message.substring(12, 14);
								Log.v(TAG, "上下行标识：" + sxx);
								// 获取长度
								int leng = MyFunc.HexToInt(message.substring(14, 18));
								String cshmsg = message.substring(18, 18 + leng * 2);
								// 判断是否是一个站一个站发的，如果是FF则是
								String isone = message.substring(18, 20);
								if (sxx.equalsIgnoreCase("0e")) {
									if (isone.equalsIgnoreCase("FF")) {
										// 站点序号
										String xnum = message.substring(20, 22);
										if (xnum.equals("01")) {
											SqliteUtil.DeleteLine(MyApplication.db, "up");
											SqliteUtil.DeleteLine(MyApplication.db, "down");
										}
										// 站点名称
										String zdname = getxxz1(cshmsg.substring(2, cshmsg.length()));
										// 站点序号
										int zdxh = MyFunc.HexToInt((cshmsg.substring(0, 2)));
										Log.v(TAG, "站名：" + zdname);
										SqliteUtil.InsertLine(MyApplication.db, "up", zdname, zdxh + "");
									} else {
										SqliteUtil.DeleteLine(MyApplication.db, "up");
										for (int i = 1;; i++) {
											if (MyFunc.HexToInt((cshmsg.substring(0, 2))) != i) {
												Log.v(TAG, "TiaoChuXunHuan");
												break;
											}
											// 站点名称
											String zdname = getxxz1(cshmsg);
											// 站点序号
											int zdxh = MyFunc.HexToInt((cshmsg.substring(0, 2)));
											SqliteUtil.InsertLine(MyApplication.db, "up", zdname, zdxh + "");
											cshmsg = getleng1(cshmsg);
											if (cshmsg.length() < 4) {
												Log.v(TAG, "TiaoChuXunHuan");
												break;
											}
										}
									}
									((MainActivity) MainActivity.context).getLineMsg(2);
								} else if (sxx.equalsIgnoreCase("0f")) {
									if (isone.equalsIgnoreCase("FF")) {
										// 站点名称
										String zdname = getxxz1(cshmsg.substring(2, cshmsg.length()));
										// 站点序号
										int zdxh = MyFunc.HexToInt((cshmsg.substring(0, 2)));
										Log.v(TAG, "站名：" + zdname);
										SqliteUtil.InsertLine(MyApplication.db, "down", zdname, zdxh + "");
									} else {
										int ktxh = MyFunc.HexToInt((cshmsg.substring(0, 2)));
										SqliteUtil.DeleteLine(MyApplication.db, "down");
										for (int i = ktxh;; i--) {
											if (MyFunc.HexToInt((cshmsg.substring(0, 2))) != i) {
												Log.v(TAG, "TiaoChuXunHuan");
												break;
											}
											// 站点名称
											String zdname = getxxz1(cshmsg);
											// 站点序号
											int zdxh = MyFunc.HexToInt((cshmsg.substring(0, 2)));
											Log.v(TAG, "站名：" + zdname);
											SqliteUtil.InsertLine(MyApplication.db, "down", zdname, zdxh + "");
											cshmsg = getleng1(cshmsg);
											if (cshmsg.length() < 4) {
												Log.v(TAG, "TiaoChuXUn");
												break;
											}
										}
									}
									((MainActivity) MainActivity.context).getLineMsg(3);
									// 遥控路牌 1E60FF010601001F 1B001C
									// 1500000100FFFFFFFFFFFFFFFFFFFFFFFF0105383838C2B702000300C31F
									// 滚动路牌 1E60FF0106010020 1B001D
									// 0000000101033535350206CDDABBFAC5AE030AC8F6B5C4B0D7BDF0BFA8D51F
								} else if (sxx.equalsIgnoreCase("1b")) {
									// 数据帧长度
									int sjzlen = MyFunc.HexToInt(message.substring(14, 18));
									// 数据帧
									String sjzmsg = message.substring(18, 18 + sjzlen * 2);
									// 把数据帧前面的4个没有的字节截取掉
									sjzmsg = sjzmsg.substring(8, sjzmsg.length());
									// 如果为遥控路牌
									String sjzmsg1 = "";
									if (sjzmsg.length() > 26) {
										sjzmsg1 = sjzmsg.substring(26, sjzmsg.length());
									}
									String xlh = "", qd = "", zd = "";
									if (sjzmsg1.indexOf("01") != -1 && sjzmsg1.indexOf("02") != -1 && sjzmsg1.indexOf("03") != -1) {
										xlh = getxxz1(sjzmsg1);
										int xhleng = MyFunc.HexToInt(sjzmsg1.substring(2, 4));
										sjzmsg1 = sjzmsg1.substring(4 + xhleng * 2, sjzmsg1.length());
										qd = getxxz1(sjzmsg1);
										xhleng = MyFunc.HexToInt(sjzmsg1.substring(2, 4));
										sjzmsg1 = sjzmsg1.substring(4 + xhleng * 2, sjzmsg1.length());
										zd = getxxz1(sjzmsg1);
									} else {
										xlh = getxxz1(sjzmsg);
										int xhleng = MyFunc.HexToInt(sjzmsg.substring(2, 4));
										sjzmsg = sjzmsg.substring(4 + xhleng * 2, sjzmsg.length());
										qd = getxxz1(sjzmsg);
										xhleng = MyFunc.HexToInt(sjzmsg.substring(2, 4));
										sjzmsg = sjzmsg.substring(4 + xhleng * 2, sjzmsg.length());
										zd = getxxz1(sjzmsg);
									}

									Log.v(TAG, "线路号：" + xlh + "  起点站：" + qd + "  终点站:" + zd);
									SqliteUtil.DeleteLineNum(MyApplication.db);
									SqliteUtil.insertMsg(MyApplication.db, xlh, qd, zd);
									((MainActivity) MainActivity.context).getLineMsg(1);
								} else if (sxx.equalsIgnoreCase("11")) {
									if (cshmsg.length()==0){
										return;
									}
									String fwyy = cshmsg.substring(14, cshmsg.length());
									String strfwyy = MyFunc.HexStringTOString(fwyy);
									Log.v(TAG, "服务用语：" + strfwyy);
									int xx = MyFunc.HexToInt(cshmsg.substring(0, 2));
									SqliteUtil.UpdateServletMsg(MyApplication.db, xx + "", strfwyy);
									((MainActivity) MainActivity.context).setpaomadeng();
								}
							} else if (xxz.equals("07")) {
								// 数据帧
								String sjz = message.substring(12, 14);
								if (sjz.equals("30")) {
									int xuhao = MyFunc.HexToInt(message.substring(18, 20));
									if (xuhao == 255) {
										if (MainActivity.fwyyfile.exists()) {
											MainActivity.fwyyfile.delete();
										}
										MyApplication.qclisr();
									} else {
										MyApplication.fwyylist.remove(xuhao);
										String bcfwyy = "";
										for (int i = 0; i < MyApplication.fwyylist.size(); i++) {
											if (!(MyApplication.fwyylist.get(i).equals(""))) {
												bcfwyy += MyApplication.fwyylist.get(i) + "\r\n";
											}
										}
										if (MainActivity.fwyyfile.exists()) {
											MainActivity.fwyyfile.delete();
										}
										writetxt(bcfwyy);
									}
									((MainActivity) MainActivity.context).setpaomadeng();
								}
							}
						}
					}
					// } catch (Exception e) {
					// Log.e(TAG, "有异常");
					// }
					break;
				case 0x8787:
					((MainActivity) MainActivity.context).setpaomadeng();
				default:
					break;
			}
		};
	};

	public void baocunxl() {
		// 先把当前的线路存起来
		// 本地上行线路
		xlname = getxlhpath(((MainActivity) MainActivity.context).getxlh());
		Log.v(TAG, "xlname:" + xlname);
		File file = new File(xlname);
		File sxlfile = new File(xlname + File.separator + "stationlines.ini");
		File xxlfile = new File(xlname + File.separator + "stationlinex.ini");
		if (!file.exists()) {
			file.mkdirs();
		}
		if (sxlfile.exists()) {
			sxlfile.delete();
		}
		if (xxlfile.exists()) {
			xxlfile.delete();
		}
		CopyFile.copyFile(MainActivity.ConfigureFilePath + "stationlines.ini", xlname + File.separator + "stationlines.ini");
		CopyFile.copyFile(MainActivity.ConfigureFilePath + "stationlinex.ini", xlname + File.separator + "stationlinex.ini");
	}

	/**
	 * 根据线路号从配置文件夹中寻找配置文件，如果没有就返回新建的线路
	 *
	 * @param xlh
	 *            要匹配的线路号
	 * @return 线路号对应的路径
	 */
	public String getxlhpath(String xlh) {
		String path = Environment.getExternalStorageDirectory() + File.separator + "Advert" + File.separator + "ConfigureFile";
		File[] file = new File(path).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].getPath().indexOf(xlh) != -1) {
				return file[i].getPath();
			}
		}
		return path + File.separator + xlh;
	}

	/**
	 * 判断本地是否有对应的线路
	 *
	 * @param xlh
	 *            要匹配的线路号
	 * @return
	 */
	public boolean ishaveclh(String xlh) {
		String path = Environment.getExternalStorageDirectory() + File.separator + "Advert" + File.separator + "ConfigureFile";
		File[] file = new File(path).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].getPath().indexOf(xlh) != -1) {
				return true;
			}
		}
		return false;
	}

	// 把数据存到txt文件
	public void writetxt(String str) {
		// 判断手机上是否存在SD卡并具有读写SD卡权限
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			// 获取SD卡目录
			try {
				File file = Environment.getExternalStorageDirectory();
				// 获取目录中的绝对路径+文件名
				RandomAccessFile raf = new RandomAccessFile(MainActivity.fwyyfile, "rw");
				// 将文件记录指针，移动到最后
				raf.seek(MainActivity.fwyyfile.length());
				// 写入内容
				raf.write(str.getBytes());
				raf.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Toast.makeText(SerialPortService.this, "未检测到SD卡", Toast.LENGTH_SHORT).show();
		}
	}

	public int getxxz(String msg, String str) {
		int xh = msg.indexOf(str) + 2;
		int xhleng = MyFunc.HexToInt(msg.substring(xh, xh + 4));
		int a = MyFunc.HexToInt(msg.substring(xh + 4, xh + 4 + (xhleng * 2)));
		return a;
	}

	public String getleng(String msg, int a) {
		String str = msg;
		for (int i = 2; i < a; i++) {
			int xh = str.indexOf("0" + i) + 2;
			int xhleng = MyFunc.HexToInt(str.substring(xh, xh + 4));
			str = str.substring(xh + 6 + xhleng - 1, str.length());
		}
		return str;
	}

	public String getleng1(String msg) {
		String str = msg;
		int xhleng = MyFunc.HexToInt(msg.substring(2, 4));
		str = msg.substring(2 + 4 + xhleng * 2, msg.length());
		return str;
	}

	/**
	 * 获取数据帧
	 *
	 * @param msg
	 * @return
	 */
	public String getxxz1(String msg) {
		int xhleng = MyFunc.HexToInt(msg.substring(2, 4));
		String a = MyFunc.HexStringTOString(msg.substring(4, 4 + (xhleng * 2)));
		return a;
	}

	public void onCreate() {
		super.onCreate();
		try {
			mSerialPort = new SerialPort(new File(MyApplication.device), MyApplication.baudrate, 0);
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();
			mReadThread = new ReadThread();
			mReadThread.start();
		} catch (Exception e) {
		}
	}

	public void sendHex(String sHex) {
		byte[] bOutArray = MyFunc.HexToByteArr(sHex);
		try {
			mOutputStream.write(bOutArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void SendMsg(String str) {
		int i;
		CharSequence t = str;
		char[] text = new char[t.length()];
		for (i = 0; i < t.length(); i++) {
			text[i] = t.charAt(i);
		}
		try {
			mOutputStream.write(new String(text).getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void closeSerialPort() {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}

	public IBinder onBind(Intent intent) {
		return null;
	}

}
