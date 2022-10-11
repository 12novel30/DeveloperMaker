package com.fastcampus.programming.dmaker.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController //restController type bean 등록
                // json 으로 주고 받고 할 것 - @requestbody 포함 어노테이션
@Slf4j //log
public class DMakerController {
    @GetMapping("/developers")
    public List<String> getAllDevelopers(){
        log.info("GET /developers HTTP/1.1");
        //2022-10-11 17:41:53.348  INFO 23224 --- [nio-8080-exec-1] c.f.p.d.controller.DMakerController      : GET /developers HTTP/1.1

        return Arrays.asList("lee", "ha", "eun");
    }
}
