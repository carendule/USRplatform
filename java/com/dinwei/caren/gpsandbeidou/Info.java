package com.dinwei.caren.gpsandbeidou;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carendule on 2018/4/9.
 */

public class Info implements Serializable {
    private static final long serialVersionUID = 1L;

    private double latitude;
    private double longitude;
    private double speed=0;
    private double accelerate=0;
    private double stress=0;
    private double temp=0;
    private String name;
    private String onlinestatus;

    public static List<Info> infos = new ArrayList<Info>();


    public Info(){
    }

    public Info(double latitude,double longitude,double speed,double accelerate,double stress,double temp,String name,String onlinestatus){
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.speed = speed;
        this.accelerate = accelerate;
        this.stress = stress;
        this.temp = temp;
        this.name = name;
        this.onlinestatus = onlinestatus;
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

    public double getSpeed(){
       return  speed;
    }

    public void setSpeed(double speed){
        this.speed = speed;
    }

    public double getAccelerate(){
        return  accelerate;
    }

    public void setAccelerate(double accelerate){
        this.accelerate = accelerate;
    }

    public double getStress(){
        return  stress;
    }

    public void setStress(double stress){
        this.stress = stress;
    }

    public double getTemp(){
        return  temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getOnlinestatus(){
        return onlinestatus;
    }

    public void  setOnlinestatus(String onlinestatus){
        this.onlinestatus = onlinestatus;
    }



}
