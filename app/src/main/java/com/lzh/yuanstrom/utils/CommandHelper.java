package com.lzh.yuanstrom.utils;

import android.util.Log;

/**
 * Created by Vicent on 2016/8/18.
 */
public class CommandHelper {

    /** 第一位备选 **/
    public static final String SWITCH_COMMAND = "01";//开关
    public static final String TIMING_COMMAND = "02";//定时指令
    public static final String RETURN_COMMAND = "03";//获取信息
    public static final String SET_COMMAND = "04";//设置

    /** 第二位备选 **/
    public static final String DOT_PATTERN = "01";//点动模式
    public static final String NORMAL_PATTERN = "02";//常规模式
    public static final String UNIQUE_PATTERN = "03";//开关保证保证只有唯一一路打开模式

    /** 第三位备选 **/
    public static final String OPEN_INSTRUCTION = "01";//开命令
    public static final String CLOSE_INSTRUCTION = "02";//关命令

    public String frameHeader;//帧头

    public String frameLength;//帧长

    public String downCommand;//下发命令 02固定的

    public String frameSequence;//帧序 00 固定的

    public String firstCommand;//第一位命令
    public String secondCommand;//第二位命令
    public String thirdCommand;//第三位命令
    public String fourthCommand;//第四位命令 备用  固定00
    public String fifthCommand;//第五位命令 备用  固定00

    public String frameVerify;//校验

    public CommandHelper() {
        setFrameHeader(0x48);
        frameHeader = Integer.toHexString(0x48);
        downCommand = "0" + Integer.toHexString(0x02);
        frameSequence = "0" + Integer.toHexString(0x00);
        fourthCommand = "0" + Integer.toHexString(0x00);
        fifthCommand = "0" + Integer.toHexString(0x00);
    }

    public void setFirstCommand(String firstCommand) {
        this.firstCommand = firstCommand;
    }

    public void setSecondCommand(String secondCommand) {
        this.secondCommand = secondCommand;
    }

    public void setThirdCommand(String thirdCommand) {
        this.thirdCommand = thirdCommand;
    }

    public void setFrameHeader(Integer frameHeader) {
        this.frameHeader = Integer.toHexString(frameHeader);
    }

    public void setFrameLength() {
        Integer length = getCommandHelperLength() / 2 + 5;
        if (length < 16) {
            frameLength = "0" + Integer.toHexString(length);
        } else {
            frameLength = Integer.toHexString(length);
        }
    }

    public int getCommandHelperLength() {
        return (firstCommand + secondCommand  + thirdCommand + fourthCommand + fifthCommand).length() ;
    }

    public void setFrameVerify() {
        String unVerify = frameLength + downCommand + frameSequence + firstCommand + secondCommand  + thirdCommand + fourthCommand + fifthCommand;
        String[] strNum = new String[unVerify.length() / 2];
        String[] binaryArray = new String[unVerify.length() / 2];
        for (int i = 0; i < unVerify.length() / 2; i++) {
            strNum[i] = unVerify.substring(i * 2, (i + 1) * 2);
            binaryArray[i] = Integer.toHexString(Integer.valueOf(strNum[i], 16));
        }

        Integer verify = 0x48;
        for (int i = 0; i < binaryArray.length; i++) {
            verify += Integer.valueOf(binaryArray[i], 16);
            Log.e("datadata", "verify--->" + verify + "-----" + "binaryArray[i]--->" + binaryArray[i]);
        }

        if (verify < 16) {
            frameVerify = "0" + Integer.toHexString(verify);
        } else {
            frameVerify = Integer.toHexString(verify);
        }
        frameVerify = frameVerify.substring(frameVerify.length() - 2, frameVerify.length());
    }

    @Override
    public String toString() {
        return frameHeader + frameLength + downCommand + frameSequence + firstCommand + secondCommand  + thirdCommand + fourthCommand + fifthCommand + frameVerify;
    }

    public static class CommandBuilder {
        String firstCommand;
        String secondCommand;
        String thirdCommand;

        public CommandBuilder() {

        }

        public CommandBuilder setFirstCommand(String firstCommand) {
            this.firstCommand = firstCommand;
            return this;
        }

        public CommandBuilder setSecondCommand(String secondCommand) {
            this.secondCommand = secondCommand;
            return this;
        }

        public CommandBuilder setThirdCommand(String thirdCommand) {
            this.thirdCommand = thirdCommand;
            return this;
        }

        public CommandHelper build() {
            CommandHelper commandHelper = new CommandHelper();
            commandHelper.setFirstCommand(firstCommand);
            commandHelper.setSecondCommand(secondCommand);
            commandHelper.setThirdCommand(thirdCommand);
            commandHelper.setFrameLength();
            commandHelper.setFrameVerify();
            return commandHelper;
        }
    }

}
