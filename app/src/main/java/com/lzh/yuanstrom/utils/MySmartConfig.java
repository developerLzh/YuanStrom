//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.lzh.yuanstrom.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.MulticastLock;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;

import com.lzh.yuanstrom.R;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import me.hekr.hekrsdk.action.HekrUserAction;
import me.hekr.hekrsdk.action.HekrUser.GetNewDevicesListener;
import me.hekr.hekrsdk.action.HekrUser.GetPinCodeListener;
import me.hekr.hekrsdk.bean.NewDeviceBean;
import me.hekr.hekrsdk.util.BaseHttpUtil;
import me.hekr.hekrsdk.util.Log;
import me.hekr.hekrsdk.util.ViewWindow;

public class MySmartConfig {
    private static final String TAG = "MySmartConfig";
    private static final int getDeviceCheckWhat = 10086;
    private static final int getDeviceSuccessWhat = 10087;
    private static final int getDeviceFailWhat = 10088;
    private AtomicBoolean isConfigOK = new AtomicBoolean(false);
    private AtomicBoolean isGetDevice = new AtomicBoolean(false);
    private MySmartConfig.MySmartConfigHandler smartConfigHandler;
    private MySmartConfig.NewDeviceListener newDeviceListener;
    private CopyOnWriteArrayList<NewDeviceBean> deviceList = new CopyOnWriteArrayList();
    private CopyOnWriteArrayList<NewDeviceBean> localList = new CopyOnWriteArrayList();
    private HekrUserAction hekrUserAction;
    private MulticastLock lock = null;
    private WifiManager manager;
    private Context context;
    private String reallyPinCode;
    private String ssid;
    private long startTime;

    private SetStrListener setStrListener;

    public MySmartConfig(Context context,SetStrListener setStrListener) {
        this.context = context;
        this.manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        this.hekrUserAction = HekrUserAction.getInstance(context);
        this.smartConfigHandler = new MySmartConfig.MySmartConfigHandler();
        this.setStrListener = setStrListener;
    }

    private void initConfig(String ssid) {
        this.ssid = ssid;
        this.deviceList.clear();
        this.localList.clear();
        this.isConfigOK.set(false);
        this.isGetDevice.set(false);
        this.debugView("开始配网");
        this.startTime = System.currentTimeMillis();
    }

    private void appendStr(String s){
        setStrListener.setStr(s);
    }
    private void appendStr(String s,int color){
        setStrListener.setStr(s,color);
    }

    public void startConfig(final String ssid, final String password, final int number) {
        this.initConfig(ssid);
        appendStr(context.getString(R.string.get_pin_code));
        this.hekrUserAction.getPinCode(ssid, new GetPinCodeListener() {
            public void getSuccess(String pinCode) {
                MySmartConfig.this.reallyPinCode = pinCode;
                Log.i("MySmartConfig", "配网获取pinCode成功:" + MySmartConfig.this.reallyPinCode, new Object[0]);
                MySmartConfig.this.debugView("成功获取pinCode:" + MySmartConfig.this.reallyPinCode);
                appendStr(context.getString(R.string.get_pin_code_suc));
                appendStr(context.getString(R.string.maybe_one_minute));
                if (!TextUtils.isEmpty(MySmartConfig.this.reallyPinCode)) {
                    (new Thread(new Runnable() {
                        public void run() {
                            MySmartConfig.this.sendMsgMain(ssid, password, MySmartConfig.this.reallyPinCode, number);
                        }
                    })).start();
                } else {
                    MySmartConfig.this.newDeviceListener.getDeviceFail();
                }

            }

            public void getFail(int errorCode) {
                appendStr(context.getString(R.string.get_pin_code_failed)+errorCode, Color.RED);
                MySmartConfig.this.newDeviceListener.getDeviceFail();
            }

            public void getFailInSuccess() {
                MySmartConfig.this.newDeviceListener.getDeviceFail();
            }
        });
    }

    public void stopConfig() {
        this.isConfigOK.set(true);
        BaseHttpUtil.getClient().cancelAllRequests(true);
    }

