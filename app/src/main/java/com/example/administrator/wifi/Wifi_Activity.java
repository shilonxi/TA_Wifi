package com.example.administrator.wifi;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Wifi_Activity extends AppCompatActivity implements OnItemClickListener,OnClickListener
{
    private TextView mTvShowConn;
    private ListView mLvWifiConInfo;
    private Button mBtnWifiSwitch;
    private Button mBtnWifiScan;
    private Button mBtnWifiCancle;
    private List<ScanResult> mScanResultList;
    private ScanResult mScanResult;
    private WifiAdmin mWifiAdmin;
    private WifiConnListAdapter mWifiConnAdapter;
    private ArrayList<WifiElement> mWifiElementList=new ArrayList<WifiElement>();
    private boolean isOpen=false;
    //建立变量

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_layout);
        mWifiAdmin=new WifiAdmin(Wifi_Activity.this);
        initView();
        //初始化
    }

    private void initView()
    {
        mTvShowConn=(TextView)this.findViewById(R.id.wifi_show_conn);
        mLvWifiConInfo=(ListView)this.findViewById(R.id.wifi_conn_lv);
        mBtnWifiSwitch=(Button)this.findViewById(R.id.wifi_conn_switch_btn);
        mBtnWifiScan=(Button)this.findViewById(R.id.wifi_conn_scan_btn);
        mBtnWifiCancle=(Button)this.findViewById(R.id.wifi_conn_cancle_btn);
        //获取实例
        if(mWifiAdmin.getWifiState()==WifiManager.WIFI_STATE_DISABLED)
        {
            mBtnWifiSwitch.setText("打开wifi");
        }else
        {
            mBtnWifiSwitch.setText("关闭wifi");
            isOpen=true;
        }
        //根据状态改变开关按钮文字
        mTvShowConn.setText("已连接："+initShowConn());
        //输出显示
        mBtnWifiCancle.setOnClickListener(this);
        mBtnWifiSwitch.setOnClickListener(this);
        mBtnWifiScan.setOnClickListener(this);
        mLvWifiConInfo.setOnItemClickListener(this);
       //建立点击监听
    }

    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.wifi_conn_cancle_btn:
                finish();
                break;
            //退出
            case R.id.wifi_conn_scan_btn:
                mWifiConnAdapter=new WifiConnListAdapter(getApplicationContext(),getAllNetWorkList());
                mLvWifiConInfo.setAdapter(mWifiConnAdapter);
                break;
            //扫描
            case R.id.wifi_conn_switch_btn:
                if(isOpen)
                {
                    Toast.makeText(getApplicationContext(),"正在关闭wifi",Toast.LENGTH_SHORT).show();
                    if(mWifiAdmin.closeWifi())
                    {
                        Toast.makeText(getApplicationContext(),"wifi关闭成功",Toast.LENGTH_SHORT).show();
                        mBtnWifiSwitch.setText("打开wifi");
                        isOpen=false;
                    }else
                    {
                        Toast.makeText(getApplicationContext(),"wifi关闭失败",Toast.LENGTH_SHORT).show();
                    }
                }else
                {
                    Toast.makeText(getApplicationContext(),"正在打开wifi",Toast.LENGTH_SHORT).show();
                    if(mWifiAdmin.OpenWifi())
                    {
                        Toast.makeText(getApplicationContext(),"wifi打开成功",Toast.LENGTH_SHORT).show();
                        mBtnWifiSwitch.setText("关闭wifi");
                        isOpen=true;
                    }else
                    {
                        Toast.makeText(getApplicationContext(),"wifi打开失败",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            //开关
            default:
                break;
        }
    }
    //按钮点击响应

    private String initShowConn()
    {
        WifiManager wifiManager=(WifiManager)getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo=wifiManager.getConnectionInfo();
        //获取实例
        String s=wifiInfo.getSSID()+'\n'+"IP地址："+mWifiAdmin.ipIntToString(wifiInfo.getIpAddress())+'\n'+"Mac地址："+wifiInfo.getMacAddress();
        return s;
    }
    //具体输出内容

    @Override
    public void onItemClick(AdapterView<?> arg0,View view,int position,long id)
    {
        final String ssid=mWifiElementList.get(position).getSsid();
        Builder dialog=new AlertDialog.Builder(Wifi_Activity.this);
        final WifiConfiguration wifiConfiguration=mWifiAdmin.IsExsits(ssid);
        dialog.setTitle("是否连接?");
        dialog.setPositiveButton("确定",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog,int which)
            {
                if(null==wifiConfiguration)
                {
                    setMessage(ssid);
                }else
                {
                    mWifiAdmin.Connect(wifiConfiguration);
                }
            }
        }).setNegativeButton("取消",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog,int which)
            {
            }
        }).setNeutralButton("移除",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog,int which)
            {
                if(null!=wifiConfiguration)
                {
                    int id=wifiConfiguration.networkId;
                    mWifiAdmin.removeNetworkLink(id);
                }
            }
        }).create();
        dialog.show();
    }
    //列表元素点击响应

    private void setMessage(final String ssid)
    {
        Builder dialog=new AlertDialog.Builder(Wifi_Activity.this);
        LayoutInflater inflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout lay=(LinearLayout)inflater.inflate(R.layout.widget_wifi_pwd,null);
        //加载布局
        dialog.setView(lay);
        final EditText pwd=(EditText)lay.findViewById(R.id.wifi_pwd_edit);
        dialog.setTitle(ssid);
        dialog.setPositiveButton("确定",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog,int which)
            {
                String pwdStr=pwd.getText().toString();
                boolean flag=mWifiAdmin.Connect(ssid,pwdStr,WifiAdmin.WifiCipherType.WIFICIPHER_WPA);
                if(flag)
                {
                    Toast.makeText(getApplicationContext(),"正在连接，请稍后",Toast.LENGTH_SHORT).show();
                }else
                {
                    showLog("连接错误");
                }
            }
        }).setNegativeButton("取消",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        }).create();
        dialog.show();
    }
    //密码块

    @SuppressLint("NewApi")
    private ArrayList<WifiElement> getAllNetWorkList()
    {
        mWifiElementList.clear();
        //每次点击扫描之前清空上一次的扫描结果
        if(mWifiAdmin.isWifiEanbled())
        {
            requestMultiplePermissions();
            //6.0后要运行时声明权限
            mWifiAdmin.startScan();
            //开始扫描网络
            Toast.makeText(getApplicationContext(),"开始扫描",Toast.LENGTH_SHORT).show();
        }else
        {
            Toast.makeText(getApplicationContext(),"请先打开wifi",Toast.LENGTH_SHORT).show();
        }
        mScanResultList=mWifiAdmin.getWifiList();
        WifiElement element;
        if(mScanResultList!=null)
        {
            for(int i=0;i<mScanResultList.size();i++)
            {
                mScanResult=mScanResultList.get(i);
                element=new WifiElement();
                element.setSsid(mScanResult.SSID);
                element.setBssid(mScanResult.BSSID);
                element.setCapabilities(mScanResult.capabilities);
                element.setFrequency(mScanResult.frequency);
                element.setLevel(mScanResult.level);
                mWifiElementList.add(element);
                // 得到扫描结果
            }
        }
        return mWifiElementList;
    }
    //扫描

    private void showLog(final String msg)
    {
        new AsyncTask<Void,Void,String>()
        {
            @Override
            protected String doInBackground(Void... params)
            {
                return null;
            }

            @Override
            protected void onPostExecute(String result)
            {
                super.onPostExecute(result);
                Dialog dialog=new AlertDialog.Builder(Wifi_Activity.this).setTitle("提示").setMessage(msg).setNegativeButton("确定",new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog,int which)
                    {
                    }
                }).create();
                dialog.show();
            }
        }.execute();
    }
    //提示信息对话框

    @Override
    protected void onResume()
    {
        super.onResume();
        IntentFilter ins=new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(netConnReceiver,ins);
    }

    private BroadcastReceiver netConnReceiver=new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context,Intent intent)
        {
            if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction()))
            {
                if(checknet())
                {
                    mTvShowConn.setText("已连接："+initShowConn());
                }else
                {
                    mTvShowConn.setText("正在尝试连接："+initShowConn());
                }
            }
        }
    };

    private NetworkInfo networkInfo;
    //获取网络

    private boolean checknet()
    {
        ConnectivityManager connManager=(ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
        // 获取代表联网状态的NetWorkInfo对象
        networkInfo=connManager.getActiveNetworkInfo();
        if(null!=networkInfo)
        {
            return networkInfo.isAvailable();
        }
        return false;
    }
    //监测网络连接

    private void requestMultiplePermissions()
    {
        String[] permissions={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(Wifi_Activity.this,permissions,1);
    }
    //运行时声明权限

}
