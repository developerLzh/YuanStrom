/*
 *    Copyright (C) 2015 Haruki Hasegawa
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.lzh.yuanstrom.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.draggable.DraggableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.draggable.ItemDraggableRange;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractDraggableItemViewHolder;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.SimpleDeviceBean;
import com.lzh.yuanstrom.common.ExampleDataProvider;
import com.lzh.yuanstrom.utils.BitmapCache;
import com.lzh.yuanstrom.utils.DrawableUtils;
import com.lzh.yuanstrom.utils.ViewUtils;

public class DraggableExampleItemAdapter
        extends RecyclerView.Adapter<DraggableExampleItemAdapter.MyViewHolder>
        implements DraggableItemAdapter<DraggableExampleItemAdapter.MyViewHolder> {
    private static final String TAG = "MyDraggableItemAdapter";

    // NOTE: Make accessible with short name
    private interface Draggable extends DraggableItemConstants {
    }

    private ExampleDataProvider mProvider;

    public ExampleDataProvider getmProvider() {
        return mProvider;
    }

    public Context context;

    private ImageLoader imageLoader;

    public static class MyViewHolder extends AbstractDraggableItemViewHolder {
        public FrameLayout mContainer;
        public View mDragHandle;
        public ImageView devIcon;
        public TextView devName;
        public TextView devCate;

        public TextView headerName;

        public MyViewHolder(View v) {
            super(v);
            mContainer = (FrameLayout) v.findViewById(R.id.container);
            mDragHandle = v.findViewById(R.id.drag_handle);
            devIcon = (ImageView) v.findViewById(R.id.device_icon);
            devName = (TextView) v.findViewById(R.id.dev_name);
            devCate = (TextView) v.findViewById(R.id.dev_cate);
            headerName = (TextView) v.findViewById(R.id.header_name);
        }
    }

    public DraggableExampleItemAdapter(ExampleDataProvider dataProvider,Context context) {
        mProvider = dataProvider;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        this.imageLoader = new ImageLoader(requestQueue, new BitmapCache());
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return mProvider.getItem(position).getId();
    }

    @Override
    public int getItemViewType(int position) {
        return mProvider.getItem(position).getViewType();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate( viewType == 1 ? R.layout.list_item2_draggable : R.layout.list_item_draggable , parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final SimpleDeviceBean item = mProvider.getData(position);

        if(item.getId() == 0){
            holder.headerName.setText(context.getString(R.string.not_added_list));//最后一个
        } else if(item.getId() == mProvider.getCount() - 1){
            holder.headerName.setText(context.getString(R.string.added_list));//最后一个
        }else{
            holder.devCate.setText(item.devCate);
            holder.devName.setText(item.devName);
            imageLoader.get(item.logo, new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                    Bitmap bitmap = imageContainer.getBitmap();
                    if(null != bitmap){
                        holder.devIcon.setImageBitmap(bitmap);
                    }else{
                        holder.devIcon.setImageBitmap(null);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    holder.devIcon.setImageBitmap(null);
                }
            },160,160);
        }

        // set background resource (target view ID: container)
        final int dragState = holder.getDragStateFlags();

        if (((dragState & Draggable.STATE_FLAG_IS_UPDATED) != 0)) {
            int bgResId;

            if ((dragState & Draggable.STATE_FLAG_IS_ACTIVE) != 0) {
                bgResId = R.drawable.bg_item_dragging_active_state;

                // need to clear drawable state here to get correct appearance of the dragging item.
                DrawableUtils.clearState(holder.mContainer.getForeground());
            } else if ((dragState & Draggable.STATE_FLAG_DRAGGING) != 0) {
                bgResId = R.drawable.bg_item_dragging_state;
            } else {
                bgResId = R.drawable.bg_item_normal_state;
            }

            holder.mContainer.setBackgroundResource(bgResId);
        }
    }

    @Override
    public int getItemCount() {
        return mProvider.getCount();
    }

    @Override
    public void onMoveItem(int fromPosition, int toPosition) {
        Log.d(TAG, "onMoveItem(fromPosition = " + fromPosition + ", toPosition = " + toPosition + ")");

        if (fromPosition == toPosition) {
            return;
        }

        mProvider.moveItem(fromPosition, toPosition);

        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public boolean onCheckCanStartDrag(MyViewHolder holder, int position, int x, int y) {
        // x, y --- relative from the itemView's top-left
        final View containerView = holder.mContainer;
        final View dragHandleView = holder.mDragHandle;

        if(null == containerView || dragHandleView == null){
            return false;
        }

        final int offsetX = containerView.getLeft() + (int) (ViewCompat.getTranslationX(containerView) + 0.5f);
        final int offsetY = containerView.getTop() + (int) (ViewCompat.getTranslationY(containerView) + 0.5f);

        return ViewUtils.hitTest(dragHandleView, x - offsetX, y - offsetY);
    }

    @Override
    public ItemDraggableRange onGetItemDraggableRange(MyViewHolder holder, int position) {
        // no drag-sortable range specified
        return null;
    }

    @Override
    public boolean onCheckCanDrop(int draggingPosition, int dropPosition) {
        return true;
    }
}
