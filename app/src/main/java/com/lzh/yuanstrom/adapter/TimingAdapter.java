package com.lzh.yuanstrom.adapter;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.alibaba.fastjson.JSONObject;
import com.litesuits.android.log.Log;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.bean.LocalDeviceBean;
import com.lzh.yuanstrom.bean.TimingBean;
import com.lzh.yuanstrom.ui.TimingActivity;
import com.lzh.yuanstrom.utils.CommandHelper;
import com.lzh.yuanstrom.utils.HekrCodeUtil;
import com.lzh.yuanstrom.utils.StringUtils;
import com.lzh.yuanstrom.utils.ToastUtil;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import me.hekr.hekrsdk.action.HekrUserAction;
import me.hekr.hekrsdk.util.ConstantsUtil;

/**
 * Created by Administrator on 2016/11/23.
 */

public class TimingAdapter extends RecyclerView.Adapter<TimingAdapter.ViewHolder> {


    private List<TimingBean> beans;

    private TimingActivity context;

    private String devTid;
    private String ctrlKey;

    public TimingAdapter(TimingActivity context, String devTid, String ctrlKey) {
        this.context = context;
        beans = new ArrayList<>();
        this.devTid = devTid;
        this.ctrlKey = ctrlKey;
    }

    public void setBeans(List<TimingBean> beans) {
        this.beans = beans;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }

