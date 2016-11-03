package com.lzh.yuanstrom.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lzh.yuanstrom.MyApplication;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.LocalDeviceBean;
import com.lzh.yuanstrom.utils.StringUtils;
import com.lzh.yuanstrom.utils.ToastUtil;

import java.util.List;

import me.hekr.hekrsdk.bean.DeviceBean;
import me.hekr.hekrsdk.bean.GroupBean;

/**
 * Created by Administrator on 2016/10/13.
 */

public class GridAdapter extends BaseAdapter {

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
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.grid_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.detail_txt);
            holder.imageView = (ImageView) convertView.findViewById(R.id.detail_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final GroupBean.DeviceLis bean = devices.get(position);
        final LocalDeviceBean local = LocalDeviceBean.findByTid(bean.getDevTid());
        holder.title.setText(bean.getDevTid());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isBlank(local.devTid)) {
                    ToastUtil.showMessage(context, context.getString(R.string.device_not_found));
                    return;
                }
                FirstPageAdapter.toWhatActivityByCateName(context, local.categoryName, local.devTid);
            }
        });
        if(StringUtils.isNotBlank(local.logo)){
            MyApplication.getInstance().getImageLoader().get(local.logo, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean b) {
                    Bitmap bitmap = response.getBitmap();
                    if(bitmap != null){
                        holder.imageView.setImageBitmap(bitmap);
                    }else{
                        holder.imageView.setImageResource(R.mipmap.chazuo_icon);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    holder.imageView.setImageResource(R.mipmap.chazuo_icon);
                }
            });
        }
        if(local.online){
            holder.imageView.setBackgroundColor(context.getResources().getColor(R.color.white));
        }else{
            holder.imageView.setBackgroundColor(context.getResources().getColor(R.color.gray));
        }
        return convertView;
    }

    final class ViewHolder {
        public TextView title;
        public ImageView imageView;
    }
}
