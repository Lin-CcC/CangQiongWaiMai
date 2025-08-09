package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    //java.sql.SQLIntegrityConstraintViolationException:
    // Duplicate entry 'admin' for key 'employee.idx_username'
    @ExceptionHandler
    public Result exceptionHandler(java.sql.SQLIntegrityConstraintViolationException ex){
        String message = ex.getMessage();
        if (message.contains("Duplicate entry")){
            String [] msg = message.split(" ");
            String userName = msg[2];
            message = "账号" + userName + MessageConstant.ALREADY_EXSITS;
            return Result.error(message);
        }
        else
            return Result.error(MessageConstant.UNKNOWN_ERROR);
    }
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }


}
