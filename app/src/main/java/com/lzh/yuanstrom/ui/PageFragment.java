package com.lzh.yuanstrom.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.adapter.FirstPageAdapter;
import com.lzh.yuanstrom.adapter.SecondPageAdapter;
import com.lzh.yuanstrom.bean.GroupBean;

import java.util.ArrayList;
import java.util.List;

import me.hekr.hekrsdk.bean.DeviceBean;

/**
 * Created by chris.black on 6/11/15.
 */
public class PageFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int mPage;

    public static PageFragment create(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler);

        List<GroupBean> groups = new ArrayList<>();
        List<DeviceBean> devices = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            DeviceBean device = new DeviceBean();
            device.setDeviceName("设备" + i);
            if (i % 2 == 0) {
                device.setOnline(true);
            } else {
                device.setOnline(false);
            }
            device.setCid("cid" + i);
            devices.add(device);
        }

        for (int i = 1; i <= 4; i++) {
            GroupBean group = new GroupBean();
            group.groupName = "种类" + i;
            group.devices = new ArrayList<>();
            for (int i1 = 1; i1 <= 5; i1++) {
                int index = i*i1;
                if(index == 20){
                    index = 0;
                }
                group.devices.add(devices.get(index));
            }
            groups.add(group);
        }

        if (mPage == 1) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),2,LinearLayoutManager.VERTICAL,false);
            recyclerView.setLayoutManager(gridLayoutManager);
            FirstPageAdapter adapter = new FirstPageAdapter(mPage, getActivity());
            recyclerView.setAdapter(adapter);

            adapter.setDevices(devices);
        } else if (mPage == 2) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            SecondPageAdapter adapter = new SecondPageAdapter(getActivity());
            recyclerView.setAdapter(adapter);

            adapter.setGroups(groups);
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.contentView);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });
        mSwipeRefreshLayout.setEnabled(false);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(false);
            mSwipeRefreshLayout.destroyDrawingCache();
            mSwipeRefreshLayout.clearAnimation();
        }
    }

    public void setSwipeToRefreshEnabled(boolean enabled) {
        mSwipeRefreshLayout.setEnabled(enabled);
    }

    /**
     * Clear callback on detach to prevent null reference errors after the view has been
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }
}
