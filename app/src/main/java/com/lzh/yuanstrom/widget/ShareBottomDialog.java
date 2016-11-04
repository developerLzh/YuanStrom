package com.lzh.yuanstrom.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.utils.BmpUtils;
import com.lzh.yuanstrom.utils.PhotoHelper;
import com.lzh.yuanstrom.utils.ToastUtil;
import com.lzh.yuanstrom.utils.Utils;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/4.
 */

public class ShareBottomDialog {

    String shareTitle = "";
    String shareContent = "";
    String shareUrl = "";
    private Activity context;

    private BaseUiListener txListener;

    private IWeiboShareAPI mWeiboShareAPI;

    private IWeiboHandler.Response weiboResponse;

    public IWeiboShareAPI getmWeiboShareAPI() {
        return mWeiboShareAPI;
    }

    public BaseUiListener getTxListener() {
        return txListener;
    }

    public IWeiboHandler.Response getWeiboResponse() {
        return weiboResponse;
    }

    public ShareBottomDialog(Activity context) {
        this.context = context;
        this.txListener = new BaseUiListener();
        initWeiboRes();
        CusBottomSheetDialog dialog = new CusBottomSheetDialog(context);
        View v = LayoutInflater.from(context).inflate(R.layout.share_dialog, null);
        LinearLayout shareQQ = (LinearLayout) v.findViewById(R.id.share_QQ);
        LinearLayout shareQzone = (LinearLayout) v.findViewById(R.id.share_Qzone);
        LinearLayout shareSina = (LinearLayout) v.findViewById(R.id.share_sina);
        LinearLayout shareWx = (LinearLayout) v.findViewById(R.id.share_weixin);
        LinearLayout shareWxCircle = (LinearLayout) v.findViewById(R.id.share_wxcircle);

        reg2Wx();
        reg2QQ();
        reg2Weibo();

        shareQQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share2QQ();
            }
        });
        shareQzone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share2Qzone();
            }
        });
        shareWx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share2Wx();
            }
        });
        shareWxCircle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share2WxCircle();
            }
        });
        shareSina.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share2Wb();
            }
        });
    }

    private void initWeiboRes() {
        this.weiboResponse = new IWeiboHandler.Response() {
            @Override
            public void onResponse(BaseResponse response) {
                switch (response.errCode) {
                    case WBConstants.ErrorCode.ERR_OK:
                        ToastUtil.showMessage(context,context.getString(R.string.share_suc));
                        break;
                    case WBConstants.ErrorCode.ERR_CANCEL:
                        ToastUtil.showMessage(context,context.getString(R.string.share_cancel));
                        break;
                    case WBConstants.ErrorCode.ERR_FAIL:
                        Log.e("WBError", response.errMsg + " " + response.errCode + " " + response.reqPackageName);
                        ToastUtil.showMessage(context,context.getString(R.string.share_err));
                        break;
                }
            }
        };
    }

    private void share2Wb() {
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/YuanStrom" + "/QrCode.png");
        TextObject textObject = new TextObject();
        textObject.title = shareTitle;
        textObject.text = shareContent;
        textObject.actionUrl = shareUrl;
        textObject.setThumbImage(BmpUtils.createBitmapThumbnail(bitmap));

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();//初始化微博的分享消息
        weiboMessage.textObject = textObject;
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        mWeiboShareAPI.sendRequest(context, request);
    }

    private void reg2Weibo() {
        String config = Utils.getFromRaw(context, R.raw.config);
        String WB_APP_KEY = JSON.parseObject(config).getJSONObject("Social").getJSONObject("Weibo").getString("AppKey");
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(context, WB_APP_KEY);
        mWeiboShareAPI.registerApp();    // 将应用注册到微博客户端
    }

    private Tencent mTencent;

    void share2QQ() {
        //分享到QQ好友
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);  //图文模式
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareTitle);          //
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareContent);       //
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareUrl);
        if (PhotoHelper.hasSdcard()) {
            params.putString(QQShare.SHARE_TO_QQ_IMAGE_LOCAL_URL, Environment.getExternalStorageDirectory().getAbsolutePath() + "/YuanStrom" + "/QrCode.png"); //图片地址
        }
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, context.getResources().getString(R.string.app_name));
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);  //隐藏分享到QQ控件按钮

        mTencent.shareToQQ(context, params, txListener);
    }

    /**
     * 注册到QQ
     */
    private void reg2QQ() {
        String config = Utils.getFromRaw(context, R.raw.config);
        String QQ_APP_ID = JSON.parseObject(config).getJSONObject("Social").getJSONObject("QQ").getString("AppId");
        mTencent = Tencent.createInstance(QQ_APP_ID, context.getApplicationContext());
    }

    /**
     * 注册到微信
     */
    private IWXAPI iwxapi;

    private void reg2Wx() {
        String config = Utils.getFromRaw(context, R.raw.config);
        String WX_APP_ID = JSON.parseObject(config).getJSONObject("Social").getJSONObject("Weixin").getString("AppId");
        iwxapi = WXAPIFactory.createWXAPI(context, WX_APP_ID, true);
        iwxapi.registerApp(WX_APP_ID);  //将应用app注册到微信
    }

    void share2Qzone() {
        //分享到QQ空间
        ArrayList<String> urls = new ArrayList<>(); //图片地址，最多支持9张图片
        if (PhotoHelper.hasSdcard()) {
            urls.add(Environment.getExternalStorageDirectory().getAbsolutePath() + "/YuanStrom" + "/QrCode.png"); //图片地址
        }

        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareTitle);//分享标题
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareContent);//分享摘要
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareUrl);//点击后跳转到的URL
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, urls);

        mTencent.shareToQzone(context, params, txListener);
    }

    void share2Wx() {
        //分享到聊天界面
        wxShare(SendMessageToWX.Req.WXSceneSession);
    }

    void share2WxCircle() {
        //分享到朋友圈
        wxShare(SendMessageToWX.Req.WXSceneTimeline);
    }

    /**
     * 分享到微信
     *
     * @param scene SendMessageToWX.Req.WXSceneSession分享到微信好友聊天
     *              SendMessageToWX.Req.WXSceneTimeline分享到微信朋友圈
     */
    private void wxShare(int scene) {

        WXWebpageObject webPage = new WXWebpageObject();
        webPage.webpageUrl = shareUrl;

        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/YuanStrom" + "/QrCode.png");

        WXMediaMessage msg = new WXMediaMessage(webPage);
        msg.title = shareTitle;
        msg.description = shareContent;
        msg.thumbData = BmpUtils.bmpToByteArray(BmpUtils.createBitmapThumbnail(bitmap), false);

        //构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());   //该字段用于唯一标识一个请求
        req.message = msg;
        req.scene = scene; //设置分享到的位置

        // 调用api接口发送数据到微信
        iwxapi.sendReq(req);
    }

    public class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object o) {
            Toast.makeText(context, context.getString(R.string.share_suc), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(UiError uiError) {
            Log.e("UiError", uiError.errorMessage + " " + uiError.errorDetail + " " + uiError.errorCode);
            Toast.makeText(context, context.getString(R.string.share_err), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
            Toast.makeText(context, context.getString(R.string.share_cancel), Toast.LENGTH_SHORT).show();
        }
    }
}
