package com.lzh.yuanstrom.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.ArrayList;

import com.lzh.yuanstrom.R;

import me.hekr.hekrsdk.bean.DeviceBean;

/**
 * Created by chris.black on 6/11/15.
 */
public class FirstPageAdapter extends RecyclerView.Adapter<FirstPageAdapter.ViewHolder> {

    private int mPage;
    private List<DeviceBean> devices;
    private Context context;

    public FirstPageAdapter(int pageNum, Context context) {
        super();
        this.mPage = pageNum;
        devices = new ArrayList<>();
        this.context = context;
    }

    public void setDevices(List<DeviceBean> devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_dev_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (devices.size() != 0) {
            DeviceBean deviceBean = devices.get(position);
            holder.title.setText(deviceBean.getDeviceName());
            if (deviceBean.isOnline()) {
                holder.subTitle.setText(context.getString(R.string.online));
            } else {
                holder.subTitle.setText(context.getString(R.string.offline));
            }
//            holder.description.setText(deviceBean.getCid());
        }
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView subTitle;
        public ImageView icon;
//        public TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            subTitle = (TextView) itemView.findViewById(R.id.subtitle);
            icon = (ImageView) itemView.findViewById(R.id.image);
//            description = (TextView) itemView.findViewById(R.id.content);
        }
    }
}
