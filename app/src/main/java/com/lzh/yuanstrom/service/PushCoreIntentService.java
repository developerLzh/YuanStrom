package com.lzh.yuanstrom.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import com.igexin.sdk.GTIntentService;
import com.igexin.sdk.PushManager;
import com.igexin.sdk.Tag;
import com.igexin.sdk.message.GTCmdMessage;
import com.igexin.sdk.message.GTTransmitMessage;
import com.lzh.yuanstrom.MyApplication;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.ui.MainActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import cz.msebera.android.httpclient.util.TextUtils;
import me.hekr.hekrsdk.action.HekrUser;
import me.hekr.hekrsdk.action.HekrUserAction;
import me.hekr.hekrsdk.util.HekrSDK;

/**
 * Created by Administrator on 2016/12/21.
 */

public class PushCoreIntentService extends GTIntentService {

    @Override
    public void onReceiveServicePid(Context context, int i) {

    }

    @Override
    public void onReceiveClientId(Context context, String cid) {
                    /*个推设置tag
                    为当前用户设置一组标签，后续推送可以指定标签名进行定向推送
                    标签的设定，一定要在获取到 Clientid 之后才可以设定。标签的设定，服务端限制一天只能成功设置一次*/
        HekrUserAction hekrUserAction = HekrUserAction.getInstance(PushCoreIntentService.this);
        if (!TextUtils.isEmpty(HekrSDK.pid)) {
            String[] tags = new String[]{HekrSDK.pid};
            Tag[] tagParam = new Tag[tags.length];
            for (int i = 0; i < tags.length; i++) {
                Tag t = new Tag();
                t.setName(tags[i]);
                tagParam[i] = t;
            }
            PushManager.getInstance().setTag(context, tagParam, "100861");
        }
        /*接口 PushManager 中的 bindAlias， 绑定别名
                    同一个别名最多绑定10个 ClientID (适用于允许多设备同时登陆的应用)，当已绑定10个 ClientID 时，再次调用此接口会自动解绑最早绑定的记录，
                    当ClientID已绑定了别名A，若调用此接口绑定别名B，则与别名A的绑定关系会自动解除。
                    此接口与 unBindAlias 一天内最多调用100次，两次调用的间隔需大于5s*/
        if (!TextUtils.isEmpty(hekrUserAction.getUserId())) {
            PushManager.getInstance().bindAlias(context, hekrUserAction.getUserId());
        }
        //hekr服务器关联个推服务器
        if (!TextUtils.isEmpty(cid)) {
            hekrUserAction.pushTagBind(cid, new HekrUser.PushTagBindListener() {
                @Override
                public void pushTagBindSuccess() {
                }

                @Override
                public void pushTagBindFail(int errorCode) {
                }
            });
        }
    }

    @Override
    public void onReceiveMessageData(Context context, GTTransmitMessage gtTransmitMessage) {
        if (null != gtTransmitMessage) {
            byte[] bytes = gtTransmitMessage.getPayload();
            if (bytes != null) {
                String s = new String(bytes);
                detailMsg(s);
            }
            PushManager.getInstance().sendFeedbackMessage(context, gtTransmitMessage.getTaskId(), gtTransmitMessage.getMessageId(), 90001);
        }
    }

    @Override
    public void onReceiveOnlineState(Context context, boolean b) {

    }

    @Override
    public void onReceiveCommandResult(Context context, GTCmdMessage gtCmdMessage) {

    }

    private void detailMsg(String s) {
        Log.e("receive getuiMsg", s);
        newShowNotify(PushCoreIntentService.this, getString(R.string.app_name), getString(R.string.notify_tips), s, R.mipmap.ic_launcher);
    }

    private static int NOTIFICATION_ID = 0;

    private void newShowNotify(Context context, String tips, String title,
                               String content, int icon) {
        NOTIFICATION_ID += 1;
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) MyApplication.getInstance()
                .getSystemService(ns);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notificationIntent.putExtra("notifyId", NOTIFICATION_ID);
        PendingIntent contentIntent = PendingIntent.getActivity(
                MyApplication.getInstance(), NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                MyApplication.getInstance());
        builder.setSmallIcon(R.mipmap.bar_icon);
        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon));
        builder.setColor(getResources().getColor(R.color.colorPrimary));
        builder.setTicker(tips);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentTitle(title);
        builder.setContentText(content);
        builder.setContentIntent(contentIntent);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(content);

        builder.setStyle(bigTextStyle);

        builder.addAction(R.mipmap.age_icon, "查看", contentIntent);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.defaults = Notification.DEFAULT_ALL;
        mNotificationManager.notify(NOTIFICATION_ID, notification);
    }
}
