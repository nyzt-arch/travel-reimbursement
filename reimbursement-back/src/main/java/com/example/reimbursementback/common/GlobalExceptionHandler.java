package com.example.reimbursementback.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 当程序出现错误时，会执行 第 15 和 16 行的代码
 * 将异常信息转换为 Result.error(msg) 格式返回。
 * 可以确保前端接收到的永远是标准的 JSON 数据，而不是 HTTP 500 错误页面
 */
@RestControllerAdvice     // 拦截项目中所有 Controller 抛出的异常
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        return Result.error(e.getMessage());
    }
}



