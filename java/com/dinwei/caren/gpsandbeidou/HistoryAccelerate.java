package com.dinwei.caren.gpsandbeidou;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carendule on 2018/4/18.
 */

public class HistoryAccelerate implements Serializable {
    private static final long serialVersionUID = 7L;

    private long changetime;
    private double accelerate;

    public static List<HistoryAccelerate> historyAccelerates = new ArrayList<HistoryAccelerate>();

    public HistoryAccelerate(){
    }

    public HistoryAccelerate(long changetime,double accelerate){
        super();
        this.changetime = changetime;
        this.accelerate = accelerate;
    }

    public long getChangetime() {
        return changetime;
    }

    public void setChangetime(long changetime) {
        this.changetime = changetime;
    }

    public double getAccelerate() {
        return accelerate;
    }

    public void setAccelerate(double accelerate) {
        this.accelerate = accelerate;
    }
}
