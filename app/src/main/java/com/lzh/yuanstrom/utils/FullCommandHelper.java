package com.lzh.yuanstrom.utils;

/**
 * Created by Administrator on 2016/10/24.
 */

public class FullCommandHelper {

    private String devTid;

    private String ctrlKey;

    private String raw;

    public void setDevTid(String devTid) {
        this.devTid = devTid;
    }

    public void setCtrlKey(String ctrlKey) {
        this.ctrlKey = ctrlKey;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public FullCommandHelper(String devTid, String ctrlKey, String raw) {
        this.devTid = devTid;
        this.ctrlKey = ctrlKey;
        this.raw = raw;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{").append("\'action\':").append("\'appSend\',\'params\':{\'devTid\':\'").append(devTid)
                .append("\',\'ctrlKey\':\'").append(ctrlKey).append("\'").append(",").append("\'data\'")
                .append(":").append("{\'raw\':\'").append(raw).append("\'").append("}}}");

        return sb.toString();
    }

}
