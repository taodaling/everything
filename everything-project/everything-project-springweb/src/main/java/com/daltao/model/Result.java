package com.daltao.model;

import com.daltao.exception.BasicException;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Result<T> {
    private boolean success;
    private String errorCode;
    private String errorMessage;
    private String prompt;
    private T data;

    public static <T> Result<T> newSuccessResult(T data) {
        return Result.<T>builder()
                .data(data)
                .success(true)
                .build();
    }

    public static <T> Result<T> newFailResult(BasicException e) {
        return Result.<T>builder()
                .success(false)
                .errorCode(e.getErrorCode())
                .errorMessage(e.getErrorMessage())
                .prompt(e.getPrompt())
                .build();
    }
}
