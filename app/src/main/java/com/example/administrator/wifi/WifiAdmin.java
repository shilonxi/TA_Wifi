package com.example.administrator.wifi;

import java.net.Inet4Address;
import java.util.List;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

public class WifiAdmin
{
    private WifiManager wifiManager;
    //声明管理对象
    private WifiInfo wifiInfo;
    //声明Wifi信息
    private List<ScanResult> scanResultList;
    //声明扫描出来的网络连接列表
    private List<WifiConfiguration> wifiConfigList;
    //声明网络配置列表
    private WifiLock wifiLock;
    //声明加密类型

    public enum WifiCipherType
    {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }
    //定义几种加密方式，一种是WEP，一种是WPA，还有没有密码的情况

    public boolean Connect(String SSID,String Password,WifiCipherType Type)
    {
        if(!this.OpenWifi())
        {
            return false;
        }
        //不开启Wi-Fi网络，则不向下进行
        while(wifiManager.getWifiState()==WifiManager.WIFI_STATE_ENABLING)
        {
            try
            {
                Thread.currentThread();
                Thread.sleep(100);
                // 为了避免程序一直while循环，让其睡个100毫秒再检测
            }catch(InterruptedException ie) {}
        }
        // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        WifiConfiguration wifiConfig=this.CreateWifiInfo(SSID,Password,Type);
        //输入密码，根据密码创建一个配置信息类
        int netID=wifiManager.addNetwork(wifiConfig);
        //调用addNetwork把配置信息加入WifiManager（这里返回networkId ）
        boolean bRet=wifiManager.enableNetwork(netID,true);
        //加入后默认是DISABLED的，调用enableNetwork去启动连接
        wifiManager.saveConfiguration();
        //保存
        return bRet;
    }
    //连接未配置的wifi

    public boolean Connect(WifiConfiguration wf)
    {
        if(!this.OpenWifi())
        {
            return false;
        }
        //不开启Wi-Fi网络，则不向下进行
        while(wifiManager.getWifiState()==WifiManager.WIFI_STATE_ENABLING)
        {
            try
            {
                Thread.currentThread();
                Thread.sleep(100);
                // 为了避免程序一直while循环，让其睡个100毫秒再检测
            }catch(InterruptedException ie) {}
        }
        // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        boolean bRet=wifiManager.enableNetwork(wf.networkId,true);
        //默认是DISABLED的，调用enableNetwork去启动连接
        wifiManager.saveConfiguration();
        //保存
        return bRet;
    }
    //连接已配置过wifi

    public boolean OpenWifi()
    {
        boolean bRet=true;
        if(!wifiManager.isWifiEnabled())
        {
            bRet=wifiManager.setWifiEnabled(true);
        }
        return bRet;
    }
    //开启Wi-Fi网络

    public WifiConfiguration IsExsits(String SSID)
    {
        List<WifiConfiguration> existingConfigs=wifiManager.getConfiguredNetworks();
        for(WifiConfiguration existingConfig:existingConfigs)
        {
            if(existingConfig.SSID.equals("\""+SSID +"\""))
            {
                return existingConfig;
            }
        }
        return null;
    }
    // 查看以前是否也配置过这个网络

    private WifiConfiguration CreateWifiInfo(String SSID,String Password,WifiCipherType Type)
    {
        WifiConfiguration wc=new WifiConfiguration();
        wc.allowedAuthAlgorithms.clear();
        wc.allowedGroupCiphers.clear();
        wc.allowedKeyManagement.clear();
        wc.allowedPairwiseCiphers.clear();
        wc.allowedProtocols.clear();
        wc.SSID="\""+SSID+"\"";
        if(Type==WifiCipherType.WIFICIPHER_NOPASS)
        {
            wc.wepKeys[0]="";
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wc.wepTxKeyIndex=0;
        }else
        if(Type==WifiCipherType.WIFICIPHER_WEP)
        {
            wc.wepKeys[0]="\""+Password+"\"";
            wc.hiddenSSID=true;
            wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wc.wepTxKeyIndex=0;
        }else
        if(Type==WifiCipherType.WIFICIPHER_WPA)
        {
            wc.preSharedKey="\""+Password+"\"";
            wc.hiddenSSID=true;
            wc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        }else
        {
            return null;
        }
        return wc;
    }
    //链接

