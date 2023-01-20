package com.fastcampus.programming.dmaker.controller;

import com.fastcampus.programming.dmaker.dto.CreateDeveloper;
import com.fastcampus.programming.dmaker.dto.DeveloperDetailDto;
import com.fastcampus.programming.dmaker.dto.DeveloperDto;
import com.fastcampus.programming.dmaker.dto.EditDeveloper;
import com.fastcampus.programming.dmaker.service.DMakerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController //restController type bean 등록
// json 으로 주고 받고 할 것 - @requestbody 포함 어노테이션
@Slf4j //log
@RequiredArgsConstructor
public class DMakerController {

    private final DMakerService dMakerService;

    @GetMapping("/developers")
    public List<DeveloperDto> getAllDevelopers() {
        // controller에 entity 쓰면 좋지 못함 - 유연성, 보안 측면

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
            @PathVariable String memberId
    ) {
        log.info("GET /developers HTTP/1.1");

        return dMakerService.getDeveloperDetail(memberId);
    }

    @PostMapping("/create-developers") // 원래 post가 맞음 그냥 테스트용 ~
    public CreateDeveloper.Response createDevelopers(
            @Valid @RequestBody CreateDeveloper.Request request
            // @Valid: for validation - 진입 전에 exception 발생
            ) {
        log.info("request: {}", request); // dto 파일의 @ToString

        return dMakerService.createDeveloper(request);

        // return Collections.singletonList("lee");
        // 단일 객체일 때에는 Arrays보다 singlletonList가 더 좋다
    }

    @PutMapping("/developers/{memberId}")
    public DeveloperDetailDto editDeveloper(
            @PathVariable String memberId,
            @Valid @RequestBody EditDeveloper.Request request
    ) {
        log.info("GET /developers HTTP/1.1");

        return dMakerService.editDeveloper(memberId, request);
    }

    @DeleteMapping()
    public DeveloperDetailDto deleteDeveloper(
            @PathVariable String memberId
    ){
        return dMakerService.deleteDeveloper(memberId);
    }
}
