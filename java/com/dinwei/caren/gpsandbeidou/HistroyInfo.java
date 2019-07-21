package com.dinwei.caren.gpsandbeidou;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carendule on 2018/4/16.
 */

public class HistroyInfo implements Serializable {
    private static final long serialVersionUID = 4L;

    private double latitude;
    private double longitude;

    public static List<HistroyInfo> histroyInfos = new ArrayList<HistroyInfo>();

    public HistroyInfo(){
    }

    public HistroyInfo(double latitude,double longitude){
        super();
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
