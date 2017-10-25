package buscardxian.ncrf.jiege.buscardxian;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import buscardxian.ncrf.jiege.buscardxian.adapter.MyAdapter;
import buscardxian.ncrf.jiege.buscardxian.adapter.shangMyAdapter;
import buscardxian.ncrf.jiege.buscardxian.application.MyApplication;
import buscardxian.ncrf.jiege.buscardxian.server.SerialPortService;
import buscardxian.ncrf.jiege.buscardxian.tools.CopyFile;
import buscardxian.ncrf.jiege.buscardxian.tools.GetLineMsg;
import buscardxian.ncrf.jiege.buscardxian.tools.HorizontalListView;
import buscardxian.ncrf.jiege.buscardxian.tools.SqliteUtil;
import buscardxian.ncrf.jiege.buscardxian.util.SiteMsg_Util;
import butterknife.BindView;
import butterknife.ButterKnife;

import static buscardxian.ncrf.jiege.buscardxian.R.id.LineWord;
import static buscardxian.ncrf.jiege.buscardxian.R.id.StationDownLast;
import static buscardxian.ncrf.jiege.buscardxian.R.id.StationUpLast;
import static buscardxian.ncrf.jiege.buscardxian.R.id.dzts;
import static buscardxian.ncrf.jiege.buscardxian.R.id.xllist;
import static buscardxian.ncrf.jiege.buscardxian.R.id.zhanming;
import static buscardxian.ncrf.jiege.buscardxian.R.id.zhuangtai;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    public static Context context;
    @BindView(LineWord)
    TextView mLineWord;
    @BindView(StationUpLast)
    TextView mStationUpLast;
    @BindView(StationDownLast)
    TextView mStationDownLast;
    @BindView(zhuangtai)
    TextView mZhuangtai;
    @BindView(zhanming)
    TextView mZhanming;
    @BindView(dzts)
    LinearLayout mDzts;
    @BindView(R.id.list1)
    HorizontalListView mList1;
    @BindView(R.id.list2)
    HorizontalListView mList2;
    @BindView(xllist)
    FrameLayout mXllist;
    private static TextView mPaomadeng;
    @BindView(R.id.blankpart)
    LinearLayout mBlankpart;
    private List<SiteMsg_Util> list1, list2;
    private MyAdapter adapter;
    private shangMyAdapter sadapter;
    private SiteMsg_Util util;
    // 系统根路径
    public static String RootPath = Environment.getExternalStorageDirectory().toString();
    // 总路径
    public static String ZongFilePath = RootPath + File.separator + "Advert";
    // 配置文件路径
    public static String ConfigureFilePath = RootPath + File.separator + "Advert" + File.separator + "ConfigureFile" + File.separator;
    public static File fwyyfile = new File(ConfigureFilePath + "fwyy.txt");
    public SharedPreferences sha;
    public boolean isOneStart = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 隐藏状态栏
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        init();// 初始化控件
    }

    // 设置站点信息

    /**
     * @param i 判断要设置哪个信息1 ：线路信息 2：上行 3：下行 4:全部
     */
    public void getLineMsg(final int i) {
        new Thread() {
            public void run() {
                // 判断文件是否存在
                switch (i) {
                    case 1:
                        GetLineMsg.getline();
                        break;
                    case 2:
                        GetLineMsg.getsxsite();
                        break;
                    case 3:
                        GetLineMsg.getxxsite();
                        break;
                    case 4:
                        GetLineMsg.getline();
                        GetLineMsg.getsxsite();
                        GetLineMsg.getxxsite();
                        break;
                    default:
                        break;
                }
                // 设置listview
                handler.sendEmptyMessage(0x2153);
            }

            ;
        }.start();
    }

    public String getxlh() {
        return mLineWord.getText().toString();
    }

    public void copyfile() {
        CopyFile.CopyAssetsFile("fwyy.txt", ConfigureFilePath);
        GetLineMsg.readfwyyMsgToSqlite();
        CopyFile.CopyAssetsFile("stationline.ini", ConfigureFilePath);
        GetLineMsg.readLineMsgToSqlite();
        CopyFile.CopyAssetsFile("stationlines.ini", ConfigureFilePath);
        GetLineMsg.readsxLineMsgToSqlite();
        CopyFile.CopyAssetsFile("stationlinex.ini", ConfigureFilePath);
        GetLineMsg.readxxLineMsgToSqlite();

    }

    public static void setpaomadeng() {
        MyApplication.fwyylist = new ArrayList<>();
        MyApplication.fwyylist = SqliteUtil.QueryAllServletMsg(MyApplication.db);
        StringBuffer buffer = new StringBuffer("");
        for (int i = 0; i < MyApplication.fwyylist.size(); i++) {
            if (!(MyApplication.fwyylist.get(i).equals(""))) {
                buffer.append(MyApplication.fwyylist.get(i) + "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t");
            }
        }
        mPaomadeng.setText("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t" + buffer.toString());
    }

    /**
     * @param str 实时信息
     */
    public static void setssxx(String str) {
        mPaomadeng.setText(str);
    }

    public void setListview() {
        addlist(MyApplication.sxlist);
        adapter = new MyAdapter(list2, this, list2.size(), 0);
        sadapter = new shangMyAdapter(list1, this, 0, 0);
        mList1.setAdapter(sadapter);
        mList2.setAdapter(adapter);
    }

    Runnable r = new Runnable() {
        @Override
        public void run() {
            mXllist.setVisibility(View.VISIBLE);
            mDzts.setVisibility(View.GONE);
        }
    };

    // 设置到站
    public void setdaozhen(int index, int dlz, List<SiteMsg_Util> list, int sxx) {
        Log.v(TAG, "Mainindex:" + index);
        String str = "";
        if (sxx == 1) {
            str = MyApplication.xxlist.get(index - 1).getStationName();
        } else {
            str = MyApplication.sxlist.get(index - 1).getStationName();
        }
        if (dlz == 1) {
            mXllist.setVisibility(View.GONE);
            mDzts.setVisibility(View.VISIBLE);
            Log.v(TAG, "到站");
            mZhuangtai.setText("到站:");
            mZhanming.setText(str);
            handler.removeCallbacks(r);
            handler.postDelayed(r, 15000);
            if (index <= list.size() / 2) {
                sadapter = new shangMyAdapter(list1, this, index - 1, 1);
                adapter = new MyAdapter(list2, this, list2.size(), 0);
                mList1.setAdapter(sadapter);
                mList2.setAdapter(adapter);
            } else {
                sadapter = new shangMyAdapter(list1, this, list1.size(), 0);
                adapter = new MyAdapter(list2, this, list2.size() - (index - list.size() / 2), 1);
                mList1.setAdapter(sadapter);
                mList2.setAdapter(adapter);
            }
        } else {
            mDzts.setVisibility(View.VISIBLE);
            mXllist.setVisibility(View.GONE);
            mZhuangtai.setText("下一站:");
            mZhanming.setText(str);
            handler.sendEmptyMessageDelayed(0x8181, 15000);
            Log.v(TAG, "离站");
            Log.v(TAG, "list/2=" + list.size() / 2);
            if (index <= list.size() / 2) {
                sadapter = new shangMyAdapter(list1, this, index - 1, 0);
                adapter = new MyAdapter(list2, this, list2.size(), 0);
                mList1.setAdapter(sadapter);
                mList2.setAdapter(adapter);
            } else {
                sadapter = new shangMyAdapter(list1, this, list1.size(), 0);
                adapter = new MyAdapter(list2, this, list2.size() - (index - list.size() / 2), 0);
                Log.v(TAG, "list2size=" + list2.size());
                mList1.setAdapter(sadapter);
                mList2.setAdapter(adapter);
            }
        }
    }

    public void setsxx(int i) {
        // 如果等于1就设置为上行
        if (i == 1) {
            Log.v(TAG, "设置为上行");
            addlist(MyApplication.sxlist);
            mStationUpLast.setText(MyApplication.sxlist.get(0).getStationName());
            mStationDownLast.setText(MyApplication.sxlist.get(MyApplication.sxlist.size() - 1).getStationName());
            // 否则就是下行
        } else {
            Log.v(TAG, "设置为下行");
            addlist(MyApplication.xxlist);
            mStationUpLast.setText(MyApplication.xxlist.get(0).getStationName());
            mStationDownLast.setText(MyApplication.xxlist.get(MyApplication.xxlist.size() - 1).getStationName());
        }

        adapter = new MyAdapter(list2, this, list2.size(), 0);
        sadapter = new shangMyAdapter(list1, this, 0, 0);
        mList1.setAdapter(sadapter);
        mList2.setAdapter(adapter);
    }

    private void addlist(List<SiteMsg_Util> list) {
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();
        int a = list.size();
        for (int i = 0; i < list.size(); i++) {
            if (i < list.size() / 2) {
                util = new SiteMsg_Util();
                util.setStationName(list.get(i).getStationName());
                list1.add(util);

            } else {
                util = new SiteMsg_Util();
                util.setStationName(list.get(a - 1).getStationName());
                list2.add(util);
                a--;
            }
        }
    }

    private void createfile() {
        // 存放广告的路径
        try {
            File file = new File(ZongFilePath);
            if (!file.exists()) {
                file.mkdirs();
                Log.v(TAG, "创建总文件夹成功");
            } else {
                Log.v(TAG, "文件夹已存在");
                Log.v(TAG, "FolderPath:" + ZongFilePath);
            }
            file = new File(ConfigureFilePath);

            if (!file.exists()) {
                file.mkdirs();
                Log.v(TAG, "创建配置文件夹成功");
            } else {
                Log.v(TAG, "文件夹已存在");
                Log.v(TAG, "FolderPath:" + ConfigureFilePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v(TAG, "创建文件夹失败");
        }
    }

    // 初始化控件
    private void init() {
        context = MainActivity.this;
        mPaomadeng = findViewById(R.id.paomadeng);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,0, MyApplication.weight);
        mBlankpart.setLayoutParams(param);
        createfile();// 创建文件夹
        //如果是第一次启动就从本地获取数据
        sha = getSharedPreferences("isone", Context.MODE_PRIVATE);
        isOneStart = sha.getBoolean("isonestart", true);
        if (isOneStart) {
            copyfile();
            SharedPreferences.Editor editor = sha.edit();
            editor.putBoolean("isonestart", false);
            editor.commit();
        }
        getLineMsg(4);// 设置站点信息
        // 接收串口数据服务
        Intent intent = new Intent(MainActivity.this, SerialPortService.class);
        startService(intent);
        setpaomadeng();// 设置跑马灯
    }

    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x2153:
                    // 设置listview
                    setListview();
                    setxlmsg();
                    break;
                default:
                    break;
            }
        }

        // 设置线路信息
        private void setxlmsg() {
            mLineWord.setText(MyApplication.line_util.getLineWord());
            mStationUpLast.setText(MyApplication.line_util.getStationUpLast());
            mStationDownLast.setText(MyApplication.line_util.getStationDownLast());
        }

        ;
    };

    /**
     * 获取和保存当前屏幕的截图
     */
//    private void GetandSaveCurrentImage() {
//        // 1.构建Bitmap
//        WindowManager windowManager = getWindowManager();
//        Display display = windowManager.getDefaultDisplay();
//        int w = display.getWidth();
//        int h = display.getHeight();
//
//        Bitmap Bmp = Bitmap.createBitmap(w, h, Config.ARGB_8888);
//
//        // 2.获取屏幕
//        View decorview = this.getWindow().getDecorView();
//        decorview.setDrawingCacheEnabled(true);
//        Bmp = decorview.getDrawingCache();
//
//        String SavePath = ConfigureFilePath;
//
//        // 3.保存Bitmap
//        try {
//            File path = new File(SavePath);
//            // 文件
//            String filepath = SavePath + "/Screen_1.png";
//            File file = new File(filepath);
//            if (!path.exists()) {
//                path.mkdirs();
//            }
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//
//            FileOutputStream fos = null;
//            fos = new FileOutputStream(file);
//            if (null != fos) {
//                Bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
//                fos.flush();
//                fos.close();
//                Toast.makeText(MainActivity.this, "截图成功" + filepath, Toast.LENGTH_LONG).show();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(MainActivity.this, "截图失败", Toast.LENGTH_LONG).show();
//        }
//    }

}
