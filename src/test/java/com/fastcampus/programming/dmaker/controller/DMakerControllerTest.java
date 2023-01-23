package com.fastcampus.programming.dmaker.controller;

import com.fastcampus.programming.dmaker.code.DeveloperLevel;
import com.fastcampus.programming.dmaker.code.StatusCode;
import com.fastcampus.programming.dmaker.dto.CreateDeveloper;
import com.fastcampus.programming.dmaker.dto.DeveloperDto;
import com.fastcampus.programming.dmaker.entity.Developer;
import com.fastcampus.programming.dmaker.repository.DeveloperRepository;
import com.fastcampus.programming.dmaker.service.DMakerService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

import static com.fastcampus.programming.dmaker.code.DeveloperLevel.JUNIOR;
import static com.fastcampus.programming.dmaker.code.DeveloperLevel.SENIOR;
import static com.fastcampus.programming.dmaker.code.DeveloperSkillType.BACK_END;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DMakerController.class)
// Mockito 랑 유사,
// 특정 controller 만 하기 좋음
class DMakerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DMakerService dMakerService;

    protected MediaType contentType = new MediaType(
            MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            StandardCharsets.UTF_8); // 자주 쓸 리턴 타입 선언

    @Test
    void getAllDevelopers() throws Exception{

        DeveloperDto juniorDeveloperDto = DeveloperDto.builder()
                .developerLevel(JUNIOR)
                .developerSkillType(BACK_END)
                .memberId("memberId1")
                .build();
        DeveloperDto seniorDeveloperDto = DeveloperDto.builder()
                .developerLevel(SENIOR)
                .developerSkillType(BACK_END)
                .memberId("memberId2")
                .build();
        given(dMakerService.getAllEmployedDevelopers())
                .willReturn(Arrays.asList(
                        juniorDeveloperDto, seniorDeveloperDto));

        mockMvc.perform(get("/developers").contentType(contentType))
                .andExpect(status().isOk())
                .andDo(print()) // 출력까지!
                .andExpect(
                        jsonPath("$.[0].developerSkillType",
                                is(BACK_END.name()))
                ).andExpect(
                        jsonPath("$.[0].developerLevel",
                                is(JUNIOR.name()))
                ).andExpect(
                        jsonPath("$.[1].developerSkillType",
                                is(BACK_END.name()))
                ).andExpect(
                        jsonPath("$.[1].developerLevel",
                                is(SENIOR.name()))
                );
    }

}