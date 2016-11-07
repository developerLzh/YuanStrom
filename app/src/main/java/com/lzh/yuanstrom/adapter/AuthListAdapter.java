package com.lzh.yuanstrom.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.lzh.yuanstrom.MyApplication;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.LocalDeviceBean;
import com.lzh.yuanstrom.utils.StringUtils;

import java.util.List;
import java.util.ArrayList;

import me.hekr.hekrsdk.bean.OAuthListBean;

/**
 * Created by chris.black on 6/11/15.
 */
public class AuthListAdapter extends RecyclerView.Adapter<AuthListAdapter.ViewHolder> {

    public List<OAuthListBean> list;

    private Context context;

    private String devTid;

    private OnClickListener deleteClick;

    public void setAuthListAdapter(List<OAuthListBean> list,String devTid) {
        this.list = list;
        this.devTid = devTid;
        notifyDataSetChanged();
    }

    public AuthListAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    public void setDeleteClick(OnClickListener deleteClick) {
        this.deleteClick = deleteClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.granted_dev_item, parent, false);
        return new AuthListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final OAuthListBean bean = list.get(position);
        if (bean != null) {
            final LocalDeviceBean local = LocalDeviceBean.findByTid(devTid);
            holder.devCate.setText(local.categoryName);
            holder.devName.setText(local.deviceName);
            holder.grantee.setText(context.getString(R.string.share_owner) + bean.getGranteeName());
            if (StringUtils.isNotBlank(local.logo)) {
                MyApplication.getInstance().getImageLoader().get(local.logo, new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean b) {
                        Bitmap bitmap = response.getBitmap();
                        if (null != bitmap) {
                            holder.devImg.setImageBitmap(bitmap);
                        } else {
                            holder.devImg.setImageResource(R.mipmap.chazuo_icon);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        holder.devImg.setImageResource(R.mipmap.chazuo_icon);
                    }
                });
            }
            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.deleteImg.getVisibility() == View.VISIBLE) {
                        holder.deleteImg.setVisibility(View.GONE);
                    }
                }
            });
            holder.deleteImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (null != deleteClick) {
                        deleteClick.OnClick(bean.getGrantee());
                    }
                }
            });
            holder.root.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    holder.deleteImg.setVisibility(View.VISIBLE);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView devImg;
        public TextView devName;
        public TextView devCate;
        public TextView grantee;
        public ImageView deleteImg;
        public LinearLayout root;

        public ViewHolder(View itemView) {
            super(itemView);
            devImg = (ImageView) itemView.findViewById(R.id.dev_img);
            deleteImg = (ImageView) itemView.findViewById(R.id.delete_img);
            devName = (TextView) itemView.findViewById(R.id.dev_name);
            devCate = (TextView) itemView.findViewById(R.id.dev_cate);
            grantee = (TextView) itemView.findViewById(R.id.grantee);
            root = (LinearLayout) itemView.findViewById(R.id.root);
        }
    }

    public interface OnClickListener {
        void OnClick(String grantee);
    }
}
