package com.lzh.yuanstrom.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.lzh.yuanstrom.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/6/19.
 */
public class SplashActivity extends BaseActivity {

    @BindView(R.id.home_bg)
    LinearLayout homeBg;

    @BindView(R.id.btn_container)
    LinearLayout btnCon;

    @OnClick(R.id.register)
    void register(){
        Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
        intent.putExtra("tag","register");
        startActivity(intent);
    }

    @OnClick(R.id.login)
    void login(){
        Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
        intent.putExtra("tag","login");
        startActivity(intent);
    }

//    @OnClick(R.id.to_qq)
//    void toQQ() {
//        startLoginWeb("http://login.hekr.me/oauth.htm?type=qq");
//    }
//
//    @OnClick(R.id.to_weibo)
//    void toWeibo() {
//        startLoginWeb("http://login.hekr.me/oauth.htm?type=weibo");
//    }
//
//    @OnClick(R.id.to_twitter)
//    void toTwitter() {
//        startLoginWeb("http://login.smartmatrix.mx/oauth.htm?type=tw");
//    }
//
//    @OnClick(R.id.to_google)
//    void toGoogle() {
//        startLoginWeb("http://login.smartmatrix.mx/oauth.htm?type=g");
//    }

    private AlphaAnimation alphaAnimation;

    private AlphaAnimation btnAnimation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ButterKnife.bind(this);

        setAnimation();

    }

    private void setAnimation() {
        alphaAnimation = new AlphaAnimation(0.0f,1.0f);
        alphaAnimation.setDuration(2000);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.e("tag","animation finish");
                btnCon.setVisibility(View.VISIBLE);
                btnCon.setAnimation(btnAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        homeBg.setAnimation(alphaAnimation);

        btnAnimation = new AlphaAnimation(0.0f,1.0f);
        btnAnimation.setDuration(1000);
    }

//    private void startLoginWeb(String url) {
//        Intent intoLoginWeb = new Intent();
//        intoLoginWeb.putExtra("url",url);
//        intoLoginWeb.setClass(SplashActivity.this, LoginWebActivity.class);
//        startActivity(intoLoginWeb);
//    }
//
//    private void initData(){
//
//        AssetsDatabaseManager.initManager(getApplication());
//        AssetsDatabaseManager mg = AssetsDatabaseManager.getManager();
//        SQLiteDatabase db = mg.getDatabase("db");
//
//        Cursor cursor = null;
//        try {
//            cursor = db.rawQuery("select setting_value from settings where setting_key=?",
//                    new String[]{"user_credential"});
//
//            if (cursor.moveToNext())
//            {
//                Intent intoMain = new Intent(SplashActivity.this,GateWayAct.class);
//                startActivity(intoMain);
//                SplashActivity.this.finish();
//            }
//            else{
//                Log.i("TAG","游标移动失败！");
//            }
//        }catch (Exception keye){
//            Log.d(SplashActivity.class.getSimpleName(),"Login从数据库查询key异常："+keye.getMessage());
//        }finally {
//            if(cursor!=null) {
//                cursor.close();
//            }
//        }
//    }

}