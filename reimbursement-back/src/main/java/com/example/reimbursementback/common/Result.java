package com.example.reimbursementback.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code; // 状态码 (200成功, 500失败)
    private String msg;   // 提示信息
    private T data;       // 实际业务数据

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
//修改原因：前后端分离开发中，前端请求需要一个一致的响应结构。
//当前端拦截器接收到响应时，先判断 code == 200，
//以此决定是正常读取 data 还是弹出 msg 错误警告。