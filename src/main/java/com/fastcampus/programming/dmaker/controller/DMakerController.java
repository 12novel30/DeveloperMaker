package com.fastcampus.programming.dmaker.controller;

import com.fastcampus.programming.dmaker.dto.*;
import com.fastcampus.programming.dmaker.service.DMakerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController //restController type bean 등록
// json 으로 주고 받고 할 것 - @RequestBody 포함 어노테이션
@Slf4j // log
@RequiredArgsConstructor // service 를 인젝션 할 필요 없음
// 생성자도 필요없음
public class DMakerController {

    private final DMakerService dMakerService; // [DI]

    @GetMapping("/developers")
    public List<DeveloperDto> getAllDevelopers() {
        // controller 에 entity 쓰면 좋지 못함 - 유연성, 보안 측면

        log.info("GET /developers HTTP/1.1");
        /* 2022-10-11 17:41:53.348  INFO
        * 23224 --- [nio-8080-exec-1]
        * c.f.p.d.controller.DMakerController
        * : GET /developers HTTP/1.1
        * */

        return dMakerService.getAllEmployedDevelopers();
    }

    @GetMapping("/developers/{memberId}")
    public DeveloperDetailDto getDeveloperDetail(
            @PathVariable final String memberId // 입력받은 값은 바뀌지 않음 - final
    ) {
        log.info("GET /developers HTTP/1.1");

        return dMakerService.getDeveloperDetail(memberId);
    }

    @PostMapping("/create-developers")
    public CreateDeveloper.Response createDevelopers(
            @Valid @RequestBody CreateDeveloper.Request request
            // @Valid: for [Validation] - 진입 전에 exception 발생
            // -> MethodArgumentNotValidException 에서 처리
            ) {
        log.info("request: {}", request); // dto 파일의 @ToString

        return dMakerService.createDeveloper(request);

        // return Collections.singletonList("lee");
        // 단일 객체일 때에는 Arrays 보다 singletonList 가 더 좋다
    }

    @PutMapping("/developers/{memberId}")
    public DeveloperDetailDto editDeveloper(
            @PathVariable final String memberId, // for [Data binding]
            @Valid @RequestBody EditDeveloper.Request request
    ) {
        log.info("GET /developers HTTP/1.1");

        return dMakerService.editDeveloper(memberId, request);
    }

    @DeleteMapping()
    public DeveloperDetailDto deleteDeveloper(
            @PathVariable final String memberId
    ){
        return dMakerService.deleteDeveloper(memberId);
    }

/*
    @ResponseStatus(value = HttpStatus.CONFLICT) // 요청이 중복되었을 때 Conflict 사용해도 좋음
    // 근데 그냥 200 ok로 두는 것도 좋음 - 딱 맞아 떨어지지 않는 경우가 많으니,
    // 끼워넣다가 어색해질 수도 있음 - 그냥 에러 원인을 내려주는 식으로 적어주는 것이 경향성에 맞음
    @ExceptionHandler(DMakerException.class) // error 처리를 하지 않고, 로그를 띄움
    // DMakerException 가 나오면 여기에서 처리한다
    public DMakerErrorResponse handleException (
            DMakerException e,
            HttpServletRequest request){
        log.error("errorCode: {}, url: {}, message: {}",
                e.getDMakerErrorCode(), request.getRequestURI(), e.getDetailMessage());
        return DMakerErrorResponse.builder()
                .errorCode(e.getDMakerErrorCode())
                .errorMessage(e.getDetailMessage())
                .build();
        *//* ex
        * {
            "errorCode": "DUPLICATED_MEMBER_ID",
            "errorMessage": "MemberId가 중복되는 개발자가 있습니다."
        }*//*
    }
    // 현재는 해당 컨트롤러에서만 사용할 수 있음 - 글로벌화하는 것이 추세
    */
}
