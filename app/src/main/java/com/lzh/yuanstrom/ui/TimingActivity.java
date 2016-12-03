package com.lzh.yuanstrom.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.litesuits.android.log.Log;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.adapter.TimingAdapter;
import com.lzh.yuanstrom.bean.LocalDeviceBean;
import com.lzh.yuanstrom.bean.TimingBean;
import com.lzh.yuanstrom.utils.CommandHelper;
import com.lzh.yuanstrom.utils.FullCommandHelper;
import com.lzh.yuanstrom.utils.HekrCodeUtil;
import com.lzh.yuanstrom.utils.ToastUtil;
import com.lzh.yuanstrom.widget.CusBottomSheetDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.hekr.hekrsdk.action.HekrUserAction;
import me.hekr.hekrsdk.util.ConstantsUtil;

/**
 * Created by Administrator on 2016/11/23.
 */

public class TimingActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;

    @BindView(R.id.empty_con)
    LinearLayout emptyCon;

    @BindView(R.id.hint_txt)
    TextView hint_txt;

    private TimingAdapter adapter;

    private int year, monthOfYear, dayOfMonth, hourOfDay, minute;

    private String devTid;
    private String ctrlKey;

    private boolean isRepeat;

    private List<String> repeatArr;

    private String date;
    private String time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timing);
        ButterKnife.bind(this);

        setCanBackToolbar(getString(R.string.timing_task));

        devTid = getIntent().getStringExtra("devTid");
        ctrlKey = getIntent().getStringExtra("ctrlKey");

        adapter = new TimingAdapter(this, devTid, ctrlKey);
        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTimes();
            }
        });

        initCalendar();

        swipeLayout.setOnRefreshListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        onRefresh();
    }

    private void initCalendar() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        monthOfYear = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }

    /**
     * 选择次数
     */
    private void selectTimes() {
        View v = LayoutInflater.from(this).inflate(R.layout.once_or_repeat_layout, null);
        final RadioButton once = (RadioButton) v.findViewById(R.id.once);
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.once_or_repeat))
                .setView(v)
                .setPositiveButton(getString(R.string.next_step), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (once.isChecked()) {
                            isRepeat = false;
                            selectDate();
                        } else {
                            isRepeat = true;
                            selectDate();
                        }
                    }
                })
                .show();
    }

    private void selectDate() {
        if (isRepeat) {
            View v = LayoutInflater.from(this).inflate(R.layout.select_repeat_layout, null);
            final CheckBox sunday = (CheckBox) v.findViewById(R.id.sunday);
            final CheckBox monday = (CheckBox) v.findViewById(R.id.monday);
            final CheckBox tuesday = (CheckBox) v.findViewById(R.id.tuesday);
            final CheckBox wednesday = (CheckBox) v.findViewById(R.id.wednesday);
            final CheckBox thursday = (CheckBox) v.findViewById(R.id.thursday);
            final CheckBox friday = (CheckBox) v.findViewById(R.id.friday);
            final CheckBox saturday = (CheckBox) v.findViewById(R.id.saturday);
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.select_repeat_day))
                    .setView(v)
                    .setPositiveButton(getString(R.string.next_step), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            if (!sunday.isChecked() && !monday.isChecked() && !tuesday.isChecked() && !wednesday.isChecked() && !thursday.isChecked() && !friday.isChecked() && !saturday.isChecked()) {
                                ToastUtil.showMessage(context, getString(R.string.must_have_one));
                                return;
                            }
                            repeatArr = new ArrayList<>();
                            if (sunday.isChecked()) {
                                repeatArr.add("SUNDAY");
                            }
                            if (monday.isChecked()) {
                                repeatArr.add("MONDAY");
                            }
                            if (tuesday.isChecked()) {
                                repeatArr.add("TUESDAY");
                            }
                            if (wednesday.isChecked()) {
                                repeatArr.add("WEDNESDAY");
                            }
                            if (thursday.isChecked()) {
                                repeatArr.add("THURSDAY");
                            }
                            if (friday.isChecked()) {
                                repeatArr.add("FRIDAY");
                            }
                            if (saturday.isChecked()) {
                                repeatArr.add("SATURDAY");
                            }
                            selectTime();
                        }

                    })
                    .show();
        } else

        {
            DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                boolean mFired = false;
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    if (mFired == true) {
                        return;
                    } else {
                        mFired = true;
                    }
                    Log.e("date", i + "   " + i1 + "    " + i2);
                    date = i + "-" + (i1 + 1) + "-" + i2;
                    selectTime();
                }
            }, year, monthOfYear, dayOfMonth);
            dialog.setTitle(getString(R.string.please_date));
            dialog.show();
        }

    }

    /**
     * 选择时间
     */
    private void selectTime() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            boolean mFired = false;
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                if (mFired == true) {
                    return;
                } else {
                    mFired = true;
                }
                Log.e("timePicker", i + "      " + i1);
                String hour = "";
                String minute = "";
                if (i < 10) {
                    hour += "0" + i;
                } else {
                    hour += i;
                }
                if (i1 < 10) {
                    minute += "0" + i1;
                } else {
                    minute += i1;
                }
                time = hour + ":" + minute + ":" + "00";
                showChazuoDialog(LocalDeviceBean.findByTid(devTid));
            }
        }, hourOfDay, minute, true);
        timePickerDialog.setTitle(getString(R.string.please_time));
        timePickerDialog.show();
    }

    private void addOnceTask(String devTid, String ctrlKey, String fullCommand, String date, String time) {
        CharSequence url = TextUtils.concat(new CharSequence[]{ConstantsUtil.UrlUtil.BASE_USER_URL, "rule/schedulerTask"});
        Map<String, Object> map = new HashMap<>();
        map.put("raw", fullCommand);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("devTid", devTid);
        jsonObject.put("ctrlKey", ctrlKey);
        jsonObject.put("code", new JSONObject(map));
        jsonObject.put("taskName", "One time reservation");
        jsonObject.put("schedulerType", "ONCE");
        jsonObject.put("timeZoneOffset", 480);
        jsonObject.put("enable", true);
        jsonObject.put("taskKey", System.currentTimeMillis());
        jsonObject.put("desc", "");
        jsonObject.put("isForce", false);
        jsonObject.put("triggerDateTime", date + "T" + time); // 2016-01-07T12:00:00

        Log.e("url", url);
        Log.e("json", jsonObject.toString());

        hekrUserAction.postHekrData(url, jsonObject.toString(), new HekrUserAction.GetHekrDataListener() {
            @Override
            public void getSuccess(Object o) {
                String a = o.toString();
                getTasks();
                Log.e("return-data", a);
            }

            @Override
            public void getFail(int i) {
                ToastUtil.showMessage(context, HekrCodeUtil.errorCode2Msg(context, i));
            }
        });
    }

    private void addRepeatTask(String devTid, String ctrlKey, String fullCommand, List<String> repeatList, String time) {
        String[] repeatArray = new String[repeatList.size()];
        for (int i = 0; i < repeatList.size(); i++) {
            repeatArray[i] = repeatList.get(i);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("raw", fullCommand);
        CharSequence url = TextUtils.concat(new CharSequence[]{ConstantsUtil.UrlUtil.BASE_USER_URL, "rule/schedulerTask"});
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("devTid", devTid);
        jsonObject.put("ctrlKey", ctrlKey);
        jsonObject.put("code", map);
        jsonObject.put("taskName", "Repeat reservation");
        jsonObject.put("schedulerType", "LOOP");
        jsonObject.put("timeZoneOffset", 480);
        jsonObject.put("taskKey", System.currentTimeMillis());
        jsonObject.put("desc", "");
        jsonObject.put("enable", true);
        jsonObject.put("isForce", false);
        jsonObject.put("triggerTime", time);
        jsonObject.put("repeatList", repeatArray);

        Log.e("url", url);
        Log.e("json", jsonObject.toString());

        hekrUserAction.postHekrData(url, jsonObject.toString(), new HekrUserAction.GetHekrDataListener() {
            @Override
            public void getSuccess(Object o) {
                String a = o.toString();
                getTasks();
                Log.e("return-data", a);
            }

            @Override
            public void getFail(int i) {
                ToastUtil.showMessage(context, HekrCodeUtil.errorCode2Msg(context, i));
            }
        });
    }

    public void getTasks() {
        CharSequence url = TextUtils.concat(new CharSequence[]{ConstantsUtil.UrlUtil.BASE_USER_URL, "rule/schedulerTask?devTid=" + devTid + "&ctrlKey=" + ctrlKey});
        hekrUserAction.getHekrData(url, new HekrUserAction.GetHekrDataListener() {
            @Override
            public void getSuccess(Object o) {
                Log.e("return-data",o.toString());
                swipeLayout.setRefreshing(false);
                JSONArray ja = JSONArray.parseArray(o.toString());
                List<TimingBean> beans = new ArrayList<>();
                for (Object o1 : ja) {
                    TimingBean bean = JSONObject.parseObject(o1.toString(), TimingBean.class);
                    bean.needShowChange = false;
                    beans.add(bean);
                }
                adapter.setBeans(beans);
                if (beans.size() == 0) {
                    emptyCon.setVisibility(View.VISIBLE);
                } else {
                    emptyCon.setVisibility(View.GONE);
                }
            }

            @Override
            public void getFail(int i) {
                swipeLayout.setRefreshing(false);
                emptyCon.setVisibility(View.VISIBLE);
                hint_txt.setText(HekrCodeUtil.errorCode2Msg(context, i));
            }
        });
    }

    private void showChazuoDialog(final LocalDeviceBean bean) {
        final CusBottomSheetDialog dialog = new CusBottomSheetDialog(this);
        View root = getLayoutInflater().inflate(R.layout.chazuo_bottom_layout, null);
        final RadioButton c1_Open = (RadioButton) root.findViewById(R.id.chazuo_1_open);
        final RadioButton c1_Close = (RadioButton) root.findViewById(R.id.chazuo_1_close);
        final RadioButton c2_Open = (RadioButton) root.findViewById(R.id.chazuo_2_open);
        final RadioButton c2_Close = (RadioButton) root.findViewById(R.id.chazuo_2_close);
        final RadioButton c3_Open = (RadioButton) root.findViewById(R.id.chazuo_3_open);
        final RadioButton c3_Close = (RadioButton) root.findViewById(R.id.chazuo_3_close);
        final RadioButton c4_Open = (RadioButton) root.findViewById(R.id.chazuo_4_open);
        final RadioButton c4_Close = (RadioButton) root.findViewById(R.id.chazuo_4_close);
        TextView cancel = (TextView) root.findViewById(R.id.cancel);
        TextView ensure = (TextView) root.findViewById(R.id.ensure);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        ensure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!c1_Open.isChecked() && !c1_Close.isChecked() && !c2_Open.isChecked() && !c2_Close.isChecked() && !c3_Open.isChecked() && !c3_Close.isChecked() && !c4_Open.isChecked() && !c4_Close.isChecked()) {
                    ToastUtil.showMessage(TimingActivity.this, getString(R.string.at_least_one_action));
                    return;
                }
                String firstCommand = CommandHelper.TIMING_COMMAND;
                String secondCommand;
                String thirdCommand;
                int a = 0;
                int b = 0;
                if (c1_Open.isChecked() || c1_Close.isChecked()) {
                    a += 1;
                    if (c1_Open.isChecked()) {
                        b += 1;
                    }
                }
                if (c2_Open.isChecked() || c2_Close.isChecked()) {
                    a += 2;
                    if (c2_Open.isChecked()) {
                        b += 2;
                    }
                }
                if (c3_Open.isChecked() || c3_Close.isChecked()) {
                    a += 4;
                    if (c3_Open.isChecked()) {
                        b += 4;
                    }
                }
                if (c4_Open.isChecked() || c4_Close.isChecked()) {
                    a += 8;
                    if (c4_Open.isChecked()) {
                        b += 8;
                    }
                }
                secondCommand = "0" + Integer.toHexString(a);
                thirdCommand = "0" + Integer.toHexString(b);
                CommandHelper commandHelper = new CommandHelper.CommandBuilder().setFirstCommand(firstCommand).setSecondCommand(secondCommand).setThirdCommand(thirdCommand).build();
                dialog.dismiss();
                createTiming(commandHelper.toString());
            }
        });
        dialog.setContentView(root);
        dialog.show();
    }

    private void createTiming(String cmd) {
        if (isRepeat) {
            addRepeatTask(devTid, ctrlKey, cmd, repeatArr, time);
        } else {
            addOnceTask(devTid, ctrlKey, cmd, date, time);
        }
    }

    @Override
    public void onRefresh() {
        swipeLayout.setRefreshing(true);
        getTasks();
    }
}
