package com.example.administrator.wifi;

public class WifiElement
{
    private String ssid;
    private String capabilities;
    private int frequency;
    private int level;
    private String bssid;
    //建立变量

    public String getSsid()
    {
        return ssid;
    }
    //返回网络名称

    public void setSsid(String ssid)
    {
        this.ssid=ssid;
    }
    //赋值网络名称

    public String getCapabilities()
    {
        return capabilities;
    }
    //返回加密方式

    public void setCapabilities(String capabilities)
    {
        this.capabilities=capabilities;
    }
    //赋值加密方式

    public int getFrequency()
    {
        return frequency;
    }
    //返回信道频率

    public void setFrequency(int frequency)
    {
        this.frequency=frequency;
    }
    //赋值信道频率

    public int getLevel()
    {
        return level;
    }
    //返回信号等级

    public void setLevel(int level)
    {
        this.level=level;
    }
    //赋值信号等级

    public String getBssid()
    {
        return bssid;
    }
    //返回接入点的Mac地址

    public void setBssid(String bssid)
    {
        this.bssid=bssid;
    }
    //赋值接入点的Mac地址

}

