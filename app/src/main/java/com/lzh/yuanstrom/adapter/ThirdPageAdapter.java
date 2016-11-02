package com.lzh.yuanstrom.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.LocalDeviceBean;
import com.lzh.yuanstrom.bean.ProfileData;
import com.lzh.yuanstrom.utils.CommandHelper;
import com.lzh.yuanstrom.utils.FullCommandHelper;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import me.hekr.hekrsdk.bean.GroupBean;

/**
 * Created by Administrator on 2016/10/13.
 */

public class ThirdPageAdapter extends RecyclerView.Adapter<ThirdPageAdapter.ViewHolder> {

    private List<ProfileData> datas;

    private Context context;

    private OnClickListener onClickListener;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public ThirdPageAdapter(Context context) {
        this.context = context;
        datas = new ArrayList<>();
    }

    public void setDatas(List<ProfileData> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (datas.size() != 0) {
            ProfileData profileData = datas.get(position);
            holder.profileName.setText(profileData.profileName);
            StringBuilder sb = new StringBuilder();
            if (profileData.profileDatas != null) {
                for (String data : profileData.profileDatas) {
                    JSONObject js = JSONObject.parseObject(data);
                    String devTid = js.getJSONObject("params").getString("devTid");
                    String devName = LocalDeviceBean.findByTid(devTid).deviceName;
                    String cmd = js.getJSONObject("params").getJSONObject("data").getString("raw");
                    sb.append("devName:").append(devName).append("  ").append("action:").append(detailData(cmd)).append("\n");
                }
            }
            holder.actionTxt.setText(sb.toString());
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != onClickListener) {
                        onClickListener.onClick(view);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView profileName;

        public TextView actionTxt;

        public View root;

        public ViewHolder(View view) {
            super(view);
            profileName = (TextView) view.findViewById(R.id.profile_name);
            actionTxt = (TextView) view.findViewById(R.id.action);
            root = view.findViewById(R.id.root);
        }
    }

    protected String detailData(String data) {
        String useful = data.substring(8, data.length() - 2);
        if (useful.length() < 10) {
            return "";
        }
        String firstCommand = useful.substring(0, 2);
        String secondCommand = useful.substring(2, 4);
        String thirdCommand = useful.substring(4, 6);
        if (firstCommand.equals(CommandHelper.SWITCH_COMMAND)) {
            StringBuilder sb = new StringBuilder();
            int b = Integer.parseInt(thirdCommand, 16);
            if (b == 1) {
                sb.append("open ");
            } else if (b == 2) {
                sb.append("close ");
            }
            int a = Integer.parseInt(secondCommand, 16);
            if (a == 1) {
                sb.append("first jack");
            } else if (a == 2) {
                sb.append("second jack");
            } else if (a == 3) {
                sb.append("third jack");
            } else if (a == 4) {
                sb.append("fourth jack");
            }
            return sb.toString();
        }
        return "";
    }

    public interface OnClickListener {
        void onClick(View v);
    }

}
