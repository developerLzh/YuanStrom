package com.lzh.yuanstrom.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.lzh.yuanstrom.MyApplication;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.httphelper.Config;
import com.lzh.yuanstrom.ui.SplashActivity;
import com.lzh.yuanstrom.utils.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2016/8/23.
 */
public class SocketService extends Service {

    public final static String OPEN_SOCKET = "com.lzh.yuanstrom.OPEN_SOCKET";
    public final static String CLOSE_SOCKET = "com.lzh.yuanstrom.CLOSE_SOCKET";
    public final static String WRITE_MESSAGE = "com.lzh.yuanstrom.WRITE_MESSAGE";
    //    public final static String LOGIN_SUCCESS = "com.lzh.yuanstrom.LOGIN_SUCCESS";
    public final static String DETAIL_DATA = "com.lzh.yuanstrom.DETAIL_DATA";
    public final static int Notification_ID = 0x999;

    private NotifyBroadCastReceiver notifyBroadCastReceiver;

    private SocketThread socketThread;

    private OutputStream ou;

    private OutputStreamWriter writer;

    private Socket socket;

    private Timer timer;//

    private TimerTask timerTask;//启动定时任务持续读取输入流

    private BufferedReader reader;

    private InputStream in;

    protected String tid;

    protected String shortAddr;

    private Timer protectTimer;

    private TimerTask proteceTimerTask;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notifyBroadCastReceiver = new NotifyBroadCastReceiver();
        IntentFilter filter = new IntentFilter(CLOSE_SOCKET);
        registerReceiver(notifyBroadCastReceiver, filter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if ((intent == null) || (intent.getAction() == null)) {
            return START_NOT_STICKY;
        }
        String gotAction = intent.getAction();
        if (gotAction.equals(OPEN_SOCKET)) {
            if (socketThread == null || !socketThread.isAlive()) {
                socketThread = new SocketThread();
                socketThread.start();
            } else {
                writeOutputStream(loginCommand());
            }
            showNotify(this);
        } else if (gotAction.equals(CLOSE_SOCKET)) {
            cancelTimerAndStream();
            MyApplication.getInstance().setLoginSuc(false);
            stopSelf();
        } else if (gotAction.equals(WRITE_MESSAGE)) {
            String msg = intent.getStringExtra("msg");
            writeOutputStream(msg);
        }
        return START_STICKY;
    }

    private void showNotify(Context context) {
        Intent intent = new Intent();
        intent.setClass(SocketService.this, SplashActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);

        builder.setSmallIcon(R.mipmap.small_icon);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setColor(getResources().getColor(R.color.colorAccent));
        builder.setContentTitle(getResources().getString(R.string.app_name));
        builder.setContentText(getResources().getString(R.string.app_name)
                + "正在后台运行");
        builder.setWhen(System.currentTimeMillis());
        builder.setContentIntent(pendingIntent);
        builder.setOngoing(true);

        Notification notification = builder.build();

        notification.flags = Notification.FLAG_NO_CLEAR
                | Notification.FLAG_ONGOING_EVENT;

        notification.contentView = notifyNormal(context);

        startForeground(Notification_ID, notification);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(notifyBroadCastReceiver);
        super.onDestroy();
    }

    private RemoteViews notifyNormal(Context context) {
        RemoteViews view = new RemoteViews(getPackageName(), R.layout.notification);
        Intent closeIntent = new Intent(CLOSE_SOCKET);
        PendingIntent mPendingIntent = PendingIntent.getBroadcast(context, 0, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.close_notification, mPendingIntent);
        return view;
    }

    public class NotifyBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && StringUtils.isNotBlank(intent.getAction())) {
                cancelTimerAndStream();
                MyApplication.getInstance().setLoginSuc(false);
                stopSelf();
            }
        }
    }

    class SocketThread extends Thread {
        @Override
        public void run() {
            try {
                //为了简单起见，所有的异常都直接往外抛
                String host = Config.HOST;  //要连接的服务端IP地址
                int port = Config.PORT;   //要连接的服务端对应的监听端口
                socket = new Socket();
                socket.connect(new InetSocketAddress(host, port));
                while (true) {
                    if (socket.isConnected()) {
                        writeOutputStream(loginCommand());
                        break;
                    }
                }
                startReadData();
            } catch (Exception e) {
                Log.e("exception", e.toString());
                e.printStackTrace();
            }
        }
    }

    private synchronized void writeOutputStream(final String message) {
        Thread tempThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ou = socket.getOutputStream();
                    writer = new OutputStreamWriter(ou, "GBK");
                    writer.write(message);
                    writer.flush();
                    Log.e("data", "write message---->" + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        tempThread.start();
    }

    private String loginCommand() {
        String token = MyApplication.getInstance().getSharedPreferences().getString("USERACCESSKEY", "");
        Log.e("token", "(login \"AppTidlampairccxxx\" \"code\" \""
                + token
                + "\" \"USER\")\n");
        return "(login \"AppTidlampairccxxx\" \"code\" \""
                + token
                + "\" \"USER\")\n";
    }

    private void startReadData() {
        try {
            in = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    final char[] chars = new char[256];
                    StringBuilder sb = new StringBuilder();
                    try {
                        reader.read(chars);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (char c : chars) {
                        sb.append(c);
                    }
                    String a = sb.toString();
                    if (!a.contains("(") || !a.contains(")")) {
                        return;
                    }
//                    Log.e("inputStream", a);
                    if (a.contains("(getall )")) {
                        MyApplication.getInstance().setLoginSuc(true);
                    }
                    String[] splits = a.split("\"uartdata\" \"");
                    if (splits.length >= 2) {
                        String[] uartdatas = splits[1].split("\"");
                        if (uartdatas != null && !uartdatas.equals("")) {
                            String[] b = splits[0].split("\"");
                            if (b.length >= 3) {
                                sendData(uartdatas[0], tid);//发送数据广播
                            }
                        }
                        Log.e("datadata", "ReceiveData--->" + uartdatas[0]);
                    }
                }
            };
            timer.schedule(timerTask, 0, 500);
            protectTimer = new Timer();
            proteceTimerTask = new TimerTask() {
                @Override
                public void run() {
                    writeOutputStream("(ping)");
                }
            };
            protectTimer.schedule(proteceTimerTask, 0, 30 * 1000);
        } catch (IOException e) {
            Log.e("exception", "exception--->" + e.toString());
            e.printStackTrace();
        }
    }

    private void cancelTimerAndStream() {
        if (protectTimer != null) {
            if (proteceTimerTask != null) {
                proteceTimerTask.cancel();
            }
            protectTimer.cancel();
        }
        try {
            in.close();
            reader.close();
            writer.close();
            ou.close();
            socket.close();
            if (socketThread.isAlive()) {
                socketThread.interrupt();
            }
            if (timer != null) {
                if (timerTask != null) {
                    timerTask.cancel();
                }
                timer.cancel();
            }
        } catch (IOException e) {
            Log.e("exception", "e.toString");
            e.printStackTrace();
        }
    }

    private void sendData(String a, String gateWay) {
        int frameLen = Integer.parseInt(a.substring(2, 4), 16);
        if (frameLen != (a.length() - 10) / 2) {
            return;
        }
        Intent intent = new Intent(DETAIL_DATA);
        intent.putExtra("data", a);
        intent.putExtra("gateWay", gateWay);
        sendBroadcast(intent);
    }

//    private void sendLoginSuc() {
//        Intent intent = new Intent(LOGIN_SUCCESS);
//        sendBroadcast(intent);
//    }
}