    public WifiAdmin(Context context)
    {
        this.wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        //获取WifiManager实例
        this.wifiInfo=wifiManager.getConnectionInfo();
        //获取WifiInfo实例
    }

    public boolean getWifiStatus()
    {
        return wifiManager.isWifiEnabled();
    }
    //判断WIFI设备是否打开

    public int getWifiState()
    {
        return wifiManager.getWifiState();
    }
    //获取当前WIFI设备的状态

    public boolean closeWifi()
    {
        if(!wifiManager.isWifiEnabled())
        {
            return true;
        }else
        {
            return wifiManager.setWifiEnabled(false);
        }
    }
    //关闭Wi-Fi网络

    public void lockWifi()
    {
        wifiLock.acquire();
    }
    //锁定wifi

    public void unLockWifi()
    {
        if(!wifiLock.isHeld())
        {
            wifiLock.release();
            // 释放资源
        }
    }
    //锁定/解锁wifi

    public void createWifiLock()
    {
        wifiLock=wifiManager.createWifiLock("flyfly");
        // 创建一个锁的标志
    }

    public void startScan()
    {
        wifiManager.startScan();
        scanResultList=wifiManager.getScanResults();
        // 扫描返回结果列表，在6.0后要声明权限
        wifiConfigList=wifiManager.getConfiguredNetworks();
        // 扫描配置列表
    }
    //扫描网络

    public List<ScanResult> getWifiList()
    {
        return scanResultList;
    }
    //扫描到的AP集合

    public List<WifiConfiguration> getWifiConfigList()
    {
        return wifiConfigList;
    }
    //已配置的AP集合

    public StringBuilder lookUpscan()
    {
        StringBuilder scanBuilder=new StringBuilder();
        for(int i=0;i<scanResultList.size();i++)
        {
            scanBuilder.append("编号："+(i+1));
            scanBuilder.append(scanResultList.get(i).toString());
            // 所有信息
            scanBuilder.append("\n");
        }
        return scanBuilder;
    }
    //获取扫描列表

    public int getLevel(int NetId)
    {
        return scanResultList.get(NetId).level;
    }
    //获取指定信号的强度

    public String getMac()
    {
        return (wifiInfo==null)?"":wifiInfo.getMacAddress();
    }
    //获取本机Mac地址

    public String getBSSID()
    {
        return (wifiInfo==null)?null:wifiInfo.getBSSID();
    }
    //获取本机链接BSSID地址

    public String getSSID()
    {
        return (wifiInfo==null)?null:wifiInfo.getSSID();
    }
    //获取本机SSID

    public int getCurrentNetId()
    {
        return (wifiInfo==null)?null:wifiInfo.getNetworkId();
    }
    //返回当前连接的网络的ID

    public String getwifiInfo()
    {
        return (wifiInfo==null)?null:wifiInfo.toString();
    }
    //返回所有信息

    public int getIP()
    {
        return (wifiInfo==null)?null:wifiInfo.getIpAddress();
    }
    //获取IP地址

    public boolean addNetWordLink(WifiConfiguration config)
    {
        int NetId=wifiManager.addNetwork(config);
        return wifiManager.enableNetwork(NetId,true);
    }
    //添加一个连接

    public boolean disableNetWordLick(int NetId)
    {
        wifiManager.disableNetwork(NetId);
        return wifiManager.disconnect();
    }
    //禁用一个链接

    public boolean removeNetworkLink(int NetId)
    {
        return wifiManager.removeNetwork(NetId);
    }
    //移除一个链接

    public void hiddenSSID(int NetId)
    {
        wifiConfigList.get(NetId).hiddenSSID=true;
    }
    //不显示SSID

    public void displaySSID(int NetId)
    {
        wifiConfigList.get(NetId).hiddenSSID=false;
    }
    //显示SSID

    public String ipIntToString(int ip)
    {
        try
        {
            byte[] bytes=new byte[4];
            bytes[0]=(byte)(0xff&ip);
            bytes[1]=(byte)((0xff00&ip)>>8);
            bytes[2]=(byte)((0xff0000&ip)>>16);
            bytes[3]=(byte)((0xff000000&ip)>>24);
            return Inet4Address.getByAddress(bytes).getHostAddress();
        }catch (Exception e)
        {
            return "";
        }
    }
    // 转换IP

    public boolean isWifiEanbled()
    {
        return wifiManager.getWifiState()==WifiManager.WIFI_STATE_ENABLED?true:false;
    }
}
