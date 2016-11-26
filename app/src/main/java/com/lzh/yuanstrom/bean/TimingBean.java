package com.lzh.yuanstrom.bean;

import java.util.List;

/**
 * Created by Administrator on 2016/11/23.
 */

public class TimingBean {

    public Long taskId;

    public String taskKey;

    public String schedulerType;

    public CmdBean code;

    public String cronExpr;

    public boolean enable;//是否打开

    public boolean expired;//是否已过期

    public String triggerDateTime;//修改ONCE型预约的日期时间

    public String triggerTime;//修改LOOP型的时间

    public List<String> repeat;//修改LOOP型的周期

    public boolean needShowChange;

}
