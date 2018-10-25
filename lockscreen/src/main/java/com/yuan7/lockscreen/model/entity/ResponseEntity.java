package com.yuan7.lockscreen.model.entity;

/**
 * Created by Administrator on 2018/5/15.
 */

public class ResponseEntity {
    private Integer code;
    private String message;
    private Boolean obj;

    public ResponseEntity() {
    }

    public ResponseEntity(Integer code, String message, Boolean obj) {
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

    public Boolean getObj() {
        return obj;
    }

    public void setObj(Boolean obj) {
        this.obj = obj;
    }
}
