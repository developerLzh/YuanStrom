package com.lzh.yuanstrom.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.LocalDeviceBean;
import com.lzh.yuanstrom.bean.SimpleDeviceBean;
import com.lzh.yuanstrom.ui.DevControlActivity;
import com.lzh.yuanstrom.ui.GroupActivity;
import com.lzh.yuanstrom.utils.StringUtils;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import me.hekr.hekrsdk.bean.GroupBean;

/**
 * Created by Administrator on 2016/10/13.
 */

public class SecondPageAdapter extends RecyclerView.Adapter<SecondPageAdapter.ViewHolder> {

    private List<GroupBean> groups;

    private Context context;

    public SecondPageAdapter(Context context) {
        this.context = context;
        groups = new ArrayList<>();

    }

    public void setGroups(List<GroupBean> groups) {
        this.groups = groups;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_group_item, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (groups.size() != 0) {
            final GroupBean groupBean = groups.get(position);
            holder.title.setText(groupBean.getGroupName());
            holder.subTitle.setText(context.getString(R.string.dev_no) + groupBean.getDeviceList().size());
            holder.gridView.setAdapter(new GridAdapter(context, groupBean.getDeviceList()));
            holder.arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.expandableLayout.isExpanded()) {

                        rotateView(holder.arrow, 180, 0);
                        holder.expandableLayout.collapse();
                    } else {
                        rotateView(holder.arrow, 0, 180);
                        holder.expandableLayout.expand();
                    }
                }
            });
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toGroupActivity(groupBean);
                }
            });
        }
    }

    private void toGroupActivity(GroupBean groupBean) {

        List<GroupBean.DeviceLis> cloudLs = groupBean.getDeviceList();
        List<LocalDeviceBean> localNotAdded = LocalDeviceBean.findALll();

        List<SimpleDeviceBean> added = new ArrayList<>();
        List<SimpleDeviceBean> notAdded = new ArrayList<>();

        for (int i = 0; i < cloudLs.size(); i++) {
            GroupBean.DeviceLis cloudL = cloudLs.get(i);
            LocalDeviceBean local = LocalDeviceBean.findByTid(cloudL.getDevTid());
            if (StringUtils.isNotBlank(local.devTid)) {//本地存在
                SimpleDeviceBean simDean = new SimpleDeviceBean();
                simDean.setId(i);
                simDean.setDevTid(local.devTid);
                simDean.setCtrlKey(local.ctrlKey);
                simDean.setDevCate(local.categoryName);
                simDean.setLogo(local.logo);
                simDean.setDevCate(local.categoryName);
                added.add(simDean);
                Iterator<LocalDeviceBean> iteator = localNotAdded.iterator();
                while (iteator.hasNext()) {
                    LocalDeviceBean localbean = iteator.next();
                    if (localbean.devTid.equals(local.devTid) ) {
                        iteator.remove();
                        break;
                    }
                }
            } else {//本地不存在
                SimpleDeviceBean simDean = new SimpleDeviceBean();
                simDean.setId(i);
                simDean.setDevTid(cloudL.getDevTid());
                simDean.setCtrlKey(cloudL.getCtrlKey());
                added.add(simDean);
            }
        }

        for (int i = 0; i < localNotAdded.size(); i++) {
            LocalDeviceBean local = localNotAdded.get(i);
            SimpleDeviceBean simDean = new SimpleDeviceBean();
            simDean.setId(i);
            simDean.setDevTid(local.devTid);
            simDean.setCtrlKey(local.ctrlKey);
            simDean.setDevCate(local.categoryName);
            simDean.setLogo(local.logo);
            simDean.setDevCate(local.categoryName);
            notAdded.add(simDean);
        }


        Intent intent = new Intent(context, GroupActivity.class);
        intent.putExtra("notAdded", (Serializable) notAdded);
        intent.putExtra("added", (Serializable) added);
        intent.putExtra("groupName", groupBean.getGroupName());
        intent.putExtra("desc", groupBean.getDesc());
        intent.putExtra("groupId", groupBean.getGroupId());
        intent.putExtra("isNewCreate", false);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public GridView gridView;

        public TextView title;

        public TextView subTitle;

        public ExpandableLayout expandableLayout;

        public View rootView;

        public ImageView arrow;

        public ViewHolder(View view) {
            super(view);
            gridView = (GridView) view.findViewById(R.id.grid_view);
            title = (TextView) view.findViewById(R.id.title);
            subTitle = (TextView) view.findViewById(R.id.subtitle);
            expandableLayout = (ExpandableLayout) view.findViewById(R.id.expand_layout);
            arrow = (ImageView) view.findViewById(R.id.arrow);
            rootView = view;
        }
    }

    /**
     * 旋转动画
     *
     * @param view
     * @param fromDegrees
     * @param toDegrees
     */
    private void rotateView(View view, int fromDegrees, final int toDegrees) {
        if (view == null) {
            return;
        }
        //旋转起始角度fromDegrees,旋转结束角度toDegrees
        //x轴相对于自己,距离x轴边界50%,y轴相对于自己,距离y轴边界50%,即图片中心旋转
        RotateAnimation rotateAnimation = new RotateAnimation(fromDegrees, toDegrees, Animation.RELATIVE_TO_SELF, 0.5F,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(500);
        rotateAnimation.setFillAfter(true); //旋转后停留在这个状态
        view.startAnimation(rotateAnimation);
    }
}
