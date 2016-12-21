package com.lzh.yuanstrom.widget;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

/**
 * Created by Administrator on 2016/12/10.
 */

public class ChazuoSelectListener implements View.OnClickListener{
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
