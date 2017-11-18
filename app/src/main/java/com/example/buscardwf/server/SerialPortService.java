package com.example.buscardwf.server;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.example.buscardwf.MainActivity;
import com.example.buscardwf.application.MyApplication;
import com.example.buscardwf.tools.MyFunc;
import com.example.buscardwf.tools.SqliteUtil;

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
    private String TAG = "SerialPortService";
    public String strResponse = "";

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
                    System.arraycopy(buffer, 0, bRec, 0, size);
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
        Log.v(TAG, "strResponse：" + strResponse);
        if (!(strResponse.length() < 25)) {
            int btindex = strResponse.indexOf("1E60");
            // 如果没有1E60就是潍坊协议
            if (btindex == -1) {
                Log.v(TAG, "aaaa" + strResponse);
                int wfbtindex = strResponse.indexOf("7E");
                Log.v(TAG, "7E的位置是：" + wfbtindex);
                // 截取"7E"后面的数据
                if (!strResponse.contains("7E")) {
                    strResponse = "";
                    return;
                }
                if (strResponse.length() - wfbtindex < 15) {
                    return;
                }
                strResponse = strResponse.substring(wfbtindex, strResponse.length());
                wfbtindex = strResponse.indexOf("7E");
                String wfstrtemp = strResponse.substring(wfbtindex + 2, strResponse.length());
                if (wfstrtemp.length() < 15) {
                    return;
                }
                // 数据帧长度
                int wfsjzcd = MyFunc.HexToInt(strResponse.substring(10, 14));
//				Log.v(TAG, "信息帧长度:" + strResponse.substring(10, 14) + "   " + MyFunc.HexToInt(strResponse.substring(10, 14)));
                if (wfsjzcd > 3000) {
                    if (strResponse.contains("7F7E")) {
                        strResponse = strResponse.substring(strResponse.indexOf("7F7E") + 2, strResponse.length());
                        return;
                    } else {
                        strResponse = "";
                        return;
                    }
                }
//				Log.v(TAG, "strResponse.length()=" + strResponse.length());
//				Log.v(TAG, "(wfsjzcd + 9) * 2)=" + (wfsjzcd + 9) * 2);
                if (strResponse.length() < (wfsjzcd + 9) * 2) {
                    return;
                } else {
                    // 判断最后一位十是否是“7F”
                    if (strResponse.length() == (wfsjzcd + 9) * 2) {
                        // 截取最后两位
                        String wfzhlw = strResponse.substring(strResponse.length() - 2, strResponse.length());
                        if (!wfzhlw.equalsIgnoreCase("7F")) {
                            strResponse = "";
                            return;
                        }
                        if (jiaoyan(strResponse)) {
                            Message message = new Message();
                            message.obj = strResponse.substring(2, strResponse.length());
                            message.what = 0x1313;
                            handler.sendMessage(message);
                        }
                        strResponse = "";
                        return;
                    } else {
                        // 如果不是就截取对应长度，保留后面的数据
                        String aa = strResponse.substring(0, (wfsjzcd + 9) * 2);
                        String wfzhlw1 = aa.substring(aa.length() - 2, aa.length());
                        if (!wfzhlw1.equalsIgnoreCase("7F")) {
                            strResponse = strResponse.substring(strResponse.indexOf("7F7E") + 2, strResponse.length());
                            return;
                        }
                        if (strResponse.length() > (wfsjzcd + 9) * 2) {
                            // 把多余的截取出来
                            strResponse = strResponse.substring((wfsjzcd + 9) * 2, strResponse.length());
                        }
                        Log.v(TAG, "aa:" + aa);
                        Log.v(TAG, "aa.leng=" + aa.length());
                        if (jiaoyan(aa)) {
                            Message message = new Message();
                            message.obj = aa.substring(2, aa.length());
                            message.what = 0x1313;
                            handler.sendMessage(message);
                        }
                        return;
                    }
                }
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
            Log.v(TAG, "信息帧长度:" + strtemp.substring(8, 12));
            if (sjzcd > 1500) {
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
//				Log.v(TAG, "aa:" + aa);
                if (jiaoyan(aa)) {
                    Message message = new Message();
                    message.obj = aa.substring(4, aa.length());
                    message.what = 0x1313;
                    handler.sendMessage(message);
                }
            }
        }
    }

    /**
     * 判断校验位是否正确的方法
     *
     * @param str 要校验的数据
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
     * @param sjz 要校验的数据
     * @return 获得的校验位
     */
    public String getjyw(String sjz) {
        int yhzhi = Integer.parseInt(sjz.substring(0, 2), 16);
        for (int i = 2; i < sjz.length(); i += 2) {
            int bb = Integer.parseInt(sjz.substring(i, i + 2), 16);
            yhzhi = yhzhi ^ bb;
        }
        Log.v(TAG, "校验位：" + Integer.toHexString(yhzhi));
        return Integer.toHexString(yhzhi);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x1313:
                    try {
                        String message = msg.obj.toString();

//					Log.v(TAG, "接收的串口数据为:" + message);
                        String lsh = message.substring(6, 8);
                        // 目标地址
                        String mbdz = message.substring(0, 2);
                        // 截取最后两位
                        String zhlw = message.substring(message.length() - 2, message.length());
                        boolean is7f = zhlw.equalsIgnoreCase("7f");
                        // 原地址
                        String ydz = message.substring(2, 4);
//                        if (!(mbdz.equals("20")||mbdz.equals("10")||mbdz.equalsIgnoreCase("FF"))){
//                            return;
//                        }
                        if (mbdz.equals("20") && !is7f) {
                            String sjz1 = "1E60";
                            String sjz2 = "050100040C000101";
                            String sjz = sjz1 + ydz + mbdz + sjz2;
                            // 巡检回复
                            if (!message.equalsIgnoreCase("2001010100005F1F")) {

                                sjz = "1E60" + ydz + mbdz + "05" + lsh + "0008090001010A000101";
                            }
                            String sendmsg = sjz + getjyw(sjz) + "1F";
                            Log.v(TAG, "发送的串口为：" + sendmsg);
                            sendHex(sendmsg);
                        }
                        if (is7f && mbdz.equals("10")) {
                            String wfsjz1 = "7E";
                            String wfsjz2 = "00081000010111000101";
                            String sjz = wfsjz1 + ydz + mbdz + "0A" + lsh + wfsjz2;
                            String sendmsg = sjz + getjyw(sjz) + "7F";
                            Log.v(TAG, "发送的串口为：" + sendmsg);
                            sendHex(sendmsg);
                        }
                        // 将路牌调为下行
                        if (message.equalsIgnoreCase("FF020701000402000101831F")) {
                            ((MainActivity) MainActivity.context).setsxx(2);
                            // 将路牌调为上行
                        } else if (message.equalsIgnoreCase("FF020701000402000100821F")) {
                            ((MainActivity) MainActivity.context).setsxx(1);
                        } else {
                            // 截取消息帧流水号
                            String xxz = message.substring(4, 6);
                            Log.v(TAG, "消息帧流水号：" + xxz);
                            if (xxz.equals("03")) {
                                int xxzleng = MyFunc.HexToInt(message.substring(8, 12));
                                // 截取消息帧
                                String msgz = message.substring(12, 12 + xxzleng * 2);
                                Log.v(TAG, "消息帧:" + msgz);
                                // 到、离站 到站： 1 离站： 2;

                                int islz = getxxz(getleng(msgz, 4), "04");
                                // 截取站点号
                                int sxxbz = getxxz(msgz, "02");
                                if (!is7f) {
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
                                        ((MainActivity) MainActivity.context).setdaozhen(MyApplication.xxlist.size() - dzxh + 1, islz,
                                                MyApplication.xxlist, 1);
                                    } else {
                                        ((MainActivity) MainActivity.context).setdaozhen(dzxh, islz, MyApplication.sxlist, 0);
                                    }
                                } else {
                                    if (sxxbz == 1) {
                                        ((MainActivity) MainActivity.context).setsxx(1);
                                    } else if (sxxbz == 2) {
                                        ((MainActivity) MainActivity.context).setsxx(2);
                                    }
                                    // 站点序号
                                    int dzxh = getxxz(getleng(msgz, 5), "05");
                                    Log.v(TAG, "到站序号：" + dzxh);
                                    // 下行
                                    if (sxxbz == 2) {
                                        ((MainActivity) MainActivity.context).setdaozhen(dzxh, islz, MyApplication.xxlist, 1);
                                    } else {
                                        ((MainActivity) MainActivity.context).setdaozhen(dzxh, islz, MyApplication.sxlist, 0);
                                    }
                                }
                            } else if (xxz.equals("06") || (is7f && xxz.equals("10"))) {
                                // 判断是上行还是下行 上行：0e 下行：0f
                                String sxx = message.substring(12, 14);
                                Log.v(TAG, "上下行标识：" + sxx);
                                // 获取长度
                                int leng = MyFunc.HexToInt(message.substring(14, 18));
                                Log.v(TAG, "截取数据长度：" + message.substring(14, 18));
                                Log.v(TAG, "截取完长度：" + leng);
                                String cshmsg = message.substring(18, 18 + leng * 2);
                                Log.v(TAG, "cshmsg:" + cshmsg);
                                Log.v(TAG, "cshmsg长度：" + cshmsg.length() / 2);
                                if (sxx.equalsIgnoreCase("0e") || sxx.equalsIgnoreCase("21")) {
                                    SqliteUtil.DeleteLine(MyApplication.db, "up");
                                    for (int i = 1; ; i++) {
                                        if (MyFunc.HexToInt((cshmsg.substring(0, 2))) != i) {
                                            Log.v(TAG, "跳出循环");
                                            break;
                                        }
                                        // 站点名称
                                        String zdname = getxxz1(cshmsg);
                                        // 站点序号
                                        int zdxh = MyFunc.HexToInt((cshmsg.substring(0, 2)));
                                        SqliteUtil.InsertLine(MyApplication.db, "up", zdname, zdxh + "");
                                        cshmsg = getleng1(cshmsg);
                                        if (cshmsg.length() < 4) {
                                            Log.v(TAG, "跳出循环");//
                                            break;
                                        }//few
                                    }
                                    ((MainActivity) MainActivity.context).getLineMsg(2);
                                } else if (sxx.equalsIgnoreCase("0f") || sxx.equalsIgnoreCase("22")) {
                                    int ktxh = MyFunc.HexToInt((cshmsg.substring(0, 2)));
                                    SqliteUtil.DeleteLine(MyApplication.db, "down");
                                    for (int i = ktxh; ; ) {
                                        if (MyFunc.HexToInt((cshmsg.substring(0, 2))) != i) {
                                            Log.v(TAG, "跳出循环");
                                            break;
                                        }
                                        // 站点名称
                                        String zdname = getxxz1(cshmsg);
                                        // 站点序号
                                        int zdxh = MyFunc.HexToInt((cshmsg.substring(0, 2)));
                                        SqliteUtil.InsertLine(MyApplication.db, "down", zdname, zdxh + "");
                                        cshmsg = getleng1(cshmsg);
                                        if (cshmsg.length() < 4) {
                                            Log.v(TAG, "跳出循环");
                                            break;
                                        }
                                        if (sxx.equalsIgnoreCase("0f")) {
                                            i--;
                                        } else {
                                            i++;
                                        }

                                    }
                                    ((MainActivity) MainActivity.context).getLineMsg(3);
                                } else if (sxx.equalsIgnoreCase("1b")) {
                                    String bb = message.substring(26, message.length());
                                    String xlh = getxxz1(bb);
                                    int xhleng = MyFunc.HexToInt(bb.substring(2, 4));
                                    bb = bb.substring(4 + xhleng * 2, bb.length());
                                    String qd = getxxz1(bb);
                                    xhleng = MyFunc.HexToInt(bb.substring(2, 4));
                                    bb = bb.substring(4 + xhleng * 2, bb.length());
                                    String zd = getxxz1(bb);
                                    Log.v(TAG, "线路号：" + xlh + "  起点站：" + qd + "  终点站:" + zd);
                                    SqliteUtil.DeleteLineNum(MyApplication.db);
                                    SqliteUtil.insertMsg(MyApplication.db, xlh, qd, zd);
                                    ((MainActivity) MainActivity.context).getLineMsg(1);
                                } else if (sxx.equalsIgnoreCase("11")) {
                                    String fwyy = cshmsg.substring(14, cshmsg.length());
                                    String strfwyy = MyFunc.HexStringTOString(fwyy);
                                    Log.v(TAG, "服务用语：" + strfwyy);
                                    String xx = cshmsg.substring(0, 2);
                                    if (xx.equals("00")) {
                                        if (MainActivity.fwyyfile.exists()) {
                                            if (MainActivity.fwyyfile.delete()){
                                                Log.v(TAG, "删除成功");
                                            }
                                        }
                                    }
                                    SqliteUtil.UpdateServletMsg(MyApplication.db, xx + "", strfwyy);
                                } else if (sxx.equals("24")) {
                                    // cshmsg
                                    while (true) {
                                        if (cshmsg.length() < 8) {
                                            break;
                                        }
                                        int xh = MyFunc.HexToInt(cshmsg.substring(0, 2));
                                        int len = MyFunc.HexToInt(cshmsg.substring(4, 6));
                                        String fwyy = MyFunc.HexStringTOString(cshmsg.substring(6, 6 + len * 2));
                                        if (xh == 1) {
                                            if (MainActivity.fwyyfile.exists()) {
                                                if (MainActivity.fwyyfile.delete()){
                                                    Log.v(TAG, "删除成功");
                                                }
                                            }
                                        }
                                        SqliteUtil.UpdateServletMsg(MyApplication.db, xh + "", fwyy);
                                        cshmsg = cshmsg.substring(6 + len * 2, cshmsg.length());
                                    }
                                }
                                // 潍坊获取线路信息
                            } else if (xxz.equals("04") && is7f) {
                                int xxzcd = MyFunc.HexToInt(message.substring(8, 12));
                                // 数据帧
                                String sjzmsg = message.substring(12, 12 + xxzcd * 2);
                                Log.v(TAG, "数据帧：" + sjzmsg);
                                if (sjzmsg.substring(0, 2).equals("30")) {
                                    int cd = MyFunc.HexToInt(sjzmsg.substring(2, 6));
                                    sjzmsg = sjzmsg.substring(6 + cd * 2, sjzmsg.length());
                                }
                                int xlhcd = MyFunc.HexToInt(sjzmsg.substring(2, 6));
                                String xlh = MyFunc.HexStringTOString(sjzmsg.substring(6, 6 + xlhcd * 2));
                                sjzmsg = sjzmsg.substring(6 + xlhcd * 2, sjzmsg.length());
                                Log.v(TAG, "截取完线路号：" + sjzmsg);
                                int wfqdzcd = MyFunc.HexToInt(sjzmsg.substring(2, 6));
                                String wfqdz = MyFunc.HexStringTOString(sjzmsg.substring(6, 6 + wfqdzcd * 2));
                                Log.v(TAG, "起点Hex：" + sjzmsg.substring(6, 6 + wfqdzcd * 2));
                                sjzmsg = sjzmsg.substring(6 + wfqdzcd * 2, sjzmsg.length());
                                Log.v(TAG, "截取完起点：" + sjzmsg);

                                int wfkw = MyFunc.HexToInt(sjzmsg.substring(2, 6));
                                sjzmsg = sjzmsg.substring(6 + wfkw * 2, sjzmsg.length());

                                int wfzdzcd = MyFunc.HexToInt(sjzmsg.substring(2, 6));
                                String wfzdz = MyFunc.HexStringTOString(sjzmsg.substring(6, 6 + wfzdzcd * 2));
                                Log.v(TAG, "终点Hex：" + sjzmsg.substring(6, 6 + wfzdzcd * 2));

                                Log.v(TAG, "线路号：" + xlh + "  起点：" + wfqdz + "  终点:" + wfzdz);
                                SqliteUtil.DeleteLineNum(MyApplication.db);
                                SqliteUtil.insertMsg(MyApplication.db, xlh, wfqdz, wfzdz);
                                ((MainActivity) MainActivity.context).getLineMsg(1);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "handler有异常");
                    }
                    break;
                default:
                    break;
            }
        }

    };

    // 把数据存到txt文件
    public void writetxt(String str) {
        // 判断手机上是否存在SD卡并具有读写SD卡权限
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // 获取SD卡目录
            try {
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
        return MyFunc.HexToInt(msg.substring(xh + 4, xh + 4 + (xhleng * 2)));
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
        String str;
        int xhleng = MyFunc.HexToInt(msg.substring(2, 4));
        str = msg.substring(2 + 4 + xhleng * 2, msg.length());
        return str;
    }

    /**
     * 获取数据帧
     *
     * @param msg  数据
     * @return 数据帧
     */
    public String getxxz1(String msg) {
        int xhleng = MyFunc.HexToInt(msg.substring(2, 4));
        return MyFunc.HexStringTOString(msg.substring(4, 4 + (xhleng * 2)));
    }

    public void onCreate() {
        super.onCreate();
        try {
            mSerialPort = new SerialPort(new File(MyApplication.device), MyApplication.baudrate, 0);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            ReadThread readThread = new ReadThread();
            readThread.start();
        } catch (Exception ignored) {
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

    public IBinder onBind(Intent intent) {
        return null;
    }

}
