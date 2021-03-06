package com.lzh.yuanstrom.httphelper;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.widget.ProgressHUD;

/**
 * Created by Vicent on 2016/9/26.
 */

public class ProgressHandler extends Handler {

    private Context context;

    private ProgressHUD progressHUD;

    private boolean cancelable;

    public static final int SHOW_DIALOG = 0X01;
    public static final int DISMISS_DIALOG = 0X02;

    private ProgressDismissListener progressDismissListener;

    public ProgressHandler(Context context, boolean cancelable, final ProgressDismissListener progressDismissListener) {
        this.context = context;
        this.cancelable = cancelable;
        this.progressDismissListener = progressDismissListener;
    }

    private void initDialog() {
        progressHUD = new ProgressHUD.Builder(context)
                .setTitle("")
                .setMessage(context.getString(R.string.wait))
                .setTouchOnOutSide(cancelable)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        progressDismissListener.onProgressDismiss();
                    }
                }).create();
        if (!progressHUD.isShowing()) {
            progressHUD.show();
        }
    }

    private void dismissDialog() {
        if (null != progressHUD && progressHUD.isShowing()) {
            progressHUD.dismiss();
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {

            case SHOW_DIALOG:
                initDialog();
                break;

            case DISMISS_DIALOG:
                dismissDialog();
                break;
        }
    }
}
