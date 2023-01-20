package com.fastcampus.programming.dmaker.exception;

import com.fastcampus.programming.dmaker.dto.DMakerErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

import static com.fastcampus.programming.dmaker.exception.DMakerErrorCode.INTERNAL_SERVER_ERROR;
import static com.fastcampus.programming.dmaker.exception.DMakerErrorCode.INVALID_REQUEST;

@Slf4j
@RestControllerAdvice // 각 컨트롤러에 어드바이스를 해주는 특수한 클래스
// 각 컨트롤러 안에 삽입해두기보다는, 전역적으로 선언하는 것이 좋음
public class DMakerExceptionHandler {

    @ExceptionHandler(DMakerException.class) // DMakerException 관련 에러
    public DMakerErrorResponse handleException (
            DMakerException e,
            HttpServletRequest request){ // client to server, 요청 내용 객체
        log.error("errorCode: {}, url: {}, message: {}",
                e.getDmakerErrorCode(), request.getRequestURI(), e.getDetailMessage());

        return DMakerErrorResponse.builder()
                .errorCode(e.getDmakerErrorCode())
                .errorMessage(e.getDetailMessage())
                .build();
    }

    @ExceptionHandler(value = { // 특수한 경우
            HttpRequestMethodNotSupportedException.class, //ex. PostMapping 인데 다른 매핑을 적용할 때
            MethodArgumentNotValidException.class // ex. java bean validation 에서 문제 발생 ->
            // 컨트롤러 내부에 진입도 못할 때
    })
    public DMakerErrorResponse handleBadRequest (
            Exception e, // 서비스만의 특수한 상황 아님
            HttpServletRequest request){
        log.error("url: {}, message: {}",
                request.getRequestURI(), e.getMessage());

        return DMakerErrorResponse.builder()
                .errorCode(INVALID_REQUEST)
                .errorMessage(INVALID_REQUEST.getMessage())
                .build();
    }

    @ExceptionHandler(Exception.class) // 이도저도아닌 모르는 케이스들
    public DMakerErrorResponse handleException (
            Exception e,
            HttpServletRequest request){
        log.error("url: {}, message: {}",
                request.getRequestURI(), e.getMessage());

        return DMakerErrorResponse.builder()
                .errorCode(INTERNAL_SERVER_ERROR)
                .errorMessage(INTERNAL_SERVER_ERROR.getMessage())
                .build();
    }
}


