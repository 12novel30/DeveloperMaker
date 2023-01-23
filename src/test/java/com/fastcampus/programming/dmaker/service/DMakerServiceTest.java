package com.fastcampus.programming.dmaker.service;

import com.fastcampus.programming.dmaker.code.DeveloperLevel;
import com.fastcampus.programming.dmaker.code.DeveloperSkillType;
import com.fastcampus.programming.dmaker.code.StatusCode;
import com.fastcampus.programming.dmaker.dto.CreateDeveloper;
import com.fastcampus.programming.dmaker.dto.DeveloperDetailDto;
import com.fastcampus.programming.dmaker.entity.Developer;
import com.fastcampus.programming.dmaker.exception.DMakerException;
import com.fastcampus.programming.dmaker.repository.DeveloperRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.fastcampus.programming.dmaker.code.DeveloperLevel.*;
import static com.fastcampus.programming.dmaker.code.DeveloperSkillType.BACK_END;
import static com.fastcampus.programming.dmaker.constant.DMakerConstant.MAX_JUNIOR_EXPERIENCE_YEARS;
import static com.fastcampus.programming.dmaker.constant.DMakerConstant.MIN_SENIOR_EXPERIENCE_YEARS;
import static com.fastcampus.programming.dmaker.exception.DMakerErrorCode.DUPLICATED_MEMBER_ID;
import static com.fastcampus.programming.dmaker.exception.DMakerErrorCode.LEVEL_EXPERIENCE_YEARS_NOT_MATCHED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

// CAUTION! test 실패하면 gradle -> intellij ide 확인할 것
// @SpringBootTest : 통합 테스트 -> 격리성이 떨어짐
@ExtendWith(MockitoExtension.class) // mock 테스트를 위함
class DMakerServiceTest {

    @Mock //DMakerService 에 선언된 final
    private DeveloperRepository developerRepository;
    //@Autowired -> for Spring boot test
    @InjectMocks
    private DMakerService dMakerService; // new DMakerService() 하지 않음

    private final Developer defaultDeveloper = Developer.builder()
            // 자주 사용하는 entity/dto 의 경우 미리 선언해두는 것이 좋음
                        .developerLevel(SENIOR)
                        .developerSkillType(BACK_END)
                        .experienceYears(MIN_SENIOR_EXPERIENCE_YEARS + 1)
                        .statusCode(StatusCode.EMPLOYED)
                        .name("name")
                        .age(32)
                        .build();

    private CreateDeveloper.Request getCreateRequest(
            DeveloperLevel developerLevel,
            DeveloperSkillType developerSkillType,
            Integer experienceYears,
            String memberId
    ){
        return CreateDeveloper.Request.builder()
                .developerLevel(developerLevel)
                .developerSkillType(developerSkillType)
                .experienceYears(experienceYears)
                .memberId(memberId)
                .name("name")
                .age(23)
                .build();
    }

    @Test
    public void getDeveloperDetailTest(){
        //given
        given(developerRepository.findByMemberId(anyString()))
                .willReturn(Optional.of(defaultDeveloper));

        //when
        DeveloperDetailDto developerDetailDto =
                dMakerService.getDeveloperDetail("memberId");

        //then
        assertEquals(SENIOR, developerDetailDto.getDeveloperLevel());
        assertEquals(BACK_END, developerDetailDto.getDeveloperSkillType());
        assertEquals(MIN_SENIOR_EXPERIENCE_YEARS + 1,
                developerDetailDto.getExperienceYears());
    }

    @Test
    void createDeveloperTest_success(){
        // given
        given(developerRepository.findByMemberId(anyString()))
                .willReturn(Optional.empty());
        given(developerRepository.save(any()))
                .willReturn(defaultDeveloper); // mocking 이 빠지면 에러남
        ArgumentCaptor<Developer> captor = // db 에 저장되는 데이터 or 외부로 보내지는 데이터를 확인
                ArgumentCaptor.forClass(Developer.class);

        // when
        dMakerService.createDeveloper(getCreateRequest(
                SENIOR, BACK_END, MIN_SENIOR_EXPERIENCE_YEARS+1,
                "createDeveloperTest_success"));
        // -> 타고 들어가면 mocking 해주어야 할 부분이 생김
        // -> given 으로 처리

        // then
        verify(developerRepository, times(1))
                .save(captor.capture()); // save 함수 실행 횟수 확인
        Developer savedDeveloper = captor.getValue();
        assertEquals(SENIOR, savedDeveloper.getDeveloperLevel());
        assertEquals(BACK_END, savedDeveloper.getDeveloperSkillType());
        assertEquals(MIN_SENIOR_EXPERIENCE_YEARS+1, savedDeveloper.getExperienceYears());
    }

    @Test
    void createDeveloperTest_failed_with_duplicated(){
        // given
        given(developerRepository.findByMemberId(anyString()))
                .willReturn(Optional.of(defaultDeveloper));

        // when
        // then
        DMakerException dMakerException = assertThrows(DMakerException.class,
                () -> dMakerService.createDeveloper(getCreateRequest(
                        SENIOR, BACK_END, MIN_SENIOR_EXPERIENCE_YEARS+1,
                        "createDeveloperTest_failed_with_duplicated"))
        );
        assertEquals(DUPLICATED_MEMBER_ID, dMakerException.getDmakerErrorCode());
    }

    @Test
    void createDeveloperTest_fail_low_senior(){
        // given
        // when
        // then - JUNIOR
        DMakerException dMakerException = assertThrows(DMakerException.class,
                () -> dMakerService.createDeveloper(
                        getCreateRequest(
                                JUNIOR, BACK_END, MAX_JUNIOR_EXPERIENCE_YEARS+1,
                                "createDeveloperTest_fail_low_senior"))
        ); // set 으로 바꾸면 전부 그렇게 됨
        assertEquals(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED,
                dMakerException.getDmakerErrorCode());
/*
        // then - JUNGNIOR
        dMakerException = assertThrows(DMakerException.class,
                () -> dMakerService.createDeveloper(
                        getCreateRequest(
                                JUNGNIOR, BACK_END, MIN_SENIOR_EXPERIENCE_YEARS+1,
                                "createDeveloperTest_fail_low_senior"))
        );
        // then - SENIOR
        dMakerException = assertThrows(DMakerException.class,
                () -> dMakerService.createDeveloper(
                        getCreateRequest(
                                SENIOR, BACK_END, MIN_SENIOR_EXPERIENCE_YEARS-1,
                                "createDeveloperTest_fail_low_senior"))
        );*/
    }
}