package com.lzh.yuanstrom.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.ui.DevControlActivity;

import net.cachapa.expandablelayout.ExpandableLayout;

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
            GroupBean groupBean = groups.get(position);
            holder.title.setText(groupBean.getGroupName());
            holder.subTitle.setText(context.getString(R.string.dev_no) + groupBean.getDeviceList().size());
            holder.gridView.setAdapter(new GridAdapter(context, groupBean.getDeviceList()));
            holder.arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.expandableLayout.isExpanded()) {
                        RotateAnimation rotateAnimation = new RotateAnimation(0,180);
                        holder.arrow.startAnimation(rotateAnimation);
                        holder.expandableLayout.collapse();
                    } else {
                        RotateAnimation rotateAnimation = new RotateAnimation(0,-180);
                        holder.arrow.startAnimation(rotateAnimation);
                        holder.expandableLayout.expand();
                    }
                }
            });
            holder.rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }
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
}
