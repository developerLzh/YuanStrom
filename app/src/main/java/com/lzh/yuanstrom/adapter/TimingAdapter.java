package com.lzh.yuanstrom.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.LocalDeviceBean;
import com.lzh.yuanstrom.bean.TimingBean;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/23.
 */

public class TimingAdapter extends RecyclerView.Adapter<TimingAdapter.ViewHolder> {


    private TimingAdapter.OnItemClickListener clickListener;
    private TimingAdapter.OnItemLongClickListener longClickListener;

    private List<TimingBean> beans;

    private Context context;

    public void setOnItemClickListener(TimingAdapter.OnItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public TimingAdapter(Context context) {
        this.context = context;
        beans = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TimingBean bean1 = new TimingBean();
            beans.add(bean1);
        }
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }

    @Override
    public TimingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timing, parent, false);
        return new TimingAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final TimingAdapter.ViewHolder holder, final int position) {
        if (holder.expandableLayout.isExpanded()) {
            holder.expandImg.setVisibility(View.INVISIBLE);
        } else {
            holder.expandImg.setVisibility(View.VISIBLE);
        }
        holder.expandImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.expandableLayout.expand();
                AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
                alpha.setDuration(300);
                alpha.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        holder.expandImg.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                holder.expandImg.startAnimation(alpha);
            }
        });
        holder.collapseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.expandableLayout.collapse();
                AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
                alpha.setDuration(300);
                alpha.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        holder.expandImg.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                holder.expandImg.startAnimation(alpha);
            }
        });
        holder.deleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                beans.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView timeNoon;
        public TextView hourMinute;
        public Switch stateSwitch;
        public TextView repeatOrOnce;
        public ImageView expandImg;
        public AppCompatCheckBox repeatCheckbox;
        public CheckBox sunday;
        public CheckBox monday;
        public CheckBox tuesday;
        public CheckBox wednesday;
        public CheckBox thursday;
        public CheckBox friday;
        public CheckBox saturday;
        public TextView taskContent;
        public ExpandableLayout expandableLayout;
        public ImageView collapseImg;
        public ImageView deleteItem;

        public ViewHolder(View itemView) {
            super(itemView);
            timeNoon = (TextView) itemView.findViewById(R.id.time_noon);
            hourMinute = (TextView) itemView.findViewById(R.id.hour_minute);
            stateSwitch = (Switch) itemView.findViewById(R.id.state_switch);
            repeatOrOnce = (TextView) itemView.findViewById(R.id.repeat_or_once);
            expandImg = (ImageView) itemView.findViewById(R.id.expand_img);
            repeatCheckbox = (AppCompatCheckBox) itemView.findViewById(R.id.repeat_checkbox);
            sunday = (CheckBox) itemView.findViewById(R.id.sunday);
            monday = (CheckBox) itemView.findViewById(R.id.monday);
            tuesday = (CheckBox) itemView.findViewById(R.id.tuesday);
            wednesday = (CheckBox) itemView.findViewById(R.id.wednesday);
            thursday = (CheckBox) itemView.findViewById(R.id.thursday);
            friday = (CheckBox) itemView.findViewById(R.id.friday);
            saturday = (CheckBox) itemView.findViewById(R.id.saturday);
            taskContent = (TextView) itemView.findViewById(R.id.task_content);
            expandableLayout = (ExpandableLayout) itemView.findViewById(R.id.expandable_layout);
            collapseImg = (ImageView) itemView.findViewById(R.id.collapse_img);
            deleteItem = (ImageView) itemView.findViewById(R.id.delete_item);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(LocalDeviceBean bean, View v);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(LocalDeviceBean bean);
    }
}
