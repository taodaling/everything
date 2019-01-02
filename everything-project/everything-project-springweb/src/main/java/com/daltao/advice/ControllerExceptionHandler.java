package com.daltao.advice;

import com.daltao.exception.BasicException;
import com.daltao.exception.InvalidInputException;
import com.daltao.exception.ServiceException;
import com.daltao.model.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ResponseBody
@Component
@ControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(InvalidInputException.class)
    ResponseEntity<?> handleInvalidException(HttpServletRequest request, InvalidInputException ex) {
        return new ResponseEntity<>(Result.newFailResult(ex), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ServiceException.class)
    ResponseEntity<?> handleServiceException(HttpServletRequest request, ServiceException ex) {
        return new ResponseEntity<>(Result.newFailResult(ex), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
