package com.fsolsh.spring.gateway.bean;

public class ExceptionEntity {

    public static final ExceptionEntity SUCCESS = new ExceptionEntity(0, "request success");
    public static final ExceptionEntity FAILURE = new ExceptionEntity(-1, "request failure");

    private int code;
    private String message;


    public ExceptionEntity(int code, String message) {
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
