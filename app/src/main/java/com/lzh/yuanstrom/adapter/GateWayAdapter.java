package com.lzh.yuanstrom.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.LinkedList;

import com.lzh.yuanstrom.bean.CardBean;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.GateWayBean;
import com.lzh.yuanstrom.ui.GateWayAct;


/**
 * Created by Vicent on 2016/5/9.
 */
public class GateWayAdapter extends RecyclerView.Adapter<GateWayHolder> {
    private List<GateWayBean> beans;
    private Context context;
    private int rawLayout;
    private GateWayAct act;

    public GateWayAdapter(Context context, int rawLayout, GateWayAct act) {
        this.context = context;
        beans = new LinkedList<>();
        this.rawLayout = rawLayout;
        this.act = act;
    }

    public void setBeans(List<GateWayBean> beans) {
        this.beans = beans;
        notifyDataSetChanged();
    }

    @Override
    public GateWayHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(rawLayout, parent, false);
        return new GateWayHolder(v);
    }

    @Override
    public void onBindViewHolder(final GateWayHolder holder, int position) {
        final GateWayBean bean = beans.get(position);
        holder.gateName.setText(bean.name);
        holder.connNo.setText(String.valueOf(0));
        holder.onlineNo.setText(String.valueOf(0));
        holder.offlinNo.setText(String.valueOf(0));
        if (bean.online == 0) {
            holder.gateState.setText(context.getResources().getString(R.string.offline));
        } else {
            holder.gateState.setText(context.getResources().getString(R.string.online));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.animateActivity(bean, holder.houseImg);
            }
        });
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }
}

class GateWayHolder extends RecyclerView.ViewHolder {
    TextView gateName;
    TextView connNo;
    TextView onlineNo;
    TextView offlinNo;
    TextView gateState;
    ImageView houseImg;

    public GateWayHolder(View itemView) {
        super(itemView);
        gateName = (TextView) itemView.findViewById(R.id.gate_name);
        connNo = (TextView) itemView.findViewById(R.id.conn_no);
        onlineNo = (TextView) itemView.findViewById(R.id.online_no);
        offlinNo = (TextView) itemView.findViewById(R.id.off_line_no);
        gateState = (TextView) itemView.findViewById(R.id.gate_state);
        houseImg = (ImageView) itemView.findViewById(R.id.house_img);
    }
}
