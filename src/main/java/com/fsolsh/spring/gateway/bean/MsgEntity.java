package com.fsolsh.spring.gateway.bean;

/**
 * 消息实体
 */
public class MsgEntity {

    public static final MsgEntity SUCCESS = new MsgEntity(0, "request success");
    public static final MsgEntity FAILURE = new MsgEntity(-1, "request failure");

    private int code;
    private String message;


    public MsgEntity(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
