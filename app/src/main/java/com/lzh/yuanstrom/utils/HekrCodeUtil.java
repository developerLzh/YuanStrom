package com.lzh.yuanstrom.utils;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.lzh.yuanstrom.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.hekr.hekrsdk.bean.ErrorMsgBean;
import me.hekr.hekrsdk.util.Log;

/**
 * Created by Administrator on 2016/11/15.
 */

public class HekrCodeUtil {
    public static final int LANGUAGE_zh_Hans = 1;
    public static final int LANGUAGE_zh_Hant = 2;
    public static final int LANGUAGE_en = 3;

    public HekrCodeUtil() {
    }

    public static String url2Folder(String url) {
        Pattern p = Pattern.compile("android/(.*?)/");
        Matcher m = p.matcher(url);
        if(m.find()) {
            MatchResult mr = m.toMatchResult();
            return mr.group(1);
        } else {
            return null;
        }
    }

    public static int getLanguage(Context context) {
        return context.getResources().getConfiguration().locale.getCountry().equals("CN")?1:(context.getResources().getConfiguration().locale.getCountry().equals("TW")?2:(context.getResources().getConfiguration().locale.getCountry().equals("US")?3:1));
    }

    public static String errorCode2Msg(Context context,int errorCode) {
        switch(errorCode) {
            case 0:
                return context.getString(R.string.code0);
            case 1:
                return "";
            case 2:
                return "";
            case 500:
            case 5200000:
                return "";
            case 400016:
                return "";
            case 400017:
                return "";
            case 3400001:
                return "";
            case 3400002:
                return "";
            case 3400003:
                return "";
            case 3400005:
                return "";
            case 3400006:
                return "";
            case 3400007:
                return "";
            case 3400008:
                return "";
            case 3400009:
                return "";
            case 3400010:
                return "";
            case 3400011:
                return "";
            case 3400012:
                return "";
            case 3400013:
                return "";
            case 3400014:
                return "";
            case 5400001:
                return "";
            case 5400002:
                return "";
            case 5400003:
                return "";
            case 5400004:
                return "";
            case 5400005:
                return "";
            case 5400006:
                return "";
            case 5400007:
                return "";
            case 5400009:
                return "";
            case 5400010:
                return "";
            case 5400011:
                return "";
            case 5400012:
                return "";
            case 5400013:
                return "";
            case 5400014:
                return "";
            case 5400015:
                return "";
            case 5400016:
                return "";
            case 5400017:
                return "";
            case 5400018:
                return "";
            case 5400019:
                return "";
            case 5400020:
                return "";
            case 5400021:
                return "";
            case 5400022:
                return "";
            case 5400023:
                return "";
            case 5400024:
                return "";
            case 5400025:
                return "";
            case 5400026:
                return "";
            case 5400027:
                return "";
            case 5400028:
                return "";
            case 5400035:
                return "";
            case 5400036:
                return "";
            case 5400037:
                return "";
            case 5400039:
                return "";
            case 5400040:
                return "";
            case 5400041:
                return "";
            case 5400042:
                return "";
            case 5400043:
                return "";
            case 5500000:
                return "";
            case 6400001:
                return "";
            case 6400002:
                return "";
            case 6400003:
                return "";
            case 6400004:
                return "";
            case 6400005:
                return "";
            case 6400006:
                return "";
            case 6400007:
                return "";
            case 6400008:
                return "";
            case 6400009:
                return "";
            case 6400010:
                return "";
            case 6400011:
                return "";
            case 6400012:
                return "";
            case 6400013:
                return "";
            case 6400014:
                return "";
            case 6400015:
                return "";
            case 6400016:
                return "";
            case 6400017:
                return "";
            case 6400018:
                return "";
            case 6400020:
                return "";
            case 6400021:
                return "";
            case 6400022:
                return "";
            case 6400023:
                return "";
            case 6400024:
                return "";
            case 6500001:
                return "";
            case 6500002:
                return "";
            case 6500003:
                return "";
            case 8200000:
                return "";
            case 8400000:
                return "";
            case 8400001:
                return "";
            case 8400002:
                return "";
            case 8400003:
                return "";
            case 8400004:
                return "";
            case 9200000:
                return "";
            case 9400000:
                return "";
            case 9400001:
                return "";
            case 9400002:
                return "";
            case 9400003:
                return "";
            case 9400004:
                return "";
            case 9400005:
                return "";
            case 9500000:
                return "";
            case 9500001:
                return "";
            default:
                return "" + errorCode;
        }
    }

    public static int getErrorCode(int code, byte[] bytes) {
        switch(code) {
            case 0:
                Log.e("HEKR_SDK_ERROR", "Network is not available", new Object[0]);
                return code;
            case 1:
                Log.e("HEKR_SDK_ERROR", "Token expired, please re login", new Object[0]);
                return code;
            case 500:
                if(bytes != null && bytes.length > 0) {
                    Log.e("HEKR_SDK_ERROR", new String(bytes), new Object[0]);
                }

                return code;
            default:
                Log.e("HEKR_SDK_ERROR", "HTTP-" + code + new String(bytes), new Object[0]);
                ErrorMsgBean errorMsgBean = msg2Bean(bytes);
                return errorMsgBean.getCode();
        }
    }

    public static ErrorMsgBean msg2Bean(byte[] bytes) {
        ErrorMsgBean errorMsgBean = new ErrorMsgBean(2, "UNKNOWN_ERROR", System.currentTimeMillis());

        try {
            if(bytes != null && bytes.length != 0) {
                String e = new String(bytes);
                errorMsgBean = (ErrorMsgBean) JSON.parseObject(e, ErrorMsgBean.class);
                return errorMsgBean;
            } else {
                return errorMsgBean;
            }
        } catch (Exception var3) {
            return new ErrorMsgBean(2, "UNKNOWN_ERROR", System.currentTimeMillis());
        }
    }

    public static boolean getTimeOutFlag(int i, byte[] bytes) {
        if(i == 403) {
            if(bytes != null && bytes.length > 0) {
                String str = new String(bytes);

                try {
                    JSONObject e = new JSONObject(str);
                    if(e.has("status")) {
                        return e.getInt("status") == 403;
                    }
                } catch (JSONException var4) {
                    var4.printStackTrace();
                    return false;
                }
            }

            return false;
        } else {
            return false;
        }
    }
}