    private void sendMsgMain(final String ssid, final String password, final String pinCode, int number) {
        Log.i("MySmartConfig", "进入配网", new Object[0]);
        this.debugView("开始发送ssid:" + ssid + ">>>password:" + password + ">>>pinCode:" + pinCode);
//        appendStr("开始发送消息到云端，ssid:" + ssid + ">>>password:" + password + ">>>pinCode:" + pinCode);
        this.isConfigOK.set(false);
        (new Thread() {
            public void run() {
                byte[] data = (ssid + '\u0000' + password + '\u0000' + pinCode + '\u0000').getBytes();
                long beginTime = System.currentTimeMillis();

                for (long passTime = MySmartConfig.this.getPassTime(beginTime); !MySmartConfig.this.isConfigOK.get(); passTime = MySmartConfig.this.getPassTime(beginTime)) {
                    if (MySmartConfig.this.lock == null) {
                        MySmartConfig.this.lock = MySmartConfig.this.manager.createMulticastLock("localWifi");
                        MySmartConfig.this.lock.setReferenceCounted(true);
                        MySmartConfig.this.lock.acquire();
                    }

                    long sleepTime ;
                    if (passTime > 1000L) {
                        sleepTime = MySmartConfig.getSleepTime(passTime, data.length);
                    } else {
                        sleepTime = MySmartConfig.getSleepTime(passTime, data.length + 1);
                    }

                    try {
                        Thread.sleep(sleepTime);
                        MySmartConfig.this.sendMsgToDevice(ssid + "", password + "", pinCode + "", (int) sleepTime);
                        Message e = new Message();
                        e.what = 10086;
                        MySmartConfig.this.smartConfigHandler.sendMessage(e);
                    } catch (Exception var9) {
                        var9.printStackTrace();
                    }
                }
            }
        }).start();
        int count = 0;

        try {
            while (!this.isConfigOK.get() && count < number) {
                Thread.sleep(1000L);
                ++count;
            }

            this.stopConfig();
            Message e = Message.obtain();
            if (!this.isGetDevice.get()) {
                e.what = 10088;
            } else {
                e.what = 10087;
            }

            this.smartConfigHandler.sendMessage(e);
            if (this.lock != null && this.lock.isHeld()) {
                this.lock.release();
                this.lock = null;
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

    }

    private void sendMsgToDevice(String ssid, String password, String pinCode, int time) throws IOException {
        Log.i("MySmartConfig", "开始发送ssid", new Object[0]);
//        appendStr("开始发送消息到设备，ssid:" + ssid );
        DatagramSocket ds = new DatagramSocket();
        byte[] ssidBs = ssid.getBytes("utf-8");
        byte[] passBs = password.getBytes("utf-8");
        byte[] pinCodeBs = pinCode.getBytes("utf-8");
        int len = ssidBs.length + passBs.length + pinCodeBs.length + 2;
        byte[] d = "hekrconfig".getBytes("utf-8");
        DatagramPacket dp = new DatagramPacket(d, d.length, InetAddress.getByName("224.127." + len + ".255"), 7001);
        ds.send(dp);
        ds.close();
        byte[] data = (ssid + '\n' + password + '\n' + pinCode).getBytes("utf-8");

        for (int i = 0; i < data.length; ++i) {
            ds = new DatagramSocket();
            dp = new DatagramPacket(d, d.length, InetAddress.getByName("224." + i + "." + this.unsignedByteToInt(data[i]) + ".255"), 7001);
            ds.send(dp);
            ds.close();
            if (time > 0) {
                try {
                    Thread.sleep((long) time);
                } catch (InterruptedException var15) {
                    var15.printStackTrace();
                }
            }
        }

    }

    private long getPassTime(long beginTime) {
        return System.currentTimeMillis() - beginTime;
    }

    public static long getSleepTime(long passTime, int length) {
        long param = passTime / 1000L - 3L > 0L ? passTime / 1000L - 3L : 0L;
        return (long) (100 / length) * (1L + param / 6L);
    }

    public void setNewDeviceListener(MySmartConfig.NewDeviceListener newDeviceListener) {
        this.newDeviceListener = newDeviceListener;
    }

    private int unsignedByteToInt(byte b) {
        return b & 255;
    }

    private void debugView(String content) {
        ViewWindow.showView(content);
    }

    @SuppressLint({"HandlerLeak"})
    class MySmartConfigHandler extends Handler {
        private WeakReference<Context> weakReference;

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (this.weakReference.get() != null) {
                switch (msg.what) {
                    case 10086:
                        Log.i("MySmartConfig", "开始查询新设备", new Object[0]);
                        if (!TextUtils.isEmpty(MySmartConfig.this.reallyPinCode) && !TextUtils.isEmpty(MySmartConfig.this.ssid)) {
//                            appendStr("单次查询新设备开始.." );
                            MySmartConfig.this.hekrUserAction.getNewDevices(MySmartConfig.this.reallyPinCode, MySmartConfig.this.ssid, new GetNewDevicesListener() {
                                public void getSuccess(List<NewDeviceBean> list) {
                                    if (list != null && !list.isEmpty()) {
                                        MySmartConfig.this.deviceList.clear();
                                        MySmartConfig.this.deviceList.addAll(list);
                                        if (MySmartConfig.this.deviceList.isEmpty()) {
                                            MySmartConfig.this.isGetDevice.set(false);
                                        } else {
                                            MySmartConfig.this.isGetDevice.set(true);
                                        }

                                        MySmartConfig.this.newDeviceListener.getDeviceList(MySmartConfig.this.deviceList);
                                        list.removeAll(MySmartConfig.this.localList);
                                        if (list.size() > 0) {
                                            long endTime = System.currentTimeMillis();
                                            long spendTime = (endTime - MySmartConfig.this.startTime) / 1000L;
                                            Iterator var6 = list.iterator();

                                            while (var6.hasNext()) {
                                                NewDeviceBean s = (NewDeviceBean) var6.next();
                                                MySmartConfig.this.debugView("新设备>>>devTid:" + s.getDevTid() + "配网耗时：" + spendTime + "秒");
                                                MySmartConfig.this.newDeviceListener.getNewDevice(s);
                                            }

                                            MySmartConfig.this.localList.addAll(list);
                                        }
                                    } else{
//                                        appendStr("单次查询结果：未查询到新设备");
                                    }

                                }

                                public void getFail(int errorCode) {
//                                    appendStr("单次查询新设备失败，错误码："+errorCode);
                                }
                            });
                        } else {
                            MySmartConfig.this.stopConfig();
                        }
                        break;
                    case 10087:
                        MySmartConfig.this.debugView("成功配网结束");
                        MySmartConfig.this.newDeviceListener.getDeviceSuccess();
                        break;
                    case 10088:
                        MySmartConfig.this.debugView("结束配网结束");
                        MySmartConfig.this.newDeviceListener.getDeviceFail();
                }
            } else {
                MySmartConfig.this.stopConfig();
            }

        }

        public MySmartConfigHandler() {
            this.weakReference = new WeakReference(MySmartConfig.this.context);
        }
    }

    public interface NewDeviceListener {
        void getDeviceList(List<NewDeviceBean> var1);

        void getNewDevice(NewDeviceBean var1);

        void getDeviceSuccess();

        void getDeviceFail();
    }

    public interface SetStrListener{
        void setStr(String s);
        void setStr(String s,int color);
    }
}
