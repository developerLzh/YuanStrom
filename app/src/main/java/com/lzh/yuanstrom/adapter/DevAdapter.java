package com.lzh.yuanstrom.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzh.yuanstrom.bean.CardBean;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.ui.DevActivity;
import com.lzh.yuanstrom.utils.CardViewExpand;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Vicent on 2016/5/9.
 */
public class DevAdapter extends RecyclerView.Adapter<DevViewHolder> {
    private List<CardBean> beans;
    private Context context;
    private int rawLayout;
    private DevActivity act;

    public DevAdapter(Context context, int rawLayout, DevActivity act) {
        this.context = context;
        beans = new LinkedList<>();
        this.rawLayout = rawLayout;
        this.act = act;
    }

    public void setBeans(List<CardBean> beans) {
        this.beans = beans;
        notifyDataSetChanged();
    }

    @Override
    public DevViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rawLayout, parent, false);
        return new DevViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final DevViewHolder holder, int position) {
        final CardBean bean = beans.get(position);
        holder.title.setText(bean.title);
        holder.subTitle.setText(bean.subTitle);
        holder.image.setImageResource(bean.imgRes);
        holder.content.setText(bean.content);

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (holder.bottomContainer.getVisibility() == View.VISIBLE) {
//                    CardViewExpand.collapse(holder.bottomContainer);
//                } else {
//                    CardViewExpand.expand(holder.bottomContainer);
//                }
//
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }
}

class DevViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    ImageView image;
    TextView subTitle;
    TextView content;
    LinearLayout bottomContainer;

    public DevViewHolder(View itemView) {
        super(itemView);
        subTitle = (TextView) itemView.findViewById(R.id.subtitle);
        title = (TextView) itemView.findViewById(R.id.title);
        image = (ImageView) itemView.findViewById(R.id.image);
        content = (TextView) itemView.findViewById(R.id.content);
        bottomContainer = (LinearLayout) itemView.findViewById(R.id.bottom_container);
    }

}

