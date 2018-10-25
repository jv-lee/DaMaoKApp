package com.yuan7.lockscreen.model.entity;

/**
 * Created by Administrator on 2018/5/25.
 */

public class Response<T> {
    private Integer code;
    private String message;
    private T obj;

    public Response() {
    }

    public Response(Integer code, String message, T obj) {
        this.code = code;
        this.message = message;
        this.obj = obj;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}
