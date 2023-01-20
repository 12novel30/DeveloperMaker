package com.fastcampus.programming.dmaker.service;

import com.fastcampus.programming.dmaker.code.StatusCode;
import com.fastcampus.programming.dmaker.dto.CreateDeveloper;
import com.fastcampus.programming.dmaker.dto.DeveloperDetailDto;
import com.fastcampus.programming.dmaker.dto.DeveloperDto;
import com.fastcampus.programming.dmaker.dto.EditDeveloper;
import com.fastcampus.programming.dmaker.entity.Developer;
import com.fastcampus.programming.dmaker.entity.RetiredDeveloper;
import com.fastcampus.programming.dmaker.exception.DmakerException;
import com.fastcampus.programming.dmaker.repository.DeveloperRepository;
import com.fastcampus.programming.dmaker.repository.RetiredDeveloperRepository;
import com.fastcampus.programming.dmaker.type.DeveloperLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.fastcampus.programming.dmaker.exception.DmakerErrorCode.*;

@Service
@RequiredArgsConstructor // repository를 인젝션 할 필요 없음
// 생성자도 필요없음

public class DMakerService {
    private final DeveloperRepository developerRepository;
    private final RetiredDeveloperRepository retiredDeveloperRepository;

    @Transactional
    /* [Transactional 개념]
    * ACID - DB 구성 규칙(atomic, consistency, isolation, durability
    * - 스프링의 관리 방식
    *   EntityManager를 불러와서, try-catch 문을 이용
    *   에러나면 rollback
    * -> annotation 기반 pointcut (AOP)
    * -> 해당 함수들을 삭제하고 AOP로 관리*/
    public CreateDeveloper.Response createDeveloper(CreateDeveloper.Request request){

        validateCreateDeveloperRequest(request);
        // business logic start
        Developer developer = Developer.builder()
                .developerLevel(request.getDeveloperLevel())
                .developerSkillType(request.getDeveloperSkillType())
                .experienceYears(request.getExperienceYears())
                .memberId(request.getMemberId())
                .statusCode(StatusCode.EMPLOYED)
                .name(request.getName())
                .age(request.getAge())
                .build();
        developerRepository.save(developer);
        // business logic end

        // transactional 이 있었던 위치

        return CreateDeveloper.Response.fromEntity(developer);
    }

    private void validateCreateDeveloperRequest(CreateDeveloper.Request request) {
        // business validation
        // 단축키로 중복되는 변수를 선언 -> extract 이후 사라짐
        // 서비스에 필요한 익셉션을 개별적으로 만들어주는 것이 좋음
        // 중복되는 함수는 extract
        validateDeveloperLevel(
                request.getDeveloperLevel(),
                request.getExperienceYears());

        // Optional 사용 - ifPresent
        developerRepository.findByMemberId(request.getMemberId())
                .ifPresent((developer -> {
                    throw new DmakerException(DUPLICATED_MEMBER_ID);
                }));

    }

    public List<DeveloperDto> getAllEmployedDevelopers() {
        return developerRepository.findDevelopersByStatusCodeEquals(StatusCode.EMPLOYED)
                .stream()
                .map(DeveloperDto::fromEntity) // entity -> dto
                .collect(Collectors.toList()); // 리스트로
    }

    public DeveloperDetailDto getDeveloperDetail(String memberId) {
        return developerRepository.findByMemberId(memberId)
                .map(DeveloperDetailDto::fromEntity)
                //.get() 보다는
                .orElseThrow(() -> new DmakerException(NO_DEVELOPER));
    }

    @Transactional
    public DeveloperDetailDto editDeveloper(String memberId, EditDeveloper.Request request) {
        validateEditDeveloperRequest(request, memberId);

        Developer developer = developerRepository.findByMemberId(memberId).orElseThrow(
                () -> new DmakerException(NO_DEVELOPER)
        );

        developer.setDeveloperLevel(request.getDeveloperLevel());
        developer.setDeveloperSkillType(request.getDeveloperSkillType());
        developer.setExperienceYears(request.getExperienceYears());

        return DeveloperDetailDto.fromEntity(developer);
    }

    private void validateEditDeveloperRequest(
            EditDeveloper.Request request,
            String memberId) {
        // 서비스에 필요한 익셉션을 개별적으로 만들어주는 것이 좋음
        validateDeveloperLevel(
                request.getDeveloperLevel(),
                request.getExperienceYears()
        );

    }

    private static void validateDeveloperLevel(DeveloperLevel developerLevel, Integer experienceYears) {
        if (developerLevel == DeveloperLevel.SENIOR
                && experienceYears < 10){
            throw new DmakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }
        if (developerLevel == DeveloperLevel.JUNIOR
                && (experienceYears < 4 ||
                experienceYears > 10)) {
            throw new DmakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }
        if (developerLevel == DeveloperLevel.JUNIOR
                && experienceYears > 4) {
            throw new DmakerException(LEVEL_EXPERIENCE_YEARS_NOT_MATCHED);
        }
    }


    @Transactional //DB 접근(수정)이 있으면 넣어주는 것이 좋음
    public DeveloperDetailDto deleteDeveloper(String memberId) {
        // 1. EMPLOYED -> RETIRED
        Developer developer = developerRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DmakerException(NO_DEVELOPER));
        developer.setStatusCode(StatusCode.RETIRED);

        if (developer != null) throw new DmakerException(NO_DEVELOPER);

        // 2. save into RetiredDeveloper
        RetiredDeveloper retiredDeveloper = RetiredDeveloper.builder()
                .memberId(memberId)
                .name(developer.getName())
                .build();
        retiredDeveloperRepository.save(retiredDeveloper);
        return DeveloperDetailDto.fromEntity(developer);
    }
}