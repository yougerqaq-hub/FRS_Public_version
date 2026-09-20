package com.example.FBS.common;

public class ResultCode {

    // 操作成功
    public static final Integer SUCCESS = 200;

    // 请求参数或业务错误
    public static final Integer BAD_REQUEST = 400;

    // 未登录或身份验证失败
    public static final Integer UNAUTHORIZED = 401;

    // 没有权限
    public static final Integer FORBIDDEN = 403;

    // 数据不存在
    public static final Integer NOT_FOUND = 404;

    // 服务器内部错误
    public static final Integer SERVER_ERROR = 500;
}