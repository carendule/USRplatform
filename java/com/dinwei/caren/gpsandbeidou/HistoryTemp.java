package com.dinwei.caren.gpsandbeidou;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carendule on 2018/4/18.
 */

public class HistoryTemp implements Serializable {
    private static final long serialVersionUID = 5L;

    private long changetime;
    private double temp;

    public static List<HistoryTemp> historyTemps = new ArrayList<HistoryTemp>();

    public HistoryTemp(){
    }

    public HistoryTemp(long changetime,double temp){
        super();
        this.changetime = changetime;
        this.temp = temp;
    }

    public long getChangetime() {
        return changetime;
    }

    public void setChangetime(long changetime) {
        this.changetime = changetime;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }
}
