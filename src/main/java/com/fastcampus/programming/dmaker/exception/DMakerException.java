package com.fastcampus.programming.dmaker.exception;

import lombok.Getter;

@Getter
public class DMakerException extends RuntimeException{
    // DMaker 서비스만의 특정 에러를 정의하는 클래스
    // RuntimeException 을 상속받음
    private DMakerErrorCode dmakerErrorCode;
    private String detailMessage;
    
    public DMakerException(DMakerErrorCode errorCode){
        // for 일반적인 에러상황
        super(errorCode.getMessage());
        this.dmakerErrorCode = errorCode;
        this.detailMessage = errorCode.getMessage();
    }

    public DMakerException(DMakerErrorCode errorCode,
                           String detailMessage){
        // for 커스텀한 에러메세지를 출력해야할 때 사용
        super(errorCode.getMessage());
        this.dmakerErrorCode = errorCode;
        this.detailMessage = detailMessage;
    }
}