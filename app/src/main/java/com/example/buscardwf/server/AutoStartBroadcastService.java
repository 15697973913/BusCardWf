package com.example.buscardwf.server;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.buscardwf.MainActivity;
import com.example.buscardwf.application.MyApplication;
import com.example.buscardwf.tools.CopyFile;
import com.example.buscardwf.tools.GetLineMsg;

import java.io.File;
import java.util.Objects;


public class AutoStartBroadcastService extends Service {
    public static AutoStartBroadcastService service;

    public static class AutoStartBroadcastReceiver extends BroadcastReceiver {
        private final String ACTION = "android.intent.action.BOOT_COMPLETED";
        private final String MOUNTED = "android.intent.action.MEDIA_MOUNTED";
        private final String UNMOUNTED = "android.intent.action.MEDIA_UNMOUNTED";
        private final String TAG = "BroadcastReceiver";
        private String USBPATH = "";
        private final String BENDIPATH = Environment.getExternalStorageDirectory() + "/Advert";// 1A09-2B6C
        private boolean ishavasd;
        private Context context;
        public String sdPath;

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "ACTION:" + ACTION);
            if (Objects.equals(intent.getAction(), ACTION) || Objects.equals(intent.getAction(), MOUNTED) || Objects.equals(intent.getAction(), UNMOUNTED)) {
                if (MainActivity.context == null) {
                    Intent newIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                    context.startActivity(newIntent);
                    ((MyApplication) MyApplication.context).chongqi();
                }
            }
            if (Objects.equals(intent.getAction(), MOUNTED)) {
                if (intent.getData()==null){
                    return;
                }
                sdPath = intent.getData().getPath();
                Log.v(TAG, "U盘路径为：" + sdPath);

                if (MyApplication.BOOTTYPE) {
                    handler.sendEmptyMessage(0x6151);
                } else {
                    handler.sendEmptyMessage(0x5151);
                }
                this.context = context;
                Log.v(TAG, "SD卡插入");
            }
        }

        @SuppressLint("HandlerLeak")
        Handler handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 0x5151:
                        Log.v(TAG, "sdpath:" + sdPath);
                        USBPATH = sdPath + "/BusCard";
                        File file = new File(USBPATH);
                        Log.v(TAG, "file:" + USBPATH);
                        ishavasd = file.exists();
                        Log.v(TAG, "ishavasd:" + ishavasd);
                        String sdapkpath = USBPATH + "/Apk/BusCardWf.apk";
                        String bdapkpath = BENDIPATH + "/Apk/BusCardWf.apk";
                        File filejia = new File(BENDIPATH + "/Apk");
                        if (!filejia.exists()) {
                            if (!filejia.mkdirs()){
                                Log.v(TAG, "创建Apk文件夹失败");
                            }
                        }
                        File bdapk = new File(bdapkpath);
                        // 复制安装包
                        String sdconfigpath = USBPATH + "/ConfigureFile";
                        String bdconfigpath = BENDIPATH + "/ConfigureFile";
                        File sdconfigfile = new File(sdconfigpath);
                        if (sdconfigfile.exists()) {
                            CopyFile.copyFolder(sdconfigpath, bdconfigpath);
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    GetLineMsg.readfwyyMsgToSqlite();
                                    GetLineMsg.readLineMsgToSqlite();
                                    GetLineMsg.readsxLineMsgToSqlite();
                                    GetLineMsg.readxxLineMsgToSqlite();
                                }
                            }.start();
                        }
                        Log.v(TAG, "sdapkpath:" + sdapkpath);
                        if (new File(sdapkpath).exists()) {
                            Log.v(TAG, "有安装包");
                            if (bdapk.exists()) {
                                if (bdapk.delete()){
                                    Log.v(TAG, "删除apk失败");
                                }
                            }
                            CopyFile.copyFile(sdapkpath, bdapkpath);
                            Intent intent = new Intent("android.intent.action.SILENT_INSTALL_PACKAGE");
                            intent.putExtra("apkFilePath", bdapkpath);
                            context.sendBroadcast(intent);
                        } else {
                            Log.v(TAG, "无安装包");
                        }
                        break;
                    case 0x6151:
                        Log.v(TAG, "sdPath:" + sdPath);
                        USBPATH = sdPath + "/BusCard";
                        File file1 = new File(USBPATH);
                        Log.v(TAG, "file:" + USBPATH);
                        ishavasd = file1.exists();
                        Log.v(TAG, "ishavasd:" + ishavasd);
                        String sdapkpath1 = USBPATH + "/Apk/BusCardWf.apk";
                        String bdapkpath1 = BENDIPATH + "/Apk/BusCardWf.apk";
                        File filejia1 = new File(BENDIPATH + "/Apk");
                        if (!filejia1.exists()) {
                            if (!filejia1.mkdirs()){
                                Log.v(TAG, "创建Apk文件夹失败");
                            }
                        }
                        File bdapk1 = new File(bdapkpath1);
                        // 复制安装包
                        String sdconfigpath1 = USBPATH + "/ConfigureFile";
                        String bdconfigpath1 = BENDIPATH + "/ConfigureFile";
                        File sdconfigfile1 = new File(sdconfigpath1);
                        if (sdconfigfile1.exists()) {
                            CopyFile.copyFolder(sdconfigpath1, bdconfigpath1);
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    GetLineMsg.readfwyyMsgToSqlite();
                                    GetLineMsg.readLineMsgToSqlite();
                                    GetLineMsg.readsxLineMsgToSqlite();
                                    GetLineMsg.readxxLineMsgToSqlite();
                                }
                            }.start();
                        }
                        Log.v(TAG, "sdapkpath:" + sdapkpath1);
                        if (new File(sdapkpath1).exists()) {
                            Log.v(TAG, "有安装包");
                            if (bdapk1.exists()) {
                                if (bdapk1.delete()){
                                    Log.v(TAG, "删除apk失败");
                                }
                            }
                            CopyFile.copyFile(sdapkpath1, bdapkpath1);
                            installNewApp(bdapkpath1, context);
                        } else {
                            Log.v(TAG, "无安装包");
                        }
                        break;
                    default:
                        break;
                }
            }
        };
    }

    public static void installNewApp(String path, Context context) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + path),
                "application/vnd.android.package-archive");
        context.startActivity(i);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        service = AutoStartBroadcastService.this;
    }
}

