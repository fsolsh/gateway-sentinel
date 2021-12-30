package com.fsolsh.spring.gateway.bean;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * 网关层的通用返回值
 *
 * @param <T>
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class Result<T> implements Serializable {

    /**
     * 响应码
     */
    private int code;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 响应时间
     */
    private long time;

    public Result() {
        time = System.currentTimeMillis();
    }

    public static Result<Boolean> result(boolean flag) {
        return flag ? ok() : fail();
    }

    public static Result<Boolean> result(MsgEntity msgEntity) {
        return result(msgEntity, null);
    }

    public static <T> Result<T> result(MsgEntity msgEntity, T data) {
        return result(msgEntity, null, data);
    }

    public static <T> Result<T> result(MsgEntity msgEntity, String message, T data) {
        boolean flag = false;
        if (msgEntity.getCode() == MsgEntity.SUCCESS.getCode()) {
            flag = true;
        }
        if (StringUtils.hasText(message)) {
            msgEntity.setMessage(message);
        }
        return new Result<T>().setSuccess(flag).setCode(msgEntity.getCode()).setMessage(msgEntity.getMessage()).setData(data);
    }

    public static <T> Result<T> ok() {
        return ok(null);
    }

    public static <T> Result<T> ok(T data) {
        return result(MsgEntity.SUCCESS, data);
    }

    public static <T> Result<T> ok(T data, String message) {
        return result(MsgEntity.SUCCESS, message, data);
    }

    public static <T> Result<T> fail() {
        return fail(null);
    }

    public static <T> Result<T> fail(T data) {
        return fail(MsgEntity.FAILURE, data);
    }

    public static <T> Result<T> fail(T data, String message) {
        return result(MsgEntity.FAILURE, message, data);
    }

    public static <T> Result<T> fail(MsgEntity msgEntity, T data) {
        if (MsgEntity.SUCCESS == msgEntity) {
            throw new RuntimeException("the code of fail result can not be : " + MsgEntity.SUCCESS.getCode());
        }
        return result(msgEntity, data);
    }

    public static <T> Result<T> fail(Integer code, String message) {
        return fail(code, message, null);
    }

    public static <T> Result<T> fail(Integer code, String message, T data) {
        return fail(new MsgEntity(code, message), data);
    }
}
