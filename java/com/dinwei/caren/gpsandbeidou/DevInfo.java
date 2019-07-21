package com.dinwei.caren.gpsandbeidou;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carendule on 2018/4/14.
 */

public class DevInfo implements Serializable {
    private static final long serialVersionUID = 3L;

    private String devid;
    private String name;
    private String isonline;

    public static List<DevInfo> devinfos = new ArrayList<DevInfo>();

    public DevInfo(){
    }

    public DevInfo(String devid,String name,String isonline){
        super();
        this.devid=devid;
        this.name=name;
        this.isonline=isonline;

    }

    public String getDevid(){
        return devid;
    }
    public void setDevid(String devid){
        this.devid=devid;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
    public String getIsonline(){
        return isonline;
    }
    public void setIsonline(String isonline){
        this.isonline=isonline;
    }

}
