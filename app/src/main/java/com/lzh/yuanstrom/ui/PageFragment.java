package com.lzh.yuanstrom.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.adapter.FirstPageAdapter;
import com.lzh.yuanstrom.adapter.SecondPageAdapter;
import com.lzh.yuanstrom.bean.LocalDeviceBean;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.hekr.hekrsdk.action.HekrUser;
import me.hekr.hekrsdk.action.HekrUserAction;
import me.hekr.hekrsdk.bean.DeviceBean;
import me.hekr.hekrsdk.bean.GroupBean;
import me.hekr.hekrsdk.util.HekrCodeUtil;

public class PageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String ARG_PAGE = "ARG_PAGE";

    int mPage;

    FirstPageAdapter firstPageAdapter;
    SecondPageAdapter secondPageAdapter;

    HekrUserAction hekrUserAction;

    @BindView(R.id.contentView)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.empty_con)
    LinearLayout emptyCon;

    @BindView(R.id.hint_txt)
    TextView hintTxt;

    @BindView(R.id.reload)
    Button reload;

    @BindView(R.id.recycler)
    RecyclerView recyclerView;

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
        hekrUserAction = HekrUserAction.getInstance(getActivity());
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);

        ButterKnife.bind(this, view);

        if (mPage == 1) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(gridLayoutManager);
            firstPageAdapter = new FirstPageAdapter(getActivity());
            firstPageAdapter.setOnItemClickListener(new FirstPageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(LocalDeviceBean bean,View v) {
                    FirstPageAdapter.toWhatActivityByCateName(getActivity(), bean.categoryName, bean.devTid);
                }
            });
            firstPageAdapter.setOnItemLongClickListener(new FirstPageAdapter.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(LocalDeviceBean bean) {
                    firstPageAdapter.showDialog(getActivity(), bean.devTid);
                    return true;
                }
            });
            recyclerView.setAdapter(firstPageAdapter);

        } else if (mPage == 2) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            secondPageAdapter = new SecondPageAdapter(getActivity());
            recyclerView.setAdapter(secondPageAdapter);
        }

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyCon.setVisibility(View.GONE);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                onRefresh();
            }
        });

        if (mPage == 1) {
            onRefresh();
        }

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.destroyDrawingCache();
            mSwipeRefreshLayout.clearAnimation();
        }
    }

    /**
     * Clear callback on detach to prevent null reference errors after the view has been
     */
    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {
        if (mPage == 1) {
            mSwipeRefreshLayout.setRefreshing(true);
            getDevices();
        } else if (mPage == 2) {
            mSwipeRefreshLayout.setRefreshing(true);
            getGroup();
        }
    }

    public void getDevices() {
        hekrUserAction.getDevices(new HekrUser.GetDevicesListener() {
            @Override
            public void getDevicesSuccess(List<DeviceBean> list) {
                mSwipeRefreshLayout.setRefreshing(false);
                LocalDeviceBean.deletAll();
                for (DeviceBean deviceBean : list) {
                    LocalDeviceBean local = LocalDeviceBean.dev2Local(deviceBean);
                    local.saveNew();
                }
                if (null != list && list.size() > 0) {
                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                    emptyCon.setVisibility(View.GONE);
                    firstPageAdapter.setDevices(LocalDeviceBean.findALll());
                } else {
                    mSwipeRefreshLayout.setVisibility(View.GONE);
                    emptyCon.setVisibility(View.VISIBLE);
                    hintTxt.setText(getString(R.string.no_device));
                    firstPageAdapter.setDevices(new ArrayList<LocalDeviceBean>());
                }
            }

            @Override
            public void getDevicesFail(int i) {
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setVisibility(View.GONE);
                emptyCon.setVisibility(View.VISIBLE);
                hintTxt.setText(getString(R.string.load_failed) + HekrCodeUtil.errorCode2Msg(i));
                firstPageAdapter.setDevices(new ArrayList<LocalDeviceBean>());
            }
        });
    }

    public void getGroup() {
        hekrUserAction.getGroup(new HekrUser.GetGroupListener() {
            @Override
            public void getGroupSuccess(List<GroupBean> list) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (null != list && list.size() > 0) {
                    mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                    emptyCon.setVisibility(View.GONE);
                    secondPageAdapter.setGroups(list);
                } else {
                    mSwipeRefreshLayout.setVisibility(View.GONE);
                    emptyCon.setVisibility(View.VISIBLE);
                    hintTxt.setText(getString(R.string.no_group));
                    secondPageAdapter.setGroups(new ArrayList<GroupBean>());
                }
            }

            @Override
            public void getGroupFail(int i) {
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setVisibility(View.GONE);
                emptyCon.setVisibility(View.VISIBLE);
                hintTxt.setText(getString(R.string.load_failed) + HekrCodeUtil.errorCode2Msg(i));
                secondPageAdapter.setGroups(new ArrayList<GroupBean>());
            }
        });
    }
}
