package com.dinwei.caren.gpsandbeidou;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Carendule on 2018/4/16.
 */

public class DevListActivity extends AppCompatActivity {
    private ListView listView,temphis,speedhis,acceleratehis,stresshis;
    private ListMsgAdapter listMsgAdapter;
    private ListTempAdapter listTempAdapter;
    private ListSpeedAdapter listSpeedAdapter;
    private ListAccelerateAdapter listAccelerateAdapter;
    private ListstressAdapter liststressAdapter;
    private TextView getlistnow,nolist,timetitle;
    private LinearLayout datatime,hisdata;
    private NumberPicker year,month,day,hour,minute;
    private BootstrapButton ok,cancel,hisback;
    private int startid;
    private Calendar calendar;
    private String starttime,endtime,nowtime;
    private ConnectService myService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition explode = TransitionInflater.from(this).inflateTransition(R.transition.slide);
        getWindow().setEnterTransition(explode);
        setContentView(R.layout.devlistlayout);
        startid = (int) getIntent().getExtras().get("startid");
        calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        initView();
        setNumberPicker();
        listMsgAdapter = new ListMsgAdapter(this);
        listView.setAdapter(listMsgAdapter);
        listTempAdapter = new ListTempAdapter(this);
        temphis.setAdapter(listTempAdapter);
        listSpeedAdapter = new ListSpeedAdapter(this);
        speedhis.setAdapter(listSpeedAdapter);
        listAccelerateAdapter = new ListAccelerateAdapter(this);
        acceleratehis.setAdapter(listAccelerateAdapter);
        liststressAdapter = new ListstressAdapter(this);
        stresshis.setAdapter(liststressAdapter);
        if (DevInfo.devinfos.size()>0){
            getlistnow.setVisibility(View.GONE);
        }else{
            getlistnow.setVisibility(View.GONE);
            nolist.setVisibility(View.VISIBLE);
        }
        Intent intent = new Intent();
        intent.setClass(DevListActivity.this,ConnectService.class);
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
        Toast.makeText(this,"选择设备开始查询",Toast.LENGTH_SHORT).show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                datatime.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(DevListActivity.this, R.anim.datatimein);
                datatime.startAnimation(animation);
                shake(timetitle);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (timetitle.getText().equals("选择开始时间")){
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                starttime = String.valueOf(sdf.parse(String.valueOf(year.getValue())+"-"+String.valueOf(month.getValue())+"-"+String.valueOf(day.getValue())+" "+String.valueOf(hour.getValue())+":"+String.valueOf(minute.getValue())+":"+"00").getTime()/1000);
                                nowtime = String.valueOf(sdf.parse(String.valueOf(calendar.get(Calendar.YEAR))+"-"+String.valueOf(calendar.get(Calendar.MONTH)+1)+"-"+String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))+" "+String.valueOf(calendar.get(Calendar.HOUR_OF_DAY))+":"+String.valueOf(calendar.get(Calendar.MINUTE))+":"+"00").getTime()/1000);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Long now = new Long(nowtime);
                            Long start = new Long(starttime);
                            if (now<start){
                                Toast.makeText(DevListActivity.this,"开始时间不能超过当前时间\n请重新选择",Toast.LENGTH_SHORT).show();
                                timetitle.setText("选择开始时间");
                                shake(timetitle);
                            }else {
                                timetitle.setText("选择结束时间");
                                shake(timetitle);
                                Log.i("info", starttime);
                            }
                        }else if (timetitle.getText().equals("选择结束时间")){
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            try {
                                endtime = String.valueOf(sdf.parse(String.valueOf(year.getValue())+"-"+String.valueOf(month.getValue())+"-"+String.valueOf(day.getValue())+" "+String.valueOf(hour.getValue())+":"+String.valueOf(minute.getValue())+":"+"00").getTime()/1000);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Long start = new Long(starttime);
                            Long end = new Long(endtime);
                            Long now = new Long(nowtime);
                            if(end<start){
                                Toast.makeText(DevListActivity.this,"结束时间不能在开始时间前\n请重新选择",Toast.LENGTH_SHORT).show();
                                timetitle.setText("选择结束时间");
                                shake(timetitle);
                            }else if(now<end){
                                Toast.makeText(DevListActivity.this,"结束时间不能超过当前时间\n请重新选择",Toast.LENGTH_SHORT).show();
                                timetitle.setText("选择结束时间");
                                shake(timetitle);
                            }else{
                                timetitle.setText("正在获取,若无响应请取消后重试");
                                myService.gethistory(starttime,endtime,position,new Messenger(handler),startid);
                            }
                            Log.i("info", endtime);
                        }
                    }
                });
            }
        });
    }

    private void initView(){
        listView = (ListView)findViewById(R.id.devlist);
        temphis = (ListView)findViewById(R.id.temphis);
        acceleratehis = (ListView)findViewById(R.id.acceleratehis);
        speedhis = (ListView)findViewById(R.id.speedhis);
        stresshis = (ListView)findViewById(R.id.stresshis);
        getlistnow = (TextView)findViewById(R.id.getlistnow);
        nolist = (TextView)findViewById(R.id.nolist);
        timetitle = (TextView)findViewById(R.id.timetitle);
        datatime = (LinearLayout)findViewById(R.id.datatime);
        hisdata = (LinearLayout)findViewById(R.id.hisdata);
        year = (NumberPicker)findViewById(R.id.year);
        month = (NumberPicker)findViewById(R.id.month);
        day = (NumberPicker)findViewById(R.id.day);
        hour = (NumberPicker)findViewById(R.id.hour);
        minute = (NumberPicker)findViewById(R.id.minute);
        ok = (BootstrapButton)findViewById(R.id.ok);
        cancel = (BootstrapButton)findViewById(R.id.cancel);
        hisback = (BootstrapButton)findViewById(R.id.hisback);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(datatime.getVisibility() == View.VISIBLE){
                    Animation animation = AnimationUtils.loadAnimation(DevListActivity.this, R.anim.datatimeout);
                    datatime.startAnimation(animation);
                    datatime.setVisibility(View.GONE);
                    timetitle.setText("选择开始时间");
                }
            }
        });
        hisback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hisdata.getVisibility() == View.VISIBLE){
                    Animation animation = AnimationUtils.loadAnimation(DevListActivity.this, R.anim.hisdataout);
                    hisdata.startAnimation(animation);
                    hisdata.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setNumberPicker(){
        year.setMaxValue(2050);
        year.setMinValue(2000);
        year.setValue(calendar.get(Calendar.YEAR));
        month.setMaxValue(12);
        month.setMinValue(1);
        month.setValue(calendar.get(Calendar.MONTH)+1);
        day.setMinValue(1);
        if(month.getValue() == 1||month.getValue() == 3||month.getValue() == 5||month.getValue() == 7||month.getValue() == 8||month.getValue() == 10||month.getValue() == 12){
            day.setMaxValue(31);
        }else {
            day.setMaxValue(30);
        }
        day.setValue(calendar.get(Calendar.DAY_OF_MONTH));
        month.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                if(i1 == 1||i1 == 3||i1 == 5||i1 == 7||i1 == 8||i1 == 10||i1 == 12){
                    day.setMaxValue(31);
                }else {
                    day.setMaxValue(30);
                }
            }
        });
        hour.setMaxValue(23);
        hour.setMinValue(0);
        if (calendar.get(calendar.AM_PM)==0) {
            hour.setValue(calendar.get(Calendar.HOUR));
        }else{
            hour.setValue(calendar.get(Calendar.HOUR)+12);
        }
        minute.setMaxValue(59);
        minute.setMinValue(0);
        minute.setValue(calendar.get(Calendar.MINUTE));
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myService = ((ConnectService.MyBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            myService = null;
        }
    };

    private void shake(View view){
        TranslateAnimation animation = new TranslateAnimation(0, 0, 60, 0);
        animation.setInterpolator(new OvershootInterpolator());
        animation.setDuration(100);
        animation.setRepeatCount(6);
        animation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(animation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    public Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 9:Toast.makeText(DevListActivity.this,"当前查询无历史数据\n请重新查询",Toast.LENGTH_LONG).show();
                           timetitle.setText("选择开始时间");shake(timetitle);break;
                case 10:Intent intent = new Intent();
                        intent.putExtra("result",1);
                        setResult(1,intent);
                        finish();break;
                case 11:Toast.makeText(DevListActivity.this,"查询超时",Toast.LENGTH_LONG).show();
                        timetitle.setText("选择开始时间");shake(timetitle);break;
                case 12:Animation animation = AnimationUtils.loadAnimation(DevListActivity.this, R.anim.hisdatain);
                        hisdata.setVisibility(View.VISIBLE);
                        hisdata.startAnimation(animation);
                        datatime.setVisibility(View.GONE);
                        timetitle.setText("选择开始时间");break;
            }
        }
    };
}
