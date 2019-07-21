package com.dinwei.caren.gpsandbeidou;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carendule on 2018/4/18.
 */

public class HistoryStress implements Serializable {
    private static final long serialVersionUID = 8L;

    private long changetime;
    private double stress;

    public static List<HistoryStress> historyStresses = new ArrayList<HistoryStress>();

    public HistoryStress(){
    }

    public HistoryStress(long changetime,double stress){
        super();
        this.changetime = changetime;
        this.stress = stress;
    }

    public long getChangetime() {
        return changetime;
    }

    public void setChangetime(long changetime) {
        this.changetime = changetime;
    }

    public double getStress() {
        return stress;
    }

    public void setStress(double stress) {
        this.stress = stress;
    }
}
