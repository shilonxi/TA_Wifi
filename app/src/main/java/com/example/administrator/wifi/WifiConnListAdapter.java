package com.example.administrator.wifi;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class WifiConnListAdapter extends BaseAdapter
{
    private LayoutInflater inflater;
    private ArrayList<WifiElement> mArr;
    //建立变量

    public WifiConnListAdapter(Context context,ArrayList<WifiElement> list)
    {
        this.inflater=LayoutInflater.from(context);
        //获取到LayoutInflater的实例
        this.mArr=list;
        //赋值
    }

    @Override
    public int getCount()
    {
        return mArr.size();
        //返回列表大小
    }

    @Override
    public Object getItem(int position)
    {
        return mArr.get(position);
        //返回列表中特定位置的数据项
    }

    @Override
    public long getItemId(int position)
    {
        return position;
        //返回值决定id参数
    }

    @Override
    public View getView(int position,View convertView,ViewGroup parent)
    {
        View view=inflater.inflate(R.layout.widget_wifi_conn_lv, null);
        //加载布局
        TextView ssid=(TextView)view.findViewById(R.id.wifi_conn_name);
        TextView wpe=(TextView)view.findViewById(R.id.wifi_conn_wpe);
        ImageView level=(ImageView)view.findViewById(R.id.wifi_conn_level);
        //获取实例
        ssid.setText(mArr.get(position).getSsid());
        wpe.setText("加密类型:"+mArr.get(position).getCapabilities());
        //输出显示
        int i=abs(mArr.get(position).getLevel());
        //获得信号等级
        if(i<=50)
        {
            level.setBackgroundResource(R.drawable.ic_wifi_lock_signal_4);
        }else
        if(i>50&&i<=65)
        {
            level.setBackgroundResource(R.drawable.ic_wifi_lock_signal_3);
        }else
        if(i>65&&i<=75)
        {
            level.setBackgroundResource(R.drawable.ic_wifi_lock_signal_2);
        }else
        if(i>75&&i<= 90)
        {
            level.setBackgroundResource(R.drawable.ic_wifi_lock_signal_1);
        }else
        {
            level.setBackgroundResource(R.drawable.ic_wifi_lock_signal_0);
        }
        // 用图片显示信号等级，这里默认全部带锁
        return view;
        //返回wifi列表元素
    }

    private int abs(int num)
    {
        return num*(1-((num>>>31)<<1));
        //取绝对值
    }

}
