package com.dinwei.caren.gpsandbeidou;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Carendule on 2018/4/12.
 */

public class ConnectService extends Service{

    private MyBinder mBinder = new MyBinder();
    public String response;
    public String devinfo;
    public String info;
    public String histroyinfo;
    private JSONObject user;
    private Messenger messenger;
    private  Netcheck netcheck = new Netcheck();
    private Double latitude = 38.0161;
    private Double longtitude = 112.449;
    private Double speed;
    private Double accelerate;
    private Double stress;
    private Double temp;

    public void onCreate(){
        super.onCreate();
        user = new JSONObject();
        try {
            user.put("account","夜火易星辰");
            user.put("password","8801DDB750769BDAB9D7CCFE2F89E8A0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId){
            messenger = (Messenger) intent.getExtras().get("messenger");
        if(netcheck.checkNetwork(this)== true) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Message startconnect = new Message();
                    startconnect.what = 1;
                    try {
                        messenger.send(startconnect);
                        response = Https.post("https://cloudapi.usr.cn/usrCloud/user/login", user.toString());
                    } catch (RemoteException e) {
                        Message message = new Message();
                        message.what = 6;
                        try {
                            messenger.send(message);
                        } catch (RemoteException e1) {
                            e1.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    com.alibaba.fastjson.JSONObject json = JSON.parseObject(response);
                    com.alibaba.fastjson.JSONObject datajson = json.getJSONObject("data");
                    Message connectresult = new Message();
                    connectresult.what = 2;
                    connectresult.obj = json.getString("info");
                    ;
                    try {
                        messenger.send(connectresult);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    if ((json.getString("info")).equals("ok")) {
                        if (Token.tokens.size() > 1) {
                            Token.tokens.remove(0);
                        } else {
                            Token.tokens.add(new Token(datajson.getString("token"), datajson.getString("uid")));
                        }
                        JSONObject devidrequest = new JSONObject();
                        try {
                            devidrequest.put("token", datajson.getString("token"));
                            devinfo = Https.post("https://cloudapi.usr.cn/usrCloud/dev/getDevs", devidrequest.toString());
                            Message getdevinfo = new Message();
                            connectresult.what = 3;
                            messenger.send(getdevinfo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        com.alibaba.fastjson.JSONObject devinfojson = JSON.parseObject(devinfo);
                        Message getdevinforesult = new Message();
                        getdevinforesult.what = 4;
                        getdevinforesult.obj = devinfojson.getString("info");
                        com.alibaba.fastjson.JSONObject devinfodata = devinfojson.getJSONObject("data");
                        JSONArray devinfolist = devinfodata.getJSONArray("dev");
                        for (int i = 0; i < devinfolist.size(); i++) {
                            com.alibaba.fastjson.JSONObject onedev = devinfolist.getJSONObject(i);
                            DevInfo.devinfos.add(new DevInfo(onedev.getString("devid"),onedev.getString("name"),onedev.getString("onlineStatus")));//将所有设备id写入Devinfo数组中
                            Log.i("info", "已添加：" + onedev.getString("devid")+onedev.getString("name")+onedev.getString("onlineStatus"));
                        }
                        try {
                            messenger.send(getdevinforesult);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Message loginfail = new Message();
                        loginfail.what = 5;
                        try {
                            messenger.send(loginfail);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();
        }//如果没有联网将不执行
        return super.onStartCommand(intent, flags, startId);
    }

    public void refreshdevid(){
        if(netcheck.checkNetwork(this) && DevInfo.devinfos.size()>0) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject devidrequest = new JSONObject();
                    try {
                        devidrequest.put("token", Token.tokens.get(0).getToken());
                        devinfo = Https.post("https://cloudapi.usr.cn/usrCloud/dev/getDevs", devidrequest.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        Message message = new Message();
                        message.what = 6;
                        try {
                            messenger.send(message);
                        } catch (RemoteException e1) {
                            e1.printStackTrace();
                        }
                    }
                    com.alibaba.fastjson.JSONObject devinfojson = JSON.parseObject(devinfo);
                    com.alibaba.fastjson.JSONObject devinfodata = devinfojson.getJSONObject("data");
                    JSONArray devinfolist = devinfodata.getJSONArray("dev");
                    for (int i = 0; i < DevInfo.devinfos.size(); ) {
                        DevInfo.devinfos.remove(i);
                        Log.i("info", "已删除一个devid");
                    }
                    for (int i = 0; i < devinfolist.size(); i++) {
                        com.alibaba.fastjson.JSONObject onedev = devinfolist.getJSONObject(i);
                        DevInfo.devinfos.add(new DevInfo(onedev.getString("devid"),onedev.getString("name"),onedev.getString("onlineStatus")));//将所有设备id写入Devinfo数组中
                        Log.i("info", "已重新添加：" + onedev.getString("devid")+onedev.getString("name")+onedev.getString("onlineStatus"));
                    }
                    for (int i = 0; i < DevInfo.devinfos.size(); i++) {
                        Log.i("info", "devid列表中有" + DevInfo.devinfos.get(i).getDevid().toString());
                    }
                }
            }).start();
        }else{
            Message message = new Message();
            message.what = 8;
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void getinfo(){
        if(netcheck.checkNetwork(this) && DevInfo.devinfos.size()>0){
            final com.alibaba.fastjson.JSONObject devinfo = new com.alibaba.fastjson.JSONObject();
            final JSONArray devDataIds = new JSONArray();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (Info.infos.size()>1){
                        for (int i = 0; i < Info.infos.size(); ) {
                            Info.infos.remove(i);
                         }
                    }
                    for(int i=0;i<DevInfo.devinfos.size();i++){
                        for(int k=1;k<=6;k++){
                            Map<String,Object> map = new HashMap<String, Object>();
                            map.put("dataId","14362");
                            map.put("slaveIndex",String.valueOf(k));
                            map.put("devid",DevInfo.devinfos.get(i).getDevid().toString());
                            devDataIds.add(map);
                        }
                        devinfo.put("token",Token.tokens.get(0).getToken().toString());
                        devinfo.put("devDataIds",devDataIds);
                        Log.i("info", devinfo.toString());
                        try {
                            info = Https.post("https://cloudapi.usr.cn/usrCloud/datadic/getLastData",devinfo.toString());
                        } catch (IOException e) {
                            Message message = new Message();
                            message.what = 6;
                            try {
                                messenger.send(message);
                            } catch (RemoteException e1) {
                                e1.printStackTrace();
                            }
                        }
                        com.alibaba.fastjson.JSONObject infojson = JSON.parseObject(info);
                        JSONArray infoarray = infojson.getJSONArray("data");
                        for (int j=0;j<infoarray.size();j++){
                            com.alibaba.fastjson.JSONObject onedev = infoarray.getJSONObject(j);
                            if (onedev.getString("slaveIndex").equals("1")){
                                latitude = Double.valueOf(onedev.getString("value"));
                                Log.i("info", "纬度为："+onedev.getString("value"));
                            }else if (onedev.getString("slaveIndex").equals("2")){
                                longtitude = Double.valueOf(onedev.getString("value"));
                                Log.i("info", "经度为："+onedev.getString("value"));
                            }else if (onedev.getString("slaveIndex").equals("3")){
                                temp = Double.valueOf(onedev.getString("value"));
                                Log.i("info", "温度为："+onedev.getString("value"));
                            }else if (onedev.getString("slaveIndex").equals("4")){
                                speed = Double.valueOf(onedev.getString("value"));
                                Log.i("info", "速度为："+onedev.getString("value"));
                            }else if (onedev.getString("slaveIndex").equals("5")){
                                accelerate = Double.valueOf(onedev.getString("value"));
                                Log.i("info", "加速度为："+onedev.getString("value"));
                            }else if (onedev.getString("slaveIndex").equals("6")){
                                stress = Double.valueOf(onedev.getString("value"));
                                Log.i("info", "应力为："+onedev.getString("value"));
                            }
                        }
                        Info.infos.add(new Info(latitude,longtitude,speed,accelerate,stress,temp,DevInfo.devinfos.get(i).getName(),DevInfo.devinfos.get(i).getIsonline()));
                    }
                    Message message = new Message();
                    message.what = 7;
                    try {
                        messenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        else{
            Message message = new Message();
            message.what = 8;
            try {
                messenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void gethistory(String starttime, String stoptime, int postion, final Messenger hismessenger, final int startid){
        JSONArray devDataPointArray = new JSONArray();
        final com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        for(int i=1;i<=6;i++){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("dataId","14362");
            map.put("slaveIndex",String.valueOf(i));
            map.put("devid",DevInfo.devinfos.get(postion).getDevid().toString());
            devDataPointArray.add(map);
        }
        jsonObject.put("token",Token.tokens.get(0).getToken().toString());
        jsonObject.put("stopTime",stoptime);
        jsonObject.put("startTime",starttime);
        jsonObject.put("devDataPointArray",devDataPointArray);
        Log.i("info", jsonObject.toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(HistroyInfo.histroyInfos.size()>0){
                    for (int i = 0; i < HistroyInfo.histroyInfos.size(); ) {
                        HistroyInfo.histroyInfos.remove(i);
                    }
                }else {
                    try {
                        histroyinfo = Https.post("https://cloudapi.usr.cn/usrCloud/datadic/getDataHisByTimePeriod", jsonObject.toString());
                    } catch (IOException e) {
                        Message message = new Message();
                        message.what = 11;
                        Log.i("接收出错", histroyinfo);
                        try {
                            hismessenger.send(message);
                        } catch (RemoteException e1) {
                            e1.printStackTrace();
                        }
                        e.printStackTrace();
                    }
                    com.alibaba.fastjson.JSONObject historyjson = JSON.parseObject(histroyinfo);
                    JSONArray historyarray = historyjson.getJSONArray("data");
                    JSONArray latitude = historyarray.getJSONArray(0);
                    JSONArray longtitude = historyarray.getJSONArray(1);
                    JSONArray histemp = historyarray.getJSONArray(2);
                    JSONArray hisspeed = historyarray.getJSONArray(3);
                    JSONArray hisaccelerate = historyarray.getJSONArray(4);
                    JSONArray hisstress = historyarray.getJSONArray(5);
                    Log.i("latitude", latitude.toString());
                    Log.i("longtitude", longtitude.toString());
                    Log.i("temp", histemp.toString());
                    Log.i("speed", hisspeed.toString());
                    Log.i("accelerate", hisaccelerate.toString());
                    Log.i("stress", hisstress.toString());
                    if ((latitude.isEmpty()|| longtitude.isEmpty())&&(histemp.isEmpty() && hisspeed.isEmpty() && hisaccelerate.isEmpty()&& hisstress.isEmpty())) {
                        Log.i("判断",String.valueOf((latitude.isEmpty()|| longtitude.isEmpty())&& (histemp.isEmpty() && hisspeed.isEmpty() && hisaccelerate.isEmpty()&& hisstress.isEmpty())));
                        Message message = new Message();
                        message.what = 9;
                        try {
                            hismessenger.send(message);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (startid == 0) {
                            for (int i = 0; i < latitude.size(); i++) {
                                com.alibaba.fastjson.JSONObject latobject = latitude.getJSONObject(i);
                                for (int j = 0; j < longtitude.size(); j++) {
                                    com.alibaba.fastjson.JSONObject longobject = longtitude.getJSONObject(j);
                                    Long lattime = new Long(latobject.getString("generateTime"));
                                    Long longtime = new Long(longobject.getString("generateTime"));
                                    if (lattime - longtime < 3 && lattime - longtime > -3) {
                                        Double lat, longt;
                                        lat = Double.valueOf(latobject.getString("value"));
                                        longt = Double.valueOf(longobject.getString("value"));
                                        HistroyInfo.histroyInfos.add(new HistroyInfo(lat, longt));
                                        Log.i("添加历史数据：", latobject.getString("value") + longobject.getString("value"));
                                        Log.i("相差时间", String.valueOf(lattime - longtime));
                                        Log.i("相差时间反减", String.valueOf(longtime - lattime));
                                        Log.i("lattime", String.valueOf(lattime));
                                        Log.i("longtime", String.valueOf(longtime));
                                    }
                                }
                            }
                            Message message = new Message();
                            message.what = 10;
                            try {
                                hismessenger.send(message);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        } else {
                            if(HistoryTemp.historyTemps.size()>0){
                                for (int i = 0; i < HistoryTemp.historyTemps.size(); ) {
                                    HistoryTemp.historyTemps.remove(i);
                                }
                            }if(HistorySpeed.historySpeeds.size()>0){
                                for (int i = 0; i < HistorySpeed.historySpeeds.size(); ) {
                                    HistorySpeed.historySpeeds.remove(i);
                                }
                            }if(HistoryAccelerate.historyAccelerates.size()>0){
                                for (int i = 0; i < HistoryAccelerate.historyAccelerates.size(); ) {
                                    HistoryAccelerate.historyAccelerates.remove(i);
                                }
                            }if(HistoryStress.historyStresses.size()>0){
                                for (int i = 0; i < HistoryStress.historyStresses.size(); ) {
                                    HistoryStress.historyStresses.remove(i);
                                }
                            }
                            for (int t = 0; t < histemp.size(); t++) {
                                long changetime;
                                double temp;
                                com.alibaba.fastjson.JSONObject jsonObject = histemp.getJSONObject(t);
                                changetime = new Long(jsonObject.getString("generateTime"));
                                temp  = Double.valueOf(jsonObject.getString("value"));
                                HistoryTemp.historyTemps.add(new HistoryTemp(changetime,temp));
                            }
                            for (int s = 0;s <hisspeed.size();s++){
                                long changetime;
                                double speed;
                                com.alibaba.fastjson.JSONObject jsonObject = hisspeed.getJSONObject(s);
                                changetime = new Long(jsonObject.getString("generateTime"));
                                speed  = Double.valueOf(jsonObject.getString("value"));
                                HistorySpeed.historySpeeds.add(new HistorySpeed(changetime,speed));
                            }
                            for (int a = 0;a <hisaccelerate.size();a++){
                                long changetime;
                                double accelerate;
                                com.alibaba.fastjson.JSONObject jsonObject = hisaccelerate.getJSONObject(a);
                                changetime = new Long(jsonObject.getString("generateTime"));
                                accelerate  = Double.valueOf(jsonObject.getString("value"));
                                HistoryAccelerate.historyAccelerates.add(new HistoryAccelerate(changetime,accelerate));
                            }
                            for (int st = 0;st <hisstress.size();st++){
                                long changetime;
                                double stress;
                                com.alibaba.fastjson.JSONObject jsonObject = hisspeed.getJSONObject(st);
                                changetime = new Long(jsonObject.getString("generateTime"));
                                stress  = Double.valueOf(jsonObject.getString("value"));
                                HistoryStress.historyStresses.add(new HistoryStress(changetime,stress));
                            }
                            Message message = new Message();
                            message.what = 12;
                            try {
                                hismessenger.send(message);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        public ConnectService getService() {
            return ConnectService.this;
        }
    }
}
