package com.yuan7.lockscreen.helper.rx;

/**
 * Created by Administrator on 2016/11/29.
 */

public class EventBase {

    private Object option;
    private Object obj;

    public EventBase() {
    }

    public EventBase(Object option, Object obj) {
        this.option = option;
        this.obj = obj;
    }

    public Object getOption() {
        return option;
    }

    public void setOption(Object option) {
        this.option = option;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}