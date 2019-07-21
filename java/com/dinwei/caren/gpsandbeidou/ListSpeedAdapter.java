package com.dinwei.caren.gpsandbeidou;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Carendule on 2018/4/18.
 */

public class ListSpeedAdapter extends BaseAdapter{
    private Context context;
    private LayoutInflater listContainer;

    public final class ListItemView{
        public TextView listtime;
        public TextView listvalue;
    }

    public ListSpeedAdapter(Context context){
        this.context = context;
        listContainer = LayoutInflater.from(context);

    }
    @Override
    public int getCount() {
        return HistorySpeed.historySpeeds.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ListItemView listItemView = null;
        if (view == null){
            listItemView = new ListItemView();
            view = listContainer.inflate(R.layout.historylist,null);
            listItemView.listtime = (TextView)view.findViewById(R.id.listtime);
            listItemView.listvalue = (TextView)view.findViewById(R.id.listvalue);
            view.setTag(listItemView);
        }else{
            listItemView = (ListItemView) view.getTag();
        }
        if (HistorySpeed.historySpeeds.isEmpty()){
            listItemView.listtime.setText("无数据");
            listItemView.listvalue.setText("无数据");
        }else{
            listItemView.listtime.setText(unix2data(HistorySpeed.historySpeeds.get(position).getChangetime()));
            listItemView.listvalue.setText(String.valueOf(HistorySpeed.historySpeeds.get(position).getSpeed()));

        }
        return view;
    }

    public String unix2data(Long time){
        String formats = "yyyy-MM-dd HH:mm:ss";
        String timetamp = time.toString();
        Long timestamp = Long.parseLong(timetamp) * 1000;
        String data = new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));
        return data;
    }
}