    @Override
    public TimingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timing, parent, false);
        return new TimingAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final TimingAdapter.ViewHolder holder, int position) {

        initExpand(holder);
        initView(holder, position);
    }


    private void initView(final ViewHolder holder, final int position) {
        TimingBean bean = beans.get(position);
        holder.monday.setChecked(false);
        holder.tuesday.setChecked(false);
        holder.wednesday.setChecked(false);
        holder.thursday.setChecked(false);
        holder.friday.setChecked(false);
        holder.saturday.setChecked(false);
        holder.sunday.setChecked(false);
        if (bean != null) {
            if (bean.needShowChange) {
                holder.sureChange.setVisibility(View.VISIBLE);
            } else {
                holder.sureChange.setVisibility(View.INVISIBLE);
            }
            holder.stateSwitch.setChecked(bean.enable);
            holder.taskContent.setText(detailData(bean.code.raw));
            String date = "";
            String time = "";
            String repeat = "";
            String noon = "";
            String cronExpr = bean.cronExpr;
            Log.e("cronExpr", cronExpr);
            String[] a = cronExpr.split(" ");
            String minute = a[1];
            String hour = a[2];
            String day = a[3];
            String month = a[4];
            String weeks = a[5];
            String year = a[6];

            Log.e("weeksa", weeks + "zzzz");

            Integer hourInt = Integer.parseInt(hour);
            if (hourInt <= 12) {
                noon = context.getString(R.string.morning);
            } else {
                hourInt = hourInt - 12;
                noon = context.getString(R.string.afternoon);
            }
            time = (hourInt > 10 ? hourInt.toString() : "0" + hourInt.toString()) + ":" + (Integer.parseInt(minute) > 10 ? minute : "0" + minute);
            holder.timeNoon.setText(noon);
            holder.hourMinute.setText(time);
            View.OnClickListener zz = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.sureChange.setVisibility(View.VISIBLE);
                    beans.get(position).cronExpr = cronExpr(holder);
                    beans.get(position).needShowChange = true;
                    if (StringUtils.isBlank(beans.get(position).cronExpr)) {
                        holder.sureChange.setVisibility(View.INVISIBLE);
                        beans.get(position).needShowChange = false;
                    }
                }
            };
            holder.stateSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimingBean bean = beans.get(position);
                    if (!holder.stateSwitch.isChecked()) {
                        Log.e("tag", "true");
                        beans.get(position).enable = true;
                        beans.get(position).needShowChange = true;
                        holder.sureChange.setVisibility(View.VISIBLE);
                        if (StringUtils.isBlank(beans.get(position).cronExpr)) {
                            holder.sureChange.setVisibility(View.INVISIBLE);
                            beans.get(position).needShowChange = false;
                        }
                    } else {
                        beans.get(position).enable = false;
                        beans.get(position).needShowChange = true;
                        Log.e("tag", "false");
                        holder.sureChange.setVisibility(View.VISIBLE);
                        if (StringUtils.isBlank(beans.get(position).cronExpr)) {
                            holder.sureChange.setVisibility(View.INVISIBLE);
                            beans.get(position).needShowChange = false;
                        }
                    }
                }
            });
            holder.changeDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickDialog(holder, position);
                }
            });

            holder.sureChange.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimingBean bean = beans.get(position);
                    changeItem(bean, devTid, ctrlKey, "changed task", holder.stateSwitch.isChecked(), bean.taskKey, bean.cronExpr);
                }
            });
            holder.hourMinute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String hourMinute = holder.hourMinute.getText().toString();
                    int hour = Integer.parseInt(hourMinute.split(":")[0]);
                    int minute = Integer.parseInt(hourMinute.split(":")[1]);
                    if (holder.timeNoon.getText().toString().equals(context.getString(R.string.afternoon))) {
                        hour += 12;
                    }
                    selectTime(holder, hour, minute, position);
                }
            });
            holder.deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimingBean bean = beans.get(position);
                    deleteItem(bean.taskId, devTid, ctrlKey);
                }
            });
            if (bean.schedulerType.equals("ONCE")) {
                holder.repeatCheckbox.setChecked(false);
                holder.weekCon.setVisibility(View.INVISIBLE);
                holder.changeDate.setVisibility(View.VISIBLE);
                date = year + "-" + month + "-" + day;
                holder.changeDate.setText(date);
                Log.e("weeks3", weeks);
            } else {
                holder.repeatCheckbox.setChecked(true);
                holder.weekCon.setVisibility(View.VISIBLE);
                holder.changeDate.setVisibility(View.INVISIBLE);
                String[] b = weeks.split(",");
                Log.e("weeks0", weeks);
                for (int i = 0; i < b.length; i++) {
                    Log.e("aaaaa",b[i]);
                     if (b[i].equals("1")) {
                        holder.monday.setChecked(true);
                    } else if (b[i].equals("2")) {
                        holder.tuesday.setChecked(true);
                    } else if (b[i].equals("3")) {
                        holder.wednesday.setChecked(true);
                    } else if (b[i].equals("4")) {
                        holder.thursday.setChecked(true);
                    } else if (b[i].equals("5")) {
                        holder.friday.setChecked(true);
                    } else if (b[i].equals("6")) {
                        holder.saturday.setChecked(true);
                    } else if (b[i].equals("7")) {
                        holder.sunday.setChecked(true);
                    }
                }
                holder.monday.setOnClickListener(zz);
                holder.tuesday.setOnClickListener(zz);
                holder.wednesday.setOnClickListener(zz);
                holder.thursday.setOnClickListener(zz);
                holder.friday.setOnClickListener(zz);
                holder.saturday.setOnClickListener(zz);
                holder.sunday.setOnClickListener(zz);
            }
            if (bean.expired) {
                holder.outDate.setVisibility(View.VISIBLE);
            } else {
                holder.outDate.setVisibility(View.GONE);
            }
        }
    }

    public String detailData(String data) {
        Log.e("data", data);
        String useful = data.substring(8, data.length() - 2);
        LocalDeviceBean local = LocalDeviceBean.findByTid(devTid);
        if (useful.length() < 10) {
            return "";
        }
        String firstCommand = useful.substring(0, 2);
        String secondCommand = useful.substring(2, 4);
        String thirdCommand = useful.substring(4, 6);
        if (firstCommand.equals(CommandHelper.TIMING_COMMAND)) {
            int x = Integer.parseInt(secondCommand, 16);
            List<String> wei = new ArrayList<>();
            if ((x & 1) == 1) {
                wei.add(context.getString(R.string.first_jack));
            } else {
                wei.add("");
            }
            if ((x & 2) == 2) {
                wei.add(context.getString(R.string.second_jack));
            } else {
                wei.add("");
            }
            if ((x & 4) == 4) {
                wei.add(context.getString(R.string.third_jack));
            } else {
                wei.add("");
            }
            if ((x & 8) == 8) {
                wei.add(context.getString(R.string.third_jack));
            } else {
                wei.add("");
            }

            int y = Integer.parseInt(thirdCommand, 16);
            List<String> control = new ArrayList<>();
            if ((y & 1) == 1) {
                control.add(context.getString(R.string.open));
            } else {
                control.add(context.getString(R.string.close));
            }
            if ((y & 2) == 2) {
                control.add(context.getString(R.string.open));
            } else {
                control.add(context.getString(R.string.close));
            }
            if ((y & 4) == 4) {
                control.add(context.getString(R.string.open));
            } else {
                control.add(context.getString(R.string.close));
            }
            if ((y & 8) == 8) {
                control.add(context.getString(R.string.open));
            } else {
                control.add(context.getString(R.string.close));
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < wei.size(); i++) {
                String a = wei.get(i);
                String b = control.get(i);
                if (StringUtils.isNotBlank(a)) {
                    sb.append(b);
                    sb.append(" \'").append(local.deviceName).append("\' ");
                    sb.append(a);
                    sb.append("\n");
                }
            }
            return sb.toString();
        }
        return "";
    }

    private String cronExpr(ViewHolder holder) {
        String seconds = "0";
        String hourMinute = holder.hourMinute.getText().toString();
        String minute = hourMinute.split(":")[1];
        String hour;
        String day;
        String month;
        String weeks = "";
        String year;
        if (holder.timeNoon.getText().toString().equals(context.getString(R.string.morning))) {
            hour = hourMinute.split(":")[0];
        } else {
            hour = String.valueOf(Integer.parseInt(hourMinute.split(":")[0]) + 12);
        }
        if (holder.repeatCheckbox.isChecked()) {
            day = "?";
            month = "*";
            if (holder.monday.isChecked()) {
                weeks += 1 + ",";
            }
            if (holder.tuesday.isChecked()) {
                weeks += 2 + ",";
            }
            if (holder.wednesday.isChecked()) {
                weeks += 3 + ",";
            }
            if (holder.thursday.isChecked()) {
                weeks += 4 + ",";
            }
            if (holder.friday.isChecked()) {
                weeks += 5 + ",";
            }
            if (holder.saturday.isChecked()) {
                weeks += 6 + ",";
            }
            if (holder.sunday.isChecked()) {
                weeks += 7 + ",";
            }
            Log.e("weeks-1", weeks);
            if (weeks.length() > 1) {
                weeks = weeks.substring(0, weeks.length() - 1);
                Log.e("weeks-2", weeks);
            } else {
                ToastUtil.showMessage(context, context.getString(R.string.must_have_one));
                return "";
            }
            year = "*";
        } else {
            String date = holder.changeDate.getText().toString();
            day = date.split("-")[2];
            month = date.split("-")[1];
            weeks = "?";
            year = date.split("-")[0];
        }
        String cronExpr = seconds + " " + minute + " " + hour + " " + day + " " + month + " " + weeks + " " + year;
        return cronExpr;
    }

    /**
     * 选择时间
     */
    private void selectTime(final ViewHolder holder, int hour, int minute, final int pos) {

        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                Log.e("timePicker", i + "      " + i1);
                if (i > 12) {
                    i = i - 12;
                    holder.timeNoon.setText(context.getString(R.string.afternoon));
                } else {
                    holder.timeNoon.setText(context.getString(R.string.morning));
                }
                holder.hourMinute.setText((i < 10 ? "0" + i : i) + ":" + (i1 < 10 ? "0" + i1 : i1));
                holder.sureChange.setVisibility(View.VISIBLE);
                beans.get(pos).needShowChange = true;
                beans.get(pos).cronExpr = cronExpr(holder);
                if (StringUtils.isBlank(beans.get(pos).cronExpr)) {
                    holder.sureChange.setVisibility(View.INVISIBLE);
                    beans.get(pos).needShowChange = false;
                }
            }
        }, hour, minute, true);
        timePickerDialog.setTitle(context.getString(R.string.please_time));
        timePickerDialog.show();
    }

    private void showDatePickDialog(final ViewHolder holder, final int pos) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                Log.e("date", i + "   " + i1 + "    " + i2);
                String date = i + "-" + (i1 + 1) + "-" + i2;
                holder.changeDate.setText(date);
                holder.sureChange.setVisibility(View.VISIBLE);
                beans.get(pos).needShowChange = true;
                beans.get(pos).cronExpr = cronExpr(holder);
                if (StringUtils.isBlank(beans.get(pos).cronExpr)) {
                    holder.sureChange.setVisibility(View.INVISIBLE);
                    beans.get(pos).needShowChange = false;
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    public void changeItem(TimingBean bean, String devTid, String ctrlKey, String taskName, boolean enable, String taskKey, String cronExpr) {
        String[] a = cronExpr.split(" ");
        String minute = Integer.parseInt(a[1]) > 10 ? a[1] : "0" + Integer.parseInt(a[1]);
        String hour = Integer.parseInt(a[2]) > 10 ? a[2] : "0" + Integer.parseInt(a[2]);
        String day = a[3];
        String month = a[4];
        String weeks = a[5];
        Log.e("weeks", weeks);
        String year = a[6];
        if (bean.schedulerType.equals("ONCE")) {
            bean.triggerDateTime = year + "-" + month + "-" + day + "T" + hour + ":" + minute + ":" + "00";
        } else if (bean.schedulerType.equals("LOOP")) {
            bean.triggerTime = hour + ":" + minute;
            String[] w = weeks.split(",");
            List<String> ls = new ArrayList<>();
            for (int i = 0; i < w.length; i++) {
                String s = w[i];
                if (s.equals("7")) {
                    ls.add("SUNDAY");
                } else if (s.equals("1")) {
                    ls.add("MONDAY");
                } else if (s.equals("2")) {
                    ls.add("TUESDAY");
                } else if (s.equals("3")) {
                    ls.add("WEDNESDAY");
                } else if (s.equals("4")) {
                    ls.add("THURSDAY");
                } else if (s.equals("5")) {
                    ls.add("FRIDAY");
                } else if (s.equals("6")) {
                    ls.add("SATURDAY");
                }
            }
            bean.repeat = ls;
        }

        context.showLoading(false);
        CharSequence url = TextUtils.concat(new CharSequence[]{ConstantsUtil.UrlUtil.BASE_USER_URL, "rule/schedulerTask?taskId=" + bean.taskId + "&devTid=" + devTid + "&ctrlKey=" + ctrlKey});
        Map<String, Object> map = new HashMap<>();
        map.put("raw", bean.code.raw);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", new JSONObject(map));
        jsonObject.put("taskName", taskName);
        jsonObject.put("enable", enable);
        jsonObject.put("taskKey", taskKey);
        if (bean.schedulerType.equals("ONCE")) {
            jsonObject.put("triggerDateTime", bean.triggerDateTime);
        } else if (bean.schedulerType.equals("LOOP")) {
            jsonObject.put("triggerTime", bean.triggerTime);
            jsonObject.put("repeat", bean.repeat);
        }

        Log.e("url", url);
        Log.e("json", jsonObject.toString());

        context.hekrUserAction.putHekrData(url, jsonObject.toString(), new HekrUserAction.GetHekrDataListener() {
            @Override
            public void getSuccess(Object o) {
                context.hideLoading();
                ToastUtil.showMessage(context, context.getString(R.string.change_suc));
                context.onRefresh();
            }

            @Override
            public void getFail(int i) {
                context.hideLoading();
                ToastUtil.showMessage(context, HekrCodeUtil.errorCode2Msg(context, i));
            }
        });
    }

    public void deleteItem(long taskId, String devTid, String ctrlKey) {
        context.showLoading(false);
        CharSequence url = TextUtils.concat(new CharSequence[]{ConstantsUtil.UrlUtil.BASE_USER_URL, "rule/schedulerTask?taskId=" + taskId + "&devTid=" + devTid + "&ctrlKey=" + ctrlKey});
        context.hekrUserAction.deleteHekrData(url, new HekrUserAction.GetHekrDataListener() {
            @Override
            public void getSuccess(Object o) {
                context.hideLoading();
                ToastUtil.showMessage(context, context.getString(R.string.delete_suc));
                context.onRefresh();
            }

            @Override
            public void getFail(int i) {
                context.hideLoading();
                ToastUtil.showMessage(context, HekrCodeUtil.errorCode2Msg(context, i));
            }
        });
    }


    private void initExpand(final TimingAdapter.ViewHolder holder) {
        if (holder.expandableLayout.isExpanded()) {
            holder.expandImg.setVisibility(View.INVISIBLE);
        } else {
            holder.expandImg.setVisibility(View.VISIBLE);
        }
        holder.expandImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.expandableLayout.expand();
                AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
                alpha.setDuration(300);
                alpha.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        holder.expandImg.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                holder.expandImg.startAnimation(alpha);
            }
        });
        holder.collapseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.expandableLayout.collapse();
                AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
                alpha.setDuration(300);
                alpha.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        holder.expandImg.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                holder.expandImg.startAnimation(alpha);
            }
        });
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView timeNoon;
        public TextView hourMinute;
        public Switch stateSwitch;
        //        public TextView repeatOrOnce;
        public ImageView expandImg;
        public AppCompatCheckBox repeatCheckbox;
        public CheckBox sunday;
        public CheckBox monday;
        public CheckBox tuesday;
        public CheckBox wednesday;
        public CheckBox thursday;
        public CheckBox friday;
        public CheckBox saturday;
        public TextView taskContent;
        public ExpandableLayout expandableLayout;
        public ImageView collapseImg;
        public ImageView deleteItem;
        public ImageView outDate;
        public LinearLayout weekCon;
        public TextView changeDate;
        public TextView sureChange;

        public ViewHolder(View itemView) {
            super(itemView);
            timeNoon = (TextView) itemView.findViewById(R.id.time_noon);
            hourMinute = (TextView) itemView.findViewById(R.id.hour_minute);
            stateSwitch = (Switch) itemView.findViewById(R.id.state_switch);
//            repeatOrOnce = (TextView) itemView.findViewById(R.id.repeat_or_once);
            expandImg = (ImageView) itemView.findViewById(R.id.expand_img);
            repeatCheckbox = (AppCompatCheckBox) itemView.findViewById(R.id.repeat_checkbox);
            sunday = (CheckBox) itemView.findViewById(R.id.sunday);
            monday = (CheckBox) itemView.findViewById(R.id.monday);
            tuesday = (CheckBox) itemView.findViewById(R.id.tuesday);
            wednesday = (CheckBox) itemView.findViewById(R.id.wednesday);
            thursday = (CheckBox) itemView.findViewById(R.id.thursday);
            friday = (CheckBox) itemView.findViewById(R.id.friday);
            saturday = (CheckBox) itemView.findViewById(R.id.saturday);
            taskContent = (TextView) itemView.findViewById(R.id.task_content);
            expandableLayout = (ExpandableLayout) itemView.findViewById(R.id.expandable_layout);
            collapseImg = (ImageView) itemView.findViewById(R.id.collapse_img);
            deleteItem = (ImageView) itemView.findViewById(R.id.delete_item);
            outDate = (ImageView) itemView.findViewById(R.id.out_date);
            weekCon = (LinearLayout) itemView.findViewById(R.id.week_con);
            changeDate = (TextView) itemView.findViewById(R.id.change_date);
            sureChange = (TextView) itemView.findViewById(R.id.sure_change);
        }
    }
}
