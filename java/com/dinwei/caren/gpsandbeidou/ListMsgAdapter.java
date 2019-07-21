package com.dinwei.caren.gpsandbeidou;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Carendule on 2018/4/16.
 */

public class ListMsgAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater listContainer;

    public final class ListItemView{
        public TextView listname;
        public TextView listonline;
        public ImageView listimage;
    }

    public ListMsgAdapter(Context context){
        this.context = context;
        listContainer = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return DevInfo.devinfos.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ListItemView listItemView = null;
        if (convertView == null){
            listItemView = new ListItemView();
            convertView = listContainer.inflate(R.layout.devlistitem,null);
            listItemView.listname = (TextView)convertView.findViewById(R.id.listname);
            listItemView.listonline = (TextView)convertView.findViewById(R.id.listonline);
            listItemView.listimage = (ImageView)convertView.findViewById(R.id.listimage);
            convertView.setTag(listItemView);
        }else{
            listItemView = (ListItemView) convertView.getTag();
        }
        listItemView.listname.setText(DevInfo.devinfos.get(position).getName());
        if (DevInfo.devinfos.get(position).getIsonline().equals("1")){
            listItemView.listonline.setText("在线");
            listItemView.listimage.setImageResource(R.drawable.greendot);
        }else{
            listItemView.listonline.setText("离线");
            listItemView.listimage.setImageResource(R.drawable.reddot);
        }
        return convertView;
    }
}
