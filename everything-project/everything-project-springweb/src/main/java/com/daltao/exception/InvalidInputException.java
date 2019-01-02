package com.daltao.exception;

import com.daltao.constant.ErrorCodes;
import org.springframework.http.HttpStatus;

public class InvalidInputException extends BasicException {
    public InvalidInputException(Integer status, String errorCode, String errorMessage, String prompt) {
        super(status, errorCode, errorMessage, prompt);
    }

    public static InvalidInputException missingField(String field) {
        return new InvalidInputException(HttpStatus.BAD_REQUEST.value(), ErrorCodes.MISSING_REQUIRED_FIELD,
                "Missing field " + field, "Please fill up " + field);
    }

    public static InvalidInputException invalidField(String field) {
        return new InvalidInputException(HttpStatus.BAD_REQUEST.value(), ErrorCodes.INVALID_FIELD,
                "Invalid field " + field, "Please verify the value of " + field);
    }

    public static InvalidInputException collisionField(String field) {
        return new InvalidInputException(HttpStatus.BAD_REQUEST.value(), ErrorCodes.COLLISION_FIELD,
                "Collision field " + field, "Please change the value of " + field);
    }
}
