package com.lzh.yuanstrom.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.ArrayList;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.lzh.yuanstrom.MyApplication;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.LocalDeviceBean;
import com.lzh.yuanstrom.ui.ChazuoActivity;
import com.lzh.yuanstrom.ui.DevControlActivity;
import com.lzh.yuanstrom.ui.LampActivity;
import com.lzh.yuanstrom.ui.YaoKong2Activity;
import com.lzh.yuanstrom.utils.BitmapCache;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

import me.hekr.hekrsdk.bean.DeviceBean;

/**
 * Created by chris.black on 6/11/15.
 */
public class FirstPageAdapter extends RecyclerView.Adapter<FirstPageAdapter.ViewHolder> {

    private List<LocalDeviceBean> devices;
    private Context context;
    private CharSequence[] items;

    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    public FirstPageAdapter(Context context) {
        super();
        devices = new ArrayList<>();
        this.context = context;

        items = new CharSequence[]{context.getString(R.string.set), context.getString(R.string.delete), context.getString(R.string.cancel)};
    }

    public void setDevices(List<LocalDeviceBean> devices) {
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
            final LocalDeviceBean deviceBean = devices.get(position);
            holder.title.setText(deviceBean.categoryName);
            if (deviceBean.online) {
                holder.subTitle.setText(context.getString(R.string.online));
            } else {
                holder.subTitle.setText(context.getString(R.string.offline));
            }
//            holder.description.setText(deviceBean.getCid());
            MyApplication.getInstance().getImageLoader().get(deviceBean.logo, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    Bitmap bitmap = imageContainer.getBitmap();
                    if (null != bitmap) {
                        holder.icon.setImageBitmap(bitmap);
                    } else {
                        holder.icon.setImageBitmap(null);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    holder.icon.setImageBitmap(null);
                }
            }, 160, 160);
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != clickListener) {
                        if(deviceBean.isClicked){
                            deviceBean.isClicked = false;
                        }else{
                            deviceBean.isClicked = true;
                        }
                        clickListener.onItemClick(deviceBean,v);
                    }
                }
            });
            holder.root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if(null != longClickListener){
                       return longClickListener.onItemLongClick(deviceBean);
                    }
                    return false;
                }
            });
        }
    }

    public void showDialog(final Context context, final String devTid) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.choice_method))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (items[i].equals(context.getString(R.string.cancel))) {
                            dialog.dismiss();
                        } else if (items[i].equals(context.getString(R.string.delete))) {
                            dialog.dismiss();
                            showSureDialog();
                        } else if (items[i].equals(context.getString(R.string.set))) {
                            //TODO to setting
                            Intent intent = new Intent(context, DevControlActivity.class);
                            intent.putExtra("devTid", devTid);
                            context.startActivity(intent);
                        }
                    }
                })
                .create();
        dialog.show();
    }

    private void showSureDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.hint))
                .setMessage(context.getString(R.string.sure_delete))
                .setPositiveButton(context.getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO delete device
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView subTitle;
        public ImageView icon;
        public RelativeLayout root;
//        public TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            subTitle = (TextView) itemView.findViewById(R.id.subtitle);
            icon = (ImageView) itemView.findViewById(R.id.image);
            root = (RelativeLayout) itemView.findViewById(R.id.root_view);
//            description = (TextView) itemView.findViewById(R.id.content);
        }
    }

    public static void toWhatActivityByCateName(Context context, String cateName, String devTid) {
        Intent intent = new Intent();
        if (cateName.equals(context.getString(R.string.lamp))) {
            intent.setClass(context, LampActivity.class);
        } else if (cateName.equals(context.getString(R.string.chazuo))) {
            intent.setClass(context, ChazuoActivity.class);
        } else if (cateName.equals(context.getString(R.string.yaokong))) {
//            intent.setClass(context.YaoKongActivity.class);
        } else if (cateName.equals(context.getString(R.string.hongwai))) {

        } else if (cateName.equals(context.getString(R.string.menci))) {

        } else if (cateName.equals(context.getString(R.string.mianban))) {

        } else if (cateName.equals(context.getString(R.string.yaokong_2))) {
            intent.setClass(context, YaoKong2Activity.class);
        }
        intent.putExtra("devTid", devTid);
        context.startActivity(intent);
    }

    public interface OnItemClickListener {
        void onItemClick(LocalDeviceBean bean,View v);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(LocalDeviceBean bean);
    }

}
