package com.example.reimbursementback.common;

import lombok.Data;

@Data
public class Result<T> {  //使用泛型的目的是适配不同的业务场景
    private Integer code;  // 状态码：用于标识请求状态200表示成功，500表示失败
    private String msg;    // 给前端返回提示信息
    private T data;        // 返回给前端具体的业务数据

    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("success");
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMsg("success");
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }
}