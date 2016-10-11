package com.lzh.yuanstrom.utils;

import android.util.Log;

/**
 * Created by Administrator on 2016/8/18.
 */
public class CommandHelper {
    public String frameHeader;//帧头

    public String frameLength;//帧长

    public String downCommand;//下发命令 02固定的

    public String frameSequence;//帧序 00 固定的

    public String frameCommand;//执行的操作上层 例如：02 操作的是灯的开或关
    // 0x0002：开关操作
    // 0x0003：调亮度
    // 0x0006：调色温
    // 0x0007：调色彩
    // 0x0009：返回详细信息
    // 0x0010 返回状态信息
    // 0x00a0：复位设备
    // 0x00b0：允许设备加入网络
    // 0x00c0：恢复网络状态

    public String shortAddr;//短地址

    public String action;//具体做什么操作

    public String frameVerify;//校验

    public CommandHelper() {
        setFrameHeader(0x48);
        frameHeader = Integer.toHexString(0x48);
        downCommand = Integer.toHexString(0x02);
        frameSequence = Integer.toHexString(0x00);
    }

    public void setFrameHeader(Integer frameHeader) {
        this.frameHeader = Integer.toHexString(frameHeader);
    }

    public void setFrameLength() {
        Integer length = getCommandHelperLength() / 2;
        if (length < 16) {
            frameLength = "0" + Integer.toHexString(length);
        } else {
            frameLength = Integer.toHexString(length);
        }
    }

    public void setFrameCommand(String frameCommand) {
        this.frameCommand = frameCommand;
    }

    public int getCommandHelperLength() {

        return ((frameCommand + shortAddr + action).length() + 12) / 2;
    }

    public void setShortAddr(String shortAddr) {
        this.shortAddr = shortAddr;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public void setFrameVerify() {
        if(action == null){
            action = "";
        }
        String unVerify = frameLength + downCommand + frameSequence + frameCommand + shortAddr  + action;
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
        return frameHeader + frameLength + downCommand + frameSequence + frameCommand + shortAddr  + action + frameVerify;
    }

    public static class CommandBuilder {
        String frameCommand;
        String shortAddr;
        String action;

        public CommandBuilder() {

        }

        public CommandBuilder setFrameCommand(String frameCommand) {
            this.frameCommand = frameCommand;
            return this;
        }

        public CommandBuilder setShortAddr(String shortAddr) {
            this.shortAddr = shortAddr;
            return this;
        }

        public CommandBuilder setAction(String action) {
            this.action = action;
            return this;
        }

        public String build() {
            CommandHelper commandHelper = new CommandHelper();
            commandHelper.setFrameCommand(frameCommand);
            commandHelper.setShortAddr(shortAddr);
            commandHelper.setAction(action);
            commandHelper.setFrameLength();
            commandHelper.setFrameVerify();
            return commandHelper.toString();
        }
    }

//    /**
//     * 设置红外绑定指令数据
//     * @param hongwaiShortAddress 红外短地址
//     * @param targetShortAddr  需要绑定的短地址
//     * @param switchState  开关状态
//     * @param lightAlwaysOrAlittleSec  持续或者过一会儿就消失
//     */
//    public void setHongWaiFrameData(String hongwaiShortAddress,String targetShortAddr,String switchState,String lightAlwaysOrAlittleSec){
//        frameData = hongwaiShortAddress+"02"+targetShortAddr+lightAlwaysOrAlittleSec+switchState;
//    }
//
//    /**
//     * 设置按键绑定的数据
//     * @param anjianShort 按键短地址
//     * @param anjianNo 按键编号
//     * @param targetShortAddr 目标设备短地址
//     * @param switchState 开关状态
//     * @param lightAlwaysOrAlittleSec 持续或者过一会儿就消失
//     */
//    public void setAnjianFrameData(String anjianShort,String anjianNo,String targetShortAddr,String switchState,String lightAlwaysOrAlittleSec){
//        frameData = anjianShort +  anjianNo + "02" + targetShortAddr + lightAlwaysOrAlittleSec + switchState;
//    }
//
//    /**
//     * 设置按键解绑数据
//     * @param anjianShort 按键短地址
//     * @param anjianNo 按键编号
//     * @param targetShortAddr 目标短地址
//     */
//    public void setAnjianDeleteFrameData(String anjianShort,String anjianNo,String targetShortAddr){
//        frameData = anjianShort +  anjianNo  + targetShortAddr ;
//    }
//
//    /**
//     * 设置红外开关指令数据
//     * @param hongwaiShortAddress 红外短地址
//     * @param switchState 开关状态
//     */
//    public void setHongWaiFrameData(String hongwaiShortAddress,String switchState){
//        frameData = hongwaiShortAddress+switchState;
//    }
//
//    /**
//     * 设置红外解绑数据
//     * @param hongwaiShortAddress 红外短地址
//     * @param targetShortAddr 目标短地址
//     */
//    public void setDelHongwaiFrameData(String hongwaiShortAddress,String targetShortAddr){
//        frameData = hongwaiShortAddress+targetShortAddr;
//    }
//
//    /**
//     * 设置插座开关状态
//     * @param shortAddr 插座短地址
//     * @param chazuoWeizhi 插座位置
//     * @param switchState 开关状态
//     */
//    public void setChazuoFrameData(String shortAddr,String chazuoWeizhi,String switchState){
//        frameData = shortAddr+chazuoWeizhi+switchState;
//    }
//
//    /**
//     *
//     * @param targetShortAddr
//     */
//    public void setDelDevFrameData(String targetShortAddr){
//        frameData = targetShortAddr;
//    }
//
//    /**
//     * 设置灯的相关操作
//     * @param shortAddr 灯的短地址
//     * @param switchState 开关状态
//     * @param ramp
//     * @param brightness 亮度
//     * @param colorTemperature
//     * @param R 红色
//     * @param G 绿色
//     * @param B 蓝色
//     * @param isGetDevState
//     */
//    public void setLampSwitchFrameData(String shortAddr,Integer switchState,Integer ramp,Integer brightness,Integer colorTemperature,Integer R,Integer G,Integer B,boolean isGetDevState) {
//        //操作灯 或开关的数据
//        frameData = shortAddr;
//        if(isGetDevState){
//            return;
//        }
//        frameData += Integer.toHexString(switchState);
//        if(switchState != 0xff){
//            frameData = shortAddr + "0"+Integer.toHexString(0x02) + "0" + Integer.toHexString(switchState);
//            return;
//        }
//        frameData += Integer.toHexString(ramp);
//        if(ramp != 0xff){
//            return;
//        }
//        frameData += Integer.toHexString(brightness);
//        if(brightness != 0xff){
//            return;
//        }
//        frameData += Integer.toHexString(colorTemperature);
//        if(colorTemperature != 0xff){
//            return;
//        }
//        if(R < 16){
//            frameData += "0" + Integer.toHexString(R);
//        }else{
//            frameData += Integer.toHexString(R);
//        }
//        if(G < 16){
//            frameData += "0" + Integer.toHexString(G);
//        }else{
//            frameData += Integer.toHexString(G);
//        }
//        if(B < 16){
//            frameData += "0" + Integer.toHexString(B);
//        }else{
//            frameData += Integer.toHexString(B);
//        }
//    }

}
