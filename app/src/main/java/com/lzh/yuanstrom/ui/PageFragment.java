package com.lzh.yuanstrom.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.adapter.FirstPageAdapter;
import com.lzh.yuanstrom.adapter.SecondPageAdapter;
import com.lzh.yuanstrom.adapter.ThirdPageAdapter;
import com.lzh.yuanstrom.bean.CustomerBean;
import com.lzh.yuanstrom.bean.ExtraProperties;
import com.lzh.yuanstrom.bean.LocalDeviceBean;
import com.lzh.yuanstrom.bean.ProfileData;
import com.lzh.yuanstrom.common.GetCustomerListener;
import com.lzh.yuanstrom.utils.CommandHelper;
import com.lzh.yuanstrom.utils.StringUtils;
import com.lzh.yuanstrom.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.hekr.hekrsdk.action.HekrUser;
import me.hekr.hekrsdk.action.HekrUserAction;
import me.hekr.hekrsdk.bean.DeviceBean;
import me.hekr.hekrsdk.bean.GroupBean;
import me.hekr.hekrsdk.listener.DataReceiverListener;
import me.hekr.hekrsdk.util.HekrCodeUtil;
import me.hekr.hekrsdk.util.MsgUtil;

public class PageFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static final String ARG_PAGE = "ARG_PAGE";

    int mPage;

    FirstPageAdapter firstPageAdapter;
    SecondPageAdapter secondPageAdapter;
    ThirdPageAdapter thirdPageAdapter;

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

    private ProgressDialog progressHUD;

    private Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

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
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_page, container, false);

        ButterKnife.bind(this, view);

        hekrUserAction = HekrUserAction.getInstance(getActivity());

        if (mPage == 1) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(gridLayoutManager);
            firstPageAdapter = new FirstPageAdapter(getActivity());
            firstPageAdapter.setOnItemClickListener(new FirstPageAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(LocalDeviceBean bean, View v) {
                    if (!bean.online) {
                        ToastUtil.showMessage(context, context.getString(R.string.dev_offline));
                    }
                    FirstPageAdapter.toWhatActivityByCateName(context, bean.categoryName, bean.devTid);
                }
            });
            firstPageAdapter.setOnItemLongClickListener(new FirstPageAdapter.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(LocalDeviceBean bean) {
                    showDialog(context, bean.devTid);
                    return true;
                }
            });
            recyclerView.setAdapter(firstPageAdapter);

