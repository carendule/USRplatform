package com.dinwei.caren.gpsandbeidou;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carendule on 2018/4/18.
 */

public class HistorySpeed implements Serializable {
    private static final long serialVersionUID = 6L;

    private long changetime;
    private double speed;

    public static List<HistorySpeed> historySpeeds = new ArrayList<HistorySpeed>();

    public HistorySpeed(){
    }

    public HistorySpeed(long changetime,double speed){
        super();
        this.changetime = changetime;
        this.speed = speed;
    }

    public long getChangetime() {
        return changetime;
    }

    public void setChangetime(long changetime) {
        this.changetime = changetime;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
