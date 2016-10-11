package com.lzh.yuanstrom.utils;

import android.content.Context;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2016/8/11.
 */
public class Utils {
    /**
     * dp2px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String hashToGetParam(Map<String, String> kv) {
        String paramStr = "";
        Iterator iter = kv.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            Object val = entry.getValue();
            paramStr += key + "=" + val + "&";
        }
        paramStr = paramStr.substring(0, paramStr.length() - 1);
        return paramStr;
    }

}
