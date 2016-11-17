package com.lzh.yuanstrom.utils;

/**
 * Created by Vicent on 2016/8/20.
 */

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;

import com.litesuits.android.log.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created by ACER on 2015/7/14.
 */
public class AppManager {


    private static Stack<Activity> activityStack;
    private static AppManager instance;

    private AppManager() {
    }

    public static AppManager getAppManager() {
        if (instance == null) {
            instance = new AppManager();
        }
        return instance;
    }

    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }

    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        if (activity != null) {
            activity.finish();
            activity = null;
        }
    }

    public void removeActivity(Activity activity) {
        if (activity != null && null != activityStack) {
            activityStack.remove(activity);
            activity = null;
        }
    }

    public void finishActivity(Activity activity) {
        if (activity != null && null != activityStack) {
            activityStack.remove(activity);
        }
    }

    public void finishActivity(Class<?> cls) {
        if (null != activityStack) {

            List<Activity> activities = new LinkedList<Activity>();
            for (Activity activity : activityStack) {
                Log.e("activity Name", activity.getClass().getName());
                Log.e("cls Name", cls.getName());
                if (activity.getClass().getName().equals(cls.getName())) {
                    activities.add(activity);
                }
            }

            for (Activity activity : activities) {
                activity.finish();
                finishActivity(activity);
            }

        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (null != activityStack) {
            for (Activity activity : activityStack) {
                activity.finish();
            }
            activityStack.clear();
        }
    }

    public void AppExit(Context context) {
        try {
            if (PhotoHelper.hasSdcard()) {
                PhotoHelper.deleteTemp(Environment.getExternalStorageDirectory().getAbsolutePath() + "/YuanStrom/MyQrCode.png");
                PhotoHelper.deleteTemp(Environment.getExternalStorageDirectory().getAbsolutePath() + "/YuanStrom/CompanyQrCode.png");
            }
            finishAllActivity();
            ActivityManager activityMgr = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.killBackgroundProcesses(context.getPackageName());
            System.exit(0);

            android.os.Process.killProcess(android.os.Process.myPid());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
