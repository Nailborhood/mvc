package com.nailshop.nailborhood.exception;

import com.nailshop.nailborhood.type.ErrorCode;

public class NotFoundException extends RuntimeException{
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode.getDescription());
    }
}
