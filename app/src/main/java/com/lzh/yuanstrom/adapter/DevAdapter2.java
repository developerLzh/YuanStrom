package com.lzh.yuanstrom.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.CateBean;
import com.lzh.yuanstrom.bean.DeviceInfo;
import com.lzh.yuanstrom.utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vicent on 2016/6/19.
 */
public class DevAdapter2 extends RecyclerView.Adapter {

    private int rawLayout;
    private Context context;

    private List<DeviceInfo> beans;

    private OnItemClickLinsenter linsenter;

    public DevAdapter2(Context context, int rawLayout) {
        this.context = context;
        this.rawLayout = rawLayout;
        beans = new ArrayList<>();
    }

    public void setBeans(List<DeviceInfo> beans) {
        this.beans = beans;
        notifyDataSetChanged();
    }

    public void setLinsenter(OnItemClickLinsenter clickLinsenter){
        this.linsenter = clickLinsenter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rawLayout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final DeviceInfo bean = beans.get(position);
        final ViewHolder devHolder = (ViewHolder) holder;

//        if (bean.gridVisible) {
//            devHolder.gridLayout.setVisibility(View.VISIBLE);
//        } else {
//            devHolder.gridLayout.setVisibility(View.GONE);
//        }

        devHolder.devCate.setText(bean.devName);
//        devHolder.connNo.setText(bean.connedNo + "");
//        devHolder.onlineNo.setText(bean.onlineNo + "");
//        devHolder.offlinNo.setText(bean.offlinNo + "");
        if (bean.devCategory.equals(context.getResources().getString(R.string.lamp))) {
            devHolder.devImg.setImageResource(R.mipmap.lamp_icon_1);
        } else if (bean.devCategory.equals(context.getResources().getString(R.string.chazuo))) {
            devHolder.devImg.setImageResource(R.mipmap.chazuo_icon);
        } else if (bean.devCategory.equals(context.getResources().getString(R.string.yaokong))) {
            devHolder.devImg.setImageResource(R.mipmap.yaokong_icon);
        } else if (bean.devCategory.equals(context.getResources().getString(R.string.menci))) {
            devHolder.devImg.setImageResource(R.mipmap.menci_icon);
        } else if (bean.devCategory.equals(context.getResources().getString(R.string.hongwai))) {
            devHolder.devImg.setImageResource(R.mipmap.hongwai_icon);
        } else if (bean.devCategory.equals(context.getResources().getString(R.string.changjing))) {
            devHolder.devImg.setImageResource(R.mipmap.house_icon);
        } else if (bean.devCategory.equals(context.getResources().getString(R.string.mianban))) {
            devHolder.devImg.setImageResource(R.mipmap.mianban_icon);
        } else if(bean.devCategory.equals(context.getResources().getString(R.string.yaokong_2))){
            devHolder.devImg.setImageResource(R.mipmap.yaokong_icon);
        }
        devHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linsenter.onItemClick(position);
            }
        });
//        WindowManager wm = (WindowManager) context
//                .getSystemService(Context.WINDOW_SERVICE);
//        int width = wm.getDefaultDisplay().getWidth();
//
//        for (DeviceInfo dev : bean.devs) {
//
//            LinearLayout gridItem = new LinearLayout(context);
//            gridItem.setOrientation(LinearLayout.VERTICAL);
//            gridItem.setGravity(Gravity.CENTER);
//            ImageView detailImg = new ImageView(context);
//            TextView detailName = new TextView(context);
//
//            if (dev.devCategory.equals(context.getResources().getString(R.string.lamp))) {
//                detailImg.setImageResource(R.mipmap.lamp_icon_1);
//            } else if (dev.devCategory.equals(context.getResources().getString(R.string.chazuo))) {
//                detailImg.setImageResource(R.mipmap.chazuo_icon);
//            } else if (dev.devCategory.equals(context.getResources().getString(R.string.hongwai))) {
//                detailImg.setImageResource(R.mipmap.hongwai_icon);
//            } else if (dev.devCategory.equals(context.getResources().getString(R.string.menci))) {
//                detailImg.setImageResource(R.mipmap.menci_icon);
//            } else if (dev.devCategory.equals(context.getResources().getString(R.string.activity_yaokong))) {
//                detailImg.setImageResource(R.mipmap.yaokong_icon);
//            } else if (dev.devCategory.equals(context.getResources().getString(R.string.changjing))) {
//                detailImg.setImageResource(R.mipmap.house_icon);
//            } else if (dev.devCategory.equals(context.getResources().getString(R.string.mianban))) {
//                detailImg.setImageResource(R.mipmap.mianban_icon);
//            }else if(bean.devCate.equals(context.getResources().getString(R.string.yaokong_2))){
//                detailImg.setImageResource(R.mipmap.yaokong_icon);
//            }
//            detailName.setText(dev.devCategory);
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((width - Utils.dip2px(context, 48)) / 5, LinearLayout.LayoutParams.WRAP_CONTENT);
//            detailImg.setLayoutParams(params);
//            gridItem.addView(detailImg);
//
//            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            params2.topMargin = 20;
//            params2.gravity = Gravity.CENTER_HORIZONTAL;
//            detailName.setLayoutParams(params2);
//            gridItem.addView(detailName);
//
//            devHolder.gridLayout.addView(gridItem);
//        }
//        Log.e("data","position-->"+position+"---bean.visible--"+bean.gridVisible);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
//        ViewHolder devHolder = (ViewHolder) holder;
//        devHolder.gridLayout.removeAllViews();
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }
}

class ViewHolder extends RecyclerView.ViewHolder {

    ImageView devImg;
    TextView devCate;
    TextView connNo;
    TextView onlineNo;
    TextView offlinNo;
//    GridLayout gridLayout;
    RelativeLayout topVisible;
    RelativeLayout rootView;

    public ViewHolder(View itemView) {
        super(itemView);
        devCate = (TextView) itemView.findViewById(R.id.dev_cate);
        connNo = (TextView) itemView.findViewById(R.id.conn_no);
        onlineNo = (TextView) itemView.findViewById(R.id.online_no);
        offlinNo = (TextView) itemView.findViewById(R.id.off_line_no);
//        gridLayout = (GridLayout) itemView.findViewById(R.id.grid_layout);
        devImg = (ImageView) itemView.findViewById(R.id.lamp_img);
        topVisible = (RelativeLayout) itemView.findViewById(R.id.top_visible);
        rootView = (RelativeLayout) itemView.findViewById(R.id.root_view);
    }
}
