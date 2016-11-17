package com.lzh.yuanstrom.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
import com.lzh.yuanstrom.ui.DevControlActivity;
import com.lzh.yuanstrom.utils.CommandHelper;
import com.lzh.yuanstrom.utils.FullCommandHelper;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import co.mobiwise.materialintro.animation.MaterialIntroListener;
import co.mobiwise.materialintro.prefs.PreferencesManager;
import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;
import me.hekr.hekrsdk.bean.GroupBean;

/**
 * Created by Administrator on 2016/10/13.
 */

public class ThirdPageAdapter extends RecyclerView.Adapter<ThirdPageAdapter.ViewHolder> {

    private List<ProfileData> datas;

    private Activity context;

    private OnClickListener onClickListener;

    private OnLongClickListener onLongClickListener;

    private PreferencesManager preferencesManager;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public ThirdPageAdapter(Activity context) {
        this.context = context;
        datas = new ArrayList<>();
        preferencesManager = new PreferencesManager(context);
    }

    public void setDatas(List<ProfileData> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    public List<ProfileData> getDatas() {
        return datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.profile_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (datas.size() != 0) {
            final ProfileData profileData = datas.get(position);
            holder.profileName.setText(profileData.profileName);
            StringBuilder sb = new StringBuilder();
//            if (profileData.profileDatas != null) {
//                for (String data : profileData.profileDatas) {
//                    JSONObject js = JSONObject.parseObject(data);
//                    String devTid = js.getJSONObject("params").getString("devTid");
//                    String devName = LocalDeviceBean.findByTid(devTid).deviceName;
//                    String cmd = js.getJSONObject("params").getJSONObject("data").getString("raw");
//                    sb.append(context.getString(R.string.dev_name_maohao)).append(devName).append("  ").append(context.getString(R.string.action)).append(detailData(cmd)).append("\n");
//                }
//            }
//            holder.actionTxt.setText(sb.toString());
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != onClickListener) {
                        if (preferencesManager.isDisplayed("ProfileItemLongClick")) {

                        } else {
                            showGuide(holder.root, context.getString(R.string.long_click), null, "ProfileItemLongClick", context);
                            return;
                        }
                        onClickListener.onClick(view, profileData);
                    }
                }
            });
            holder.root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (null != onLongClickListener) {
                        onLongClickListener.onLongClick(view, profileData);
                    }
                    return true;
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

    public interface OnClickListener {
        void onClick(View v, ProfileData profileData);
    }

    public interface OnLongClickListener {
        void onLongClick(View v, ProfileData profileData);
    }

    private void showGuide(View v, String guideStr, MaterialIntroListener listener, String useageId, Activity context) {
        new MaterialIntroView.Builder(context)
                .enableDotAnimation(true)
                .enableIcon(false)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(500)
                .enableFadeAnimation(true)
                .performClick(false)
                .setInfoText(guideStr)
                .setTarget(v)
                .dismissOnTouch(true)
                .setListener(listener)
                .setUsageId(useageId) //THIS SHOULD BE UNIQUE ID
                .show();
    }
}
