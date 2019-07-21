package com.dinwei.caren.gpsandbeidou;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.floatingnavigationview.FloatingNavigationView;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.githang.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public MapView mainmapview = null;
    public LinearLayout markerinfo;
    private Netcheck netcheck = new Netcheck();
    public FloatingActionButton fab = null;
    public BaiduMap maptype = null;
    private ConnectService myService;
    private TextView wait;
    private FloatingNavigationView mFloatingNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.app_bar_main);
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.green));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mFloatingNavigationView = (FloatingNavigationView) findViewById(R.id.floating_navigation_view);
        mFloatingNavigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFloatingNavigationView.open();
            }
        });
        mFloatingNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Snackbar.make((View) mFloatingNavigationView.getParent(), item.getTitle() + " Selected!", Snackbar.LENGTH_SHORT).show();
                mFloatingNavigationView.close();
                int id = item.getItemId();
                if (id == R.id.OfflineMap) {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this,OfflineDemo.class);
                    startActivity(intent);
                }
                else if(id == R.id.ChangeMap){
                    if(maptype.getMapType()== BaiduMap.MAP_TYPE_NORMAL ) {
                        maptype.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    }
                    else{
                        maptype.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    }
                }
                else if(id == R.id.CarMark){
                    myService.getinfo();
                }
                else if(id == R.id.Location){
                    int startid = 0;
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this,DevListActivity.class);
                    intent.putExtra("startid",startid);
                    if(netcheck.checkNetwork(MainActivity.this)) {
                        startActivityForResult(intent,1, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                    }else{
                        Toast.makeText(MainActivity.this,"网络未连接\n该功能暂时无法使用",Toast.LENGTH_LONG).show();
                    }
                }
                else if(id == R.id.CleanMap){
                    maptype.clear();
                    if(markerinfo.getVisibility()==View.VISIBLE){
                        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.tran_out);
                        markerinfo.startAnimation(animation);
                        markerinfo.setVisibility(View.GONE);
                    }
                }
                else if(id == R.id.HistoryData){
                    int startid = 1;
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this,DevListActivity.class);
                    intent.putExtra("startid",startid);
                    if(netcheck.checkNetwork(MainActivity.this)) {
                        startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
                    }else{
                        Toast.makeText(MainActivity.this,"网络未连接\n该功能暂时无法使用",Toast.LENGTH_LONG).show();
                    }
                }
                return true;
            }
        });

        markerinfo = (LinearLayout)findViewById(R.id.markpopup);
        mainmapview = (MapView)findViewById(R.id.MainMapView);
        wait = (TextView)findViewById(R.id.wait);
        mainmapview.showZoomControls(false);
        maptype = mainmapview.getMap();
        fabinit();
        Netcheck();
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,ConnectService.class);
        intent.putExtra("messenger",new Messenger(handler));
        startService(intent);
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
        onMarkClick(maptype,markerinfo);
        onMapClick(maptype,markerinfo);

    }

    public void onMapClick(final BaiduMap maptype, final LinearLayout markerinfo){
        maptype.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(markerinfo.getVisibility() == View.VISIBLE) {
                    Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.tran_out);
                    markerinfo.startAnimation(animation);
                    markerinfo.setVisibility(View.GONE);
                    maptype.hideInfoWindow();
                }
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    public void Netcheck(){
        if(netcheck.checkNetwork(this)== false){
            wait.setText(R.string.Netcheck);
        }
    }

    public void fabinit(){
        fab = (FloatingActionButton)findViewById(R.id.Refresh);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(markerinfo.getVisibility()==View.VISIBLE){
                    Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.tran_out);
                    markerinfo.startAnimation(animation);
                    markerinfo.setVisibility(View.GONE);
                }
                maptype.clear();
                ObjectAnimator animator = ObjectAnimator.ofFloat(view,"rotation",1080,-20,0);
                animator.setDuration(5000);
                animator.start();
                myService.refreshdevid();
                Toast.makeText(MainActivity.this,"已刷新数据,请重新选择查询",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onMarkClick(final BaiduMap maptype, final LinearLayout markerinfo){
        maptype.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(Info.infos.size()>0) {
                    Info info = (Info) marker.getExtraInfo().get("info");
                    TextView getmore = new TextView(getApplicationContext());
                    getmore.setBackgroundResource(R.drawable.markerinfo);
                    getmore.setText("详细信息");
                    getmore.setTextColor(getResources().getColor(R.color.white));
                    getmore.setPadding(30, 20, 30, 50);
                    LatLng point = marker.getPosition();
                    InfoWindow mInfoWindow = new InfoWindow(getmore, point, -120);
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(point, 17);
                    maptype.animateMapStatus(u, 2000);
                    maptype.showInfoWindow(mInfoWindow);
                    markerinfo.setVisibility(View.VISIBLE);
                    Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.tran_in);
                    markerinfo.startAnimation(animation);
                    popupInfo(markerinfo, info);
                    return false;
                }else{
                    TextView lineinfo = new TextView(getApplicationContext());
                    lineinfo.setBackgroundResource(R.drawable.markerinfo);
                    lineinfo.setTextColor(getResources().getColor(R.color.white));
                    lineinfo.setPadding(30, 20, 30, 50);
                    if (marker.getExtraInfo().getInt("id")==0){
                        lineinfo.setText("起点");
                    }else{
                        lineinfo.setText("终点");
                    }
                    LatLng point = marker.getPosition();
                    InfoWindow mInfoWindow = new InfoWindow(lineinfo, point, -120);
                    maptype.showInfoWindow(mInfoWindow);
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(point, 20);
                    maptype.animateMapStatus(u, 2000);
                    return false;
                }
            }
        });
    }

    protected void popupInfo(LinearLayout mMarkerLy,Info info){
        ViewHolder viewHolder = null;
        if (mMarkerLy.getTag() == null) {
            viewHolder = new ViewHolder();
            viewHolder.speed = (TextView)mMarkerLy.findViewById(R.id.speed);
            viewHolder.accelerate=(TextView)mMarkerLy.findViewById(R.id.accelerate);
            viewHolder.stress=(TextView)mMarkerLy.findViewById(R.id.stress);
            viewHolder.temp=(TextView)mMarkerLy.findViewById(R.id.temp);
            viewHolder.name=(TextView)mMarkerLy.findViewById(R.id.devname);
            viewHolder.onlinestatus=(ImageView)mMarkerLy.findViewById(R.id.devonlinestatus);
            mMarkerLy.setTag(viewHolder);
        }
        viewHolder = (ViewHolder)mMarkerLy.getTag();
        viewHolder.speed.setText(String.valueOf(info.getSpeed()));
        viewHolder.accelerate.setText(String.valueOf(info.getAccelerate()));
        viewHolder.stress.setText(String.valueOf(info.getStress()));
        viewHolder.temp.setText(String.valueOf(info.getTemp()));
        viewHolder.name.setText("<"+info.getName()+">");
        if(info.getOnlinestatus().equals("0")){
            viewHolder.onlinestatus.setImageResource(R.drawable.reddot);
        }else if(info.getOnlinestatus().equals("1")){
            viewHolder.onlinestatus.setImageResource(R.drawable.greendot);
        }
        }

    public void CarMark(List<Info> infos,BaiduMap maptype){
        maptype.clear();
        LatLng latLng = null;
        OverlayOptions option = null;
        Marker marker = null;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        final BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marker);
        for (Info info:infos){
            latLng = new LatLng(info.getLatitude(),info.getLongitude());
            option = new MarkerOptions().position(latLng).icon(bitmap);
            marker = (Marker)(maptype.addOverlay(option));
            Bundle bundle = new Bundle();
            bundle.putSerializable("info", info);
            marker.setExtraInfo(bundle);
            builder = builder.include(latLng);
        }
        LatLngBounds latlngBounds = builder.build();
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(latlngBounds,mainmapview.getWidth(),mainmapview.getHeight());
        maptype.animateMapStatus(u,4000);
    }

    public void Location(BaiduMap maptype){
        maptype.clear();
        Marker markerstart = null;
        Marker markerend = null;
        List<LatLng> points = new ArrayList<LatLng>();
        for(int i=0;i<HistroyInfo.histroyInfos.size();i++){
            points.add(new LatLng(HistroyInfo.histroyInfos.get(i).getLatitude(),HistroyInfo.histroyInfos.get(i).getLongitude()));
        }
        List<OverlayOptions> lineOptions = new ArrayList<OverlayOptions>();
        OverlayOptions lineoption = new PolylineOptions().color(0xAAFF0000).width(12).points(points);
        lineOptions.add(lineoption);
        maptype.addOverlays(lineOptions);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng p : points) {
            builder = builder.include(p);
        }
        final BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marker);
        LatLng end = points.get(0);
        LatLng start = points.get(points.size()-1);
        OverlayOptions option = new MarkerOptions().position(start).icon(bitmap);
        OverlayOptions option0 = new MarkerOptions().position(end).icon(bitmap);
        markerstart = (Marker)(maptype.addOverlay(option));
        markerend = (Marker)(maptype.addOverlay(option0));
        Bundle bundle = new Bundle();
        bundle.putInt("id",0);
        markerstart.setExtraInfo(bundle);
        Bundle bundle0 = new Bundle();
        bundle0.putInt("id",1);
        markerend.setExtraInfo(bundle0);
        LatLngBounds latlngBounds = builder.build();
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngBounds(latlngBounds,mainmapview.getWidth(),mainmapview.getHeight());
        maptype.setMapStatus(u);
    }
    @Override
    public void onBackPressed() {
        if (mFloatingNavigationView.isOpened()) {
            mFloatingNavigationView.close();
        } else {
            super.onBackPressed();
        }
    }

    protected void onStart(){
        super.onStart();
    }

    protected void onStop(){
        super.onStop();
        Intent intent = new Intent();
        intent.setClass(MainActivity.this,ConnectService.class);
        stopService(intent);
    }

    protected void onDestroy(){
        super.onDestroy();
        mainmapview.onDestroy();
        unbindService(serviceConnection);
    }

    protected void onResume(){
        super.onResume();
        mainmapview.onResume();
    }

    protected void onPause(){
        super.onPause();
        mainmapview.onPause();
    }

    private class ViewHolder {
        TextView speed;
        TextView accelerate;
        TextView stress;
        TextView temp;
        TextView name;
        ImageView onlinestatus;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                if (requestCode ==1){
                    if(HistroyInfo.histroyInfos.size()>0) {
                        if(Info.infos.size()>0){
                            for (int i=0;i<Info.infos.size();){
                                Info.infos.remove(i);
                            }
                        }
                        maptype.clear();
                        markerinfo.setVisibility(View.GONE);
                        Location(maptype);
                    }else{
                        Toast.makeText(MainActivity.this,"无匹配历史数据",Toast.LENGTH_LONG).show();
                    }
                }
    }

    public Handler handler = new Handler(){

        public void handleMessage(Message msg){
            switch (msg.what){
                case 1:wait.setText("正在连接服务器");break;
                case 2:if ((msg.obj.toString()).equals("ok")){
                    wait.setText("服务器连接成功");break;
                }else{
                    wait.setText(msg.obj.toString()+"\n请重启重试");break;
                }
                case 3:wait.setText("正在更新终端列表");break;
                case 4:if ((msg.obj.toString()).equals("ok")){
                        wait.setText("终端列表更新成功");mainmapview.setVisibility(View.VISIBLE);fab.setVisibility(View.VISIBLE);
                        wait.setVisibility(View.GONE);break;
                }else{
                    wait.setText(msg.obj.toString()+"\n请重启重试");break;
                }
                case 5:Toast.makeText(MainActivity.this,"连接服务器失败\n请联系管理员检查",Toast.LENGTH_LONG).show();
                       wait.setText("连接服务器失败\n请联系管理员检查");break;
                case 6:Toast.makeText(MainActivity.this,"网络错误，请检查网络后重试",Toast.LENGTH_LONG).show();
                       wait.setText("网络错误，请检查网络后重试");break;
                case 7:CarMark(Info.infos,maptype);break;
                case 8:Toast.makeText(MainActivity.this,"因网络问题未连接服务器\n请检查网络后重启",Toast.LENGTH_LONG).show();
                       wait.setText("因网络问题未连接服务器\n请检查网络后重启");break;
            }
        }
    };
}
