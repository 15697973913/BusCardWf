package com.example.buscardXiAn.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.example.buscardXiAn.R;
import com.example.buscardXiAn.tools.MyTextView;
import com.example.buscardXiAn.util.SiteMsg_Util;

import java.util.ArrayList;
import java.util.List;


public class shangMyAdapter extends BaseAdapter {
    private List<SiteMsg_Util> list = new ArrayList<SiteMsg_Util>();
    private LayoutInflater inflater;
    private int index;
    private MyTextView zdname, zdname1;
    private ImageView img;
    private FrameLayout layout;
    private Context context;
    private ImageView dqimg;
    private int isdz;

    public shangMyAdapter(List<SiteMsg_Util> list, Context context, int index, int isdz) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.context=context;
        this.index = index;
        this.isdz = isdz;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int arg0, View view, ViewGroup arg2) {
        view = inflater.inflate(R.layout.shanghuangse_item, null);
        zdname = view.findViewById(R.id.shhs_name);
        zdname1 = view.findViewById(R.id.shhs_name1);
        img = view.findViewById(R.id.stype_img);
        layout = view.findViewById(R.id.szdbuju);
        LayoutParams lp;
        lp = (LayoutParams) layout.getLayoutParams();
        lp.width = context.getResources().getDimensionPixelSize(R.dimen.dp_1300) / list.size();
        layout.setLayoutParams(lp);
        zdname.setText(list.get(arg0).getStationName());
        if (list.get(arg0).getStationName() != null) {
            switch (list.get(arg0).getStationName().length()) {
                case 2:
                case 3:
                case 4:
                case 5:
                    zdname.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_22));
                    break;
                case 6:
                    zdname.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_21));
                    break;
                case 7:
                    zdname.setText(list.get(arg0).getStationName().substring(0, 4));
                    zdname1.setText(list.get(arg0).getStationName().substring(4, 7));
                    zdname1.setVisibility(View.VISIBLE);
                    zdname.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_22));
                    zdname1.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_22));
                    break;
                case 8:
                    zdname.setText(list.get(arg0).getStationName().substring(0, 4));
                    zdname1.setText(list.get(arg0).getStationName().substring(4, 8));
                    zdname1.setVisibility(View.VISIBLE);
                    zdname.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_22));
                    zdname1.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_22));
                    break;
                case 9:
                    zdname.setText(list.get(arg0).getStationName().substring(0, 5));
                    zdname1.setText(list.get(arg0).getStationName().substring(5, 9));
                    zdname1.setVisibility(View.VISIBLE);
                    zdname.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_22));
                    zdname1.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_22));
                    break;
                case 10:
                    zdname.setText(list.get(arg0).getStationName().substring(0, 5));
                    zdname1.setText(list.get(arg0).getStationName().substring(5, 10));
                    zdname1.setVisibility(View.VISIBLE);
                    zdname.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_22));
                    zdname1.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_22));
                    break;
                case 11:
                    zdname.setText(list.get(arg0).getStationName().substring(0, 6));
                    zdname1.setText(list.get(arg0).getStationName().substring(6, 11));
                    zdname1.setVisibility(View.VISIBLE);
                    zdname.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_21));
                    zdname1.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_21));
                    break;
                case 12:
                    zdname.setText(list.get(arg0).getStationName().substring(0, 6));
                    zdname1.setText(list.get(arg0).getStationName().substring(6, 12));
                    zdname1.setVisibility(View.VISIBLE);
                    zdname.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_21));
                    zdname1.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_21));
                    break;
                case 13:
                    zdname.setText(list.get(arg0).getStationName().substring(0, 7));
                    zdname1.setText(list.get(arg0).getStationName().substring(7, 13));
                    zdname1.setVisibility(View.VISIBLE);
                    zdname.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_19));
                    zdname1.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_21));
                    break;
                case 14:
                    zdname.setText(list.get(arg0).getStationName().substring(0, 7));
                    zdname1.setText(list.get(arg0).getStationName().substring(7, 14));
                    zdname1.setVisibility(View.VISIBLE);
                    zdname.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_19));
                    zdname1.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_19));
                    break;
                case 15:
                    zdname.setText(list.get(arg0).getStationName().substring(0, 8));
                    zdname1.setText(list.get(arg0).getStationName().substring(8, 15));
                    zdname1.setVisibility(View.VISIBLE);
                    zdname.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_18));
                    zdname1.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_19));
                    break;
                case 16:
                    zdname.setText(list.get(arg0).getStationName().substring(0, 8));
                    zdname1.setText(list.get(arg0).getStationName().substring(8, 16));
                    zdname1.setVisibility(View.VISIBLE);
                    zdname.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_18));
                    zdname1.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.sp_18));
                    break;
                default:
                    break;
            }
        }
        TextPaint tp = zdname.getPaint();
        tp.setFakeBoldText(true);
        TextPaint tp1 = zdname1.getPaint();
        tp1.setFakeBoldText(true);
        if (arg0 < index) {
            img.setImageResource(R.mipmap.bs_yuan);
            zdname.setTextColor(Color.parseColor("#000000"));
            zdname1.setTextColor(Color.parseColor("#000000"));
        } else if (arg0 == index) {
            if (isdz == 1) {
                img.setImageResource(R.mipmap.hs_yuan);
                zdname.setTextColor(Color.parseColor("#ff0000"));
                zdname1.setTextColor(Color.parseColor("#ff0000"));
            } else {
                dqimg = img;
                handler.sendEmptyMessage(0x4141);
                zdname.setTextColor(Color.parseColor("#ff0000"));
                zdname1.setTextColor(Color.parseColor("#ff0000"));
            }
        } else {
            img.setImageResource(R.mipmap.heise_yuan);
            zdname.setTextColor(Color.parseColor("#000000"));
            zdname1.setTextColor(Color.parseColor("#000000"));
        }
        return view;
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0x4141:
                    dqimg.setImageResource(R.mipmap.bs_yuan);
                    handler.sendEmptyMessageDelayed(0x3131, 500);
                    break;
                case 0x3131:
                    dqimg.setImageResource(R.mipmap.heise_yuan);
                    handler.sendEmptyMessageDelayed(0x4141, 500);
                    break;
                default:
                    break;
            }
        }

        ;
    };
}
