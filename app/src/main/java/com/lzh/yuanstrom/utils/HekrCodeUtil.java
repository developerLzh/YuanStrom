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
                return context.getString(R.string.code1);
            case 2:
                return context.getString(R.string.code2);
            case 500:
            case 5200000:
                return context.getString(R.string.code5200000);
            case 400016:
                return context.getString(R.string.code400016);
            case 400017:
                return context.getString(R.string.code400017);
            case 3400001:
                return context.getString(R.string.code3400001);
            case 3400002:
                return context.getString(R.string.code3400002);
            case 3400003:
                return context.getString(R.string.code3400003);
            case 3400005:
                return context.getString(R.string.code3400005);
            case 3400006:
                return context.getString(R.string.code3400006);
            case 3400007:
                return context.getString(R.string.code3400007);
            case 3400008:
                return context.getString(R.string.code3400008);
            case 3400009:
                return context.getString(R.string.code3400009);
            case 3400010:
                return context.getString(R.string.code3400010);
            case 3400011:
                return context.getString(R.string.code3400011);
            case 3400012:
                return context.getString(R.string.code3400012);
            case 3400013:
                return context.getString(R.string.code3400013);
            case 3400014:
                return context.getString(R.string.code3400014);
            case 5400001:
                return context.getString(R.string.code5400001);
            case 5400002:
                return context.getString(R.string.code5400002);
            case 5400003:
                return context.getString(R.string.code5400003);
            case 5400004:
                return context.getString(R.string.code5400004);
            case 5400005:
                return context.getString(R.string.code5400005);
            case 5400006:
                return context.getString(R.string.code5400006);
            case 5400007:
                return context.getString(R.string.code5400007);
            case 5400009:
                return context.getString(R.string.code5400009);
            case 5400010:
                return context.getString(R.string.code54000010);
            case 5400011:
                return context.getString(R.string.code54000011);
            case 5400012:
                return context.getString(R.string.code54000012);
            case 5400013:
                return context.getString(R.string.code54000013);
            case 5400014:
                return context.getString(R.string.code54000014);
            case 5400015:
                return context.getString(R.string.code54000015);
            case 5400016:
                return context.getString(R.string.code54000016);
            case 5400017:
                return context.getString(R.string.code54000017);
            case 5400018:
                return context.getString(R.string.code54000018);
            case 5400019:
                return context.getString(R.string.code54000019);
            case 5400020:
                return context.getString(R.string.code54000020);
            case 5400021:
                return context.getString(R.string.code54000021);
            case 5400022:
                return context.getString(R.string.code54000022);
            case 5400023:
                return context.getString(R.string.code54000023);
            case 5400024:
                return context.getString(R.string.code54000024);
            case 5400025:
                return context.getString(R.string.code54000025);
            case 5400026:
                return context.getString(R.string.code54000026);
            case 5400027:
                return context.getString(R.string.code54000027);
            case 5400028:
                return context.getString(R.string.code54000028);
            case 5400035:
                return context.getString(R.string.code54000035);
            case 5400036:
                return context.getString(R.string.code54000036);
            case 5400037:
                return context.getString(R.string.code54000037);
            case 5400039:
                return context.getString(R.string.code54000039);
            case 5400040:
                return context.getString(R.string.code54000040);
            case 5400041:
                return context.getString(R.string.code54000041);
            case 5400042:
                return context.getString(R.string.code54000042);
            case 5400043:
                return context.getString(R.string.code54000043);
            case 5500000:
                return context.getString(R.string.code5500000);
            case 6400001:
                return context.getString(R.string.code6400001);
            case 6400002:
                return context.getString(R.string.code6400002);
            case 6400003:
                return context.getString(R.string.code6400003);
            case 6400004:
                return context.getString(R.string.code6400004);
            case 6400005:
                return context.getString(R.string.code6400005);
            case 6400006:
                return context.getString(R.string.code6400006);
            case 6400007:
                return context.getString(R.string.code6400007);
            case 6400008:
                return context.getString(R.string.code6400008);
            case 6400009:
                return context.getString(R.string.code6400009);
            case 6400010:
                return context.getString(R.string.code6400010);
            case 6400011:
                return context.getString(R.string.code6400011);
            case 6400012:
                return context.getString(R.string.code6400012);
            case 6400013:
                return context.getString(R.string.code6400013);
            case 6400014:
                return context.getString(R.string.code6400014);
            case 6400015:
                return context.getString(R.string.code6400015);
            case 6400016:
                return context.getString(R.string.code6400016);
            case 6400017:
                return context.getString(R.string.code6400017);
            case 6400018:
                return context.getString(R.string.code6400018);
            case 6400020:
                return context.getString(R.string.code6400020);
            case 6400021:
                return context.getString(R.string.code6400021);
            case 6400022:
                return context.getString(R.string.code6400022);
            case 6400023:
                return context.getString(R.string.code6400023);
            case 6400024:
                return context.getString(R.string.code6400024);
            case 6500001:
                return context.getString(R.string.code6500001);
            case 6500002:
                return context.getString(R.string.code6500002);
            case 6500003:
                return context.getString(R.string.code6500003);
            case 8200000:
                return context.getString(R.string.code8200000);
            case 8400000:
                return context.getString(R.string.code8400000);
            case 8400001:
                return context.getString(R.string.code8400001);
            case 8400002:
                return context.getString(R.string.code8400002);
            case 8400003:
                return context.getString(R.string.code8400003);
            case 8400004:
                return context.getString(R.string.code8400004);
            case 9200000:
                return context.getString(R.string.code9200000);
            case 9400000:
                return context.getString(R.string.code9400000);
            case 9400001:
                return context.getString(R.string.code9400001);
            case 9400002:
                return context.getString(R.string.code9400002);
            case 9400003:
                return context.getString(R.string.code9400003);
            case 9400004:
                return context.getString(R.string.code9400004);
            case 9400005:
                return context.getString(R.string.code9400005);
            case 9500000:
                return context.getString(R.string.code9500000);
            case 9500001:
                return context.getString(R.string.code9500001);
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