//        } else if (mPage == 2) {
//            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//            secondPageAdapter = new SecondPageAdapter(getActivity());
//            recyclerView.setAdapter(secondPageAdapter);
        } else if (mPage == 2) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            thirdPageAdapter = new ThirdPageAdapter(getActivity());
            thirdPageAdapter.setOnClickListener(new ThirdPageAdapter.OnClickListener() {
                @Override
                public void onClick(View v, ProfileData profileData) {
                    if (null != profileData && profileData.profileDatas != null) {
                        List<String> devTids = new ArrayList<>();
                        List<String> datas = new ArrayList<>();
                        for (String data : profileData.profileDatas) {
                            com.alibaba.fastjson.JSONObject jb = JSON.parseObject(data);
                            String devTid = jb.getJSONObject("params").getString("devTid");
                            devTids.add(devTid);
                            datas.add(data);
                        }
                        sendData(devTids, datas, 0);
                    }
                }
            });
            thirdPageAdapter.setOnLongClickListener(new ThirdPageAdapter.OnLongClickListener() {
                @Override
                public void onLongClick(View v, ProfileData profileData) {
                    showThirdDialog(context, profileData);
                }
            });
            recyclerView.setAdapter(thirdPageAdapter);
            if (null != getActivity()) {
                CustomerBean bean = ((MainActivity) getActivity()).getCustomerBean();
                if (bean != null && bean.getProfileDatas() != null && bean.profileDatas.size() > 0) {
                    emptyCon.setVisibility(View.GONE);
                    thirdPageAdapter.setDatas(bean.profileDatas);
                } else {
                    thirdPageAdapter.setDatas(new ArrayList<ProfileData>());
                    emptyCon.setVisibility(View.VISIBLE);
                    hintTxt.setText(context.getString(R.string.no_profiles));
                }
            }
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
//        } else if (mPage == 2) {
//            mSwipeRefreshLayout.setRefreshing(true);
//            getGroup();
        } else if (mPage == 2) {
            mSwipeRefreshLayout.setRefreshing(true);
            if (getActivity() != null) {
                ((MainActivity) getActivity()).getCustomerInfo(new GetCustomerListener() {
                    @Override
                    public void getSuccess(CustomerBean bean) {
                        if (getActivity() != null) {
                            ((MainActivity) getActivity()).setCustomerBean(bean);
                            if (bean != null && bean.profileDatas != null && bean.profileDatas.size() > 0) {
                                emptyCon.setVisibility(View.GONE);
                                thirdPageAdapter.setDatas(bean.profileDatas);
                            } else {
                                thirdPageAdapter.setDatas(new ArrayList<ProfileData>());
                                emptyCon.setVisibility(View.VISIBLE);
                                hintTxt.setText(context.getString(R.string.no_profiles));
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }

                    @Override
                    public void getFailed(int errCode) {
                        ToastUtil.showMessage(context, HekrCodeUtil.errorCode2Msg(errCode));
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }
    }

    private int renameNo = 0;
    private int maxRenameNumber = 0;

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
                Log.e("list.size()", "getDevicesSuccess--" + list.size());
                if (null != list && list.size() > 0) {
                    emptyCon.setVisibility(View.GONE);
                    firstPageAdapter.setDevices(LocalDeviceBean.findALll());
                    Log.e("getDevicesSuccess", "getDevicesSuccess--" + list.get(0).getDeviceName());
                    Iterator<DeviceBean> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        DeviceBean bean = iterator.next();
                        if (StringUtils.isNotBlank(bean.getDeviceName())) {
                            iterator.remove();
                        }
                    }
                    maxRenameNumber = list.size();
                    Log.e("maxRenameNumber", maxRenameNumber + "");
                    for (int i = 0; i < list.size(); i++) {
                        DeviceBean bean = list.get(i);
                        Log.e("bean.getDeviceName", bean.getDeviceName() + "");
                        if (StringUtils.isBlank(bean.getDeviceName())) {
                            String name;
                            if (LocalDeviceBean.isChinese()) {
                                name = bean.getProductName().getZh_CN();
                            } else {
                                name = bean.getProductName().getEn_US();
                            }
                            Log.e("renameDevice", "renameDevice");
                            renameDevice(bean.getDevTid(), bean.getCtrlKey(), name, bean.getDesc());
                        }
                    }
                } else {
                    emptyCon.setVisibility(View.VISIBLE);
                    hintTxt.setText(context.getString(R.string.no_device));
                    firstPageAdapter.setDevices(new ArrayList<LocalDeviceBean>());
                }
            }

            @Override
            public void getDevicesFail(int i) {
                mSwipeRefreshLayout.setRefreshing(false);
                emptyCon.setVisibility(View.VISIBLE);
                if (isAdded()) {
                    hintTxt.setText(context.getString(R.string.load_failed) + HekrCodeUtil.errorCode2Msg(i));
                }
                firstPageAdapter.setDevices(new ArrayList<LocalDeviceBean>());
            }
        });
    }

    private void renameDevice(String devTid, String ctrlKey, String name, String desc) {
        hekrUserAction.renameDevice(devTid, ctrlKey, name, desc, new HekrUser.RenameDeviceListener() {
            @Override
            public void renameDeviceSuccess() {
                renameNo++;
                Log.e("renameDeviceSuccess", renameNo + " " + maxRenameNumber);
                if (renameNo == maxRenameNumber) {
                    maxRenameNumber = 0;
                    renameNo = 0;
                    delayGetDevices();
                    Log.e("getDevices", "getDevices");
                }
            }

            @Override
            public void renameDeviceFail(int i) {
                renameNo++;
                Log.e("renameDeviceFail", renameNo + " " + maxRenameNumber);
                if (renameNo == maxRenameNumber) {
                    maxRenameNumber = 0;
                    renameNo = 0;
                    delayGetDevices();
                }
            }
        });
    }

    private void delayGetDevices() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getDevices();
                        }
                    });
                }
            }
        }, 2000);
    }

    public void getGroup() {
        hekrUserAction.getGroup(new HekrUser.GetGroupListener() {
            @Override
            public void getGroupSuccess(List<GroupBean> list) {
                mSwipeRefreshLayout.setRefreshing(false);
                if (null != list && list.size() > 0) {
                    emptyCon.setVisibility(View.GONE);
                    secondPageAdapter.setGroups(list);
                } else {
                    emptyCon.setVisibility(View.VISIBLE);
                    hintTxt.setText(context.getString(R.string.no_group));
                    secondPageAdapter.setGroups(new ArrayList<GroupBean>());
                }
            }

            @Override
            public void getGroupFail(int i) {
                mSwipeRefreshLayout.setRefreshing(false);
                emptyCon.setVisibility(View.VISIBLE);
                hintTxt.setText(context.getString(R.string.load_failed) + HekrCodeUtil.errorCode2Msg(i));
                secondPageAdapter.setGroups(new ArrayList<GroupBean>());
            }
        });
    }

    protected String detailData(String devTid, String data) {
        com.alibaba.fastjson.JSONObject jb = com.alibaba.fastjson.JSONObject.parseObject(data);
        String cmd = jb.getJSONObject("params").getJSONObject("data").getString("raw");
        String useful = cmd.substring(8, cmd.length() - 2);
        LocalDeviceBean local = LocalDeviceBean.findByTid(devTid);
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
                sb.append(context.getString(R.string.open));
            } else if (b == 2) {
                sb.append(context.getString(R.string.close));
            }
            sb.append(" \'").append(local.deviceName).append("\' ");
            int a = Integer.parseInt(secondCommand, 16);
            if (a == 1) {
                sb.append(context.getString(R.string.first_jack));
            } else if (a == 2) {
                sb.append(context.getString(R.string.second_jack));
            } else if (a == 3) {
                sb.append(context.getString(R.string.third_jack));
            } else if (a == 4) {
                sb.append(context.getString(R.string.fourth_jack));
            }
            return sb.toString();
        }
        return "";
    }

    public void sendData(final List<String> devTids, final List<String> commands, final int index) {
        try {
            final int size = devTids.size();
            final String action = detailData(devTids.get(index), commands.get(index));
            if (index == 0) {
                progressHUD = new ProgressDialog(context);
                progressHUD.setMessage(context.getString(R.string.now) + " " + action);
                progressHUD.setTitle("");
                progressHUD.setCanceledOnTouchOutside(false);
                if (getActivity() != null && !getActivity().isFinishing() && !getActivity().isFinishing()) {
                    progressHUD.show();
                }
            } else {
                progressHUD.setMessage(context.getString(R.string.now) + " " + action);
            }
            if (null != getActivity()) {
                MsgUtil.sendMsg(getActivity(), devTids.get(index), new JSONObject(commands.get(index)), new DataReceiverListener() {
                    @Override
                    public void onReceiveSuccess(String s) {
                        Log.e("onReceiveSuccess", s);
                        ToastUtil.showMessage(getActivity(), action + " " + context.getString(R.string.send_suc));
                        if (index < size - 1) {
                            int b = index + 1;
                            sendData(devTids, commands, b);
                        } else {
                            progressHUD.dismiss();
                        }
                    }

                    @Override
                    public void onReceiveTimeout() {
                        Log.e("onReceiveSuccess", "request failed");
                        ToastUtil.showMessage(getActivity(), action + " " + context.getString(R.string.send_failed));
                        if (index < size - 1) {
                            int b = index + 1;
                            sendData(devTids, commands, b);
                        } else {
                            progressHUD.dismiss();
                        }
                    }
                }, false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private CharSequence[] items;

    public void showThirdDialog(final Context context, final ProfileData profileData) {
        items = new CharSequence[]{context.getString(R.string.delete), context.getString(R.string.cancel)};
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.choice_method))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (items[i].equals(context.getString(R.string.delete))) {
                            dialog.dismiss();
                            showDeleteProfileDialog(profileData);
                        } else if (items[i].equals(context.getString(R.string.cancel))) {
                            dialog.dismiss();
                        }
                    }
                })
                .create();
        dialog.show();
    }

    private void showDeleteProfileDialog(final ProfileData profileData) {
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.hint))
                .setMessage(context.getString(R.string.sure_delete_profile))
                .setPositiveButton(context.getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<ProfileData> datas = thirdPageAdapter.getDatas();
                        if (datas != null && datas.size() > 0) {
                            Iterator<ProfileData> iterator = datas.iterator();
                            while (iterator.hasNext()) {
                                ProfileData data = iterator.next();
                                if (data.createTime == profileData.createTime) {
                                    iterator.remove();
                                    break;
                                }
                            }
                        }
                        if (null != getActivity()) {
                            CustomerBean customer = ((MainActivity) getActivity()).getCustomerBean();
                            if (null != customer) {
                                ExtraProperties extraProperties = new ExtraProperties();
                                extraProperties.age = customer.getAge();
                                extraProperties.profileDatas = datas;
                                customer.setExtraProperties(extraProperties);
                            }
                            ((MainActivity) getActivity()).uploadCustomer();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    public void showDialog(final Context context, final String devTid) {
        items = new CharSequence[]{context.getString(R.string.set), context.getString(R.string.delete), context.getString(R.string.cancel)};
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.choice_method))
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (items[i].equals(context.getString(R.string.cancel))) {
                            dialog.dismiss();
                        } else if (items[i].equals(context.getString(R.string.delete))) {
                            dialog.dismiss();
                            showSureDialog(devTid);
                        } else if (items[i].equals(context.getString(R.string.set))) {
                            //TODO to setting
                            Intent intent = new Intent(context, DevControlActivity.class);
                            intent.putExtra("devTid", devTid);
                            context.startActivity(intent);
                        }
                    }
                })
                .create();
        dialog.show();
    }

    private void showSureDialog(final String devTid) {
        final LocalDeviceBean local = LocalDeviceBean.findByTid(devTid);
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.hint))
                .setMessage(context.getString(R.string.sure_delete))
                .setPositiveButton(context.getString(R.string.ensure), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        //TODO delete device
                        hekrUserAction.deleteDevice(devTid, local.bindKey, new HekrUser.DeleteDeviceListener() {
                            @Override
                            public void deleteDeviceSuccess() {
                                ToastUtil.showMessage(context, context.getString(R.string.delete_dev_suc));
                                getDevices();
                            }

                            @Override
                            public void deleteDeviceFail(int i) {
                                ToastUtil.showMessage(context, HekrCodeUtil.errorCode2Msg(i));
                            }
                        });
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }
}
