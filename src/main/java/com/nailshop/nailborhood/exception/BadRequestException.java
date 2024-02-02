package com.nailshop.nailborhood.exception;

import com.nailshop.nailborhood.type.ErrorCode;

public class BadRequestException extends RuntimeException{
    public BadRequestException(ErrorCode errorCode) {
        super(errorCode.getDescription());
    }
}
