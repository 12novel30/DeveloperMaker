package com.fastcampus.programming.dmaker.exception;

import lombok.Getter;

@Getter
public class DmakerException extends RuntimeException{
    private DmakerErrorCode dmakerErrorCode;
    private String detailMessage;
    
    public DmakerException(DmakerErrorCode errorCode){
        // for 일반적인 에러상황
        super(errorCode.getMessage());
        this.dmakerErrorCode = errorCode;
        this.detailMessage = errorCode.getMessage();
    }

    public DmakerException(DmakerErrorCode errorCode,
                           String detailMessage){
        // for 커스텀한 에러상황
        super(errorCode.getMessage());
        this.dmakerErrorCode = errorCode;
        this.detailMessage = detailMessage;
    }
}