package com.lzh.yuanstrom.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.lzh.yuanstrom.R;
import com.lzh.yuanstrom.contract.BaseImpl;
import com.lzh.yuanstrom.utils.AppManager;
import com.lzh.yuanstrom.utils.StringUtils;

import me.hekr.hekrsdk.action.HekrUserAction;

/**
 * Created by Administrator on 2016/12/3.
 */

public abstract class MBaseActivity extends AppCompatActivity implements BaseImpl {

    protected AlertDialog progressHUD;

    public HekrUserAction hekrUserAction;

    protected Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setScreenOption();

        setContentView(setLayoutId());

        initView();

        canBackToolbar(setTitle());
        context = this;
        hekrUserAction = HekrUserAction.getInstance(context);
        AppManager.getAppManager().addActivity(this);
    }

    /**
     * @return layoutId
     */
    abstract int setLayoutId();

    /**
     * 显示加载框
     *
     * @param cancelable
     */
    @Override
    public void showLoading(boolean cancelable) {
        if (progressHUD == null) {
            progressHUD = new ProgressDialog(this);
            progressHUD.setTitle("");
            progressHUD.setMessage(this.getString(R.string.wait));
            progressHUD.setCanceledOnTouchOutside(cancelable);
        }
        if (!progressHUD.isShowing() && !this.isFinishing()) {
            progressHUD.show();
        }
    }

    /**
     * 隐藏加载框
     */
    @Override
    public void hideLoading() {
        if (null != progressHUD && progressHUD.isShowing()) {
            progressHUD.dismiss();
        }
    }

    /**
     * 设置toolbar相关
     *
     * @param title
     */
    @Override
    public void canBackToolbar(String title) {
        if (StringUtils.isNotBlank(title)) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(title);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
    }

    /**
     * 设置toolbar标题，如果不为空，则该界面是带返回键的。如果是带导航的按钮就设置为空然后再自行
     * 设置toolBar
     * @return
     */
    abstract String setTitle();

    /**
     * 这里可以重写 以便设置屏幕参数
     */
    @Override
    public void setScreenOption() {

    }

    abstract void initView();
}
