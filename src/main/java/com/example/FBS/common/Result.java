package com.example.FBS.common;

public class Result<T> {

    private Integer code;
    private String message;
    private T data;

    public Result() {
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 操作成功
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS, "操作成功", data);
    }

    // 操作成功，自定义消息
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS, message, data);
    }

    // 普通业务错误
    public static <T> Result<T> error(String message) {
        return new Result<>(ResultCode.BAD_REQUEST, message, null);
    }

    // 指定错误码
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }

    // 数据不存在
    public static <T> Result<T> notFound(String message) {
        return new Result<>(ResultCode.NOT_FOUND, message, null);
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}