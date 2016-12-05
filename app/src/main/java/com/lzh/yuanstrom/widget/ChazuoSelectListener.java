package com.lzh.yuanstrom.widget;

import android.view.View;
import android.widget.CheckBox;

import com.litesuits.android.log.Log;

/**
 * Created by Administrator on 2016/12/5.
 */

public class ChazuoSelectListener implements View.OnClickListener {

    private CheckBox onClickBox;
    private CheckBox otherBox;
    public ChazuoSelectListener(CheckBox onClickBox, CheckBox otherBox) {
        this.onClickBox = onClickBox;
        this.otherBox = otherBox;
    }

    @Override
    public void onClick(View v) {
        Log.e("onClick",onClickBox.isChecked()+"---");
        if(onClickBox.isChecked()){
            otherBox.setChecked(false);
        }
    }
}
