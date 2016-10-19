package com.lzh.yuanstrom.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.ArrayList;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.utils.BitmapCache;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

import me.hekr.hekrsdk.bean.DeviceBean;

/**
 * Created by chris.black on 6/11/15.
 */
public class FirstPageAdapter extends RecyclerView.Adapter<FirstPageAdapter.ViewHolder> {

    private int mPage;
    private List<DeviceBean> devices;
    private Context context;

    private ImageLoader imageLoader;

    public FirstPageAdapter(int pageNum, Context context) {
        super();
        this.mPage = pageNum;
        devices = new ArrayList<>();
        this.context = context;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        this.imageLoader = new ImageLoader(requestQueue, new BitmapCache());
    }

    public void setDevices(List<DeviceBean> devices) {
        this.devices = devices;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_dev_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (devices.size() != 0) {
            DeviceBean deviceBean = devices.get(position);
            holder.title.setText(deviceBean.getDeviceName());
            if (deviceBean.isOnline()) {
                holder.subTitle.setText(context.getString(R.string.online));
            } else {
                holder.subTitle.setText(context.getString(R.string.offline));
            }
//            holder.description.setText(deviceBean.getCid());
            imageLoader.get(deviceBean.getLogo(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    Bitmap bitmap = imageContainer.getBitmap();
                    if(null != bitmap){
                        holder.icon.setImageBitmap(bitmap);
                    }else{
                        holder.icon.setImageBitmap(null);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    holder.icon.setImageBitmap(null);
                }
            },160,160);
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
