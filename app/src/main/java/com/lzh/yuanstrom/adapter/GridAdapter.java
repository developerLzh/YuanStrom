package com.lzh.yuanstrom.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lzh.yuanstrom.R;

import java.util.List;

import me.hekr.hekrsdk.bean.DeviceBean;
import me.hekr.hekrsdk.bean.GroupBean;

/**
 * Created by Administrator on 2016/10/13.
 */

public class GridAdapter extends BaseAdapter{

    private Context context;

    private List<GroupBean.DeviceLis> devices;

    public GridAdapter(Context context, List<GroupBean.DeviceLis> devices) {
        this.context = context;
        this.devices = devices;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.grid_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.detail_txt);
//            holder.subTitle = (TextView) convertView.findViewById(R.id.subtitle);
//            holder.description = (TextView) convertView.findViewById(R.id.content);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        GroupBean.DeviceLis bean = devices.get(position);
        holder.title.setText(bean.getDevTid());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
            }
        });
//        if(bean.isOnline()){
//            holder.subTitle.setText(context.getString(R.string.online));
//        }else{
//            holder.subTitle.setText(context.getString(R.string.offline));
//        }
//        holder.description.setText(bean.getCid());
        return convertView;
    }

    final class ViewHolder {
        public TextView title;
        public TextView subTitle;
        public TextView description;
    }
}
