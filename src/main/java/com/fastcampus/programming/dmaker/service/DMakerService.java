package com.fastcampus.programming.dmaker.service;

import com.fastcampus.programming.dmaker.code.StatusCode;
import com.fastcampus.programming.dmaker.dto.CreateDeveloper;
import com.fastcampus.programming.dmaker.dto.DeveloperDetailDto;
import com.fastcampus.programming.dmaker.dto.DeveloperDto;
import com.fastcampus.programming.dmaker.dto.EditDeveloper;
import com.fastcampus.programming.dmaker.entity.Developer;
import com.fastcampus.programming.dmaker.entity.RetiredDeveloper;
import com.fastcampus.programming.dmaker.exception.DMakerException;
import com.fastcampus.programming.dmaker.repository.DeveloperRepository;
import com.fastcampus.programming.dmaker.repository.RetiredDeveloperRepository;
import com.fastcampus.programming.dmaker.code.DeveloperLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

import static com.fastcampus.programming.dmaker.constant.DMakerConstant.MAX_JUNIOR_EXPERIENCE_YEARS;
import static com.fastcampus.programming.dmaker.constant.DMakerConstant.MIN_SENIOR_EXPERIENCE_YEARS;
import static com.fastcampus.programming.dmaker.exception.DMakerErrorCode.*;

@Service
@RequiredArgsConstructor
public class DMakerService {
    @Value("${developer.level.min.senior}") // [SpEL]
    private final Integer minSeniorYears;
    private final DeveloperRepository developerRepository;
    private final RetiredDeveloperRepository retiredDeveloperRepository;

    @Transactional // [AOP]
    //DB 접근(수정)이 있으면 넣어주는 것이 좋음
    /* [Transactional 개념]
    * ACID - DB 구성 규칙(atomic, consistency, isolation, durability
    * - 스프링의 관리 방식
    *   EntityManager 를 불러와서, try-catch 문을 이용
    *   에러나면 rollback
    * -> annotation 기반 pointcut (AOP)
    * -> 해당 함수들을 삭제하고 AOP 로 관리*/
    public CreateDeveloper.Response createDeveloper(CreateDeveloper.Request request){
        validateCreateDeveloperRequest(request);
        // (transactional 원래 함수가 있었던 위치)
        return CreateDeveloper.Response.fromEntity(
                developerRepository.save(createDeveloperFromRequest(request)));
        // 지역변수를 웬만하면 만들지 않는 편이 좋음
    }
    public static Developer createDeveloperFromRequest(CreateDeveloper.Request request){
        return Developer.builder()
                .developerLevel(request.getDeveloperLevel())
                .developerSkillType(request.getDeveloperSkillType())
                .experienceYears(request.getExperienceYears())
                .memberId(request.getMemberId())
                .statusCode(StatusCode.EMPLOYED)
                .name(request.getName())
                .age(request.getAge())
                .build();
    }

    private void validateCreateDeveloperRequest(
            @NonNull CreateDeveloper.Request request) { // [Null-safety]
        // try - catch 는 이제 잘 안함 - 성공에 관한 깔끔한 코드를 작성할 수 있음

        // business validation

        // 단축키로 중복되는 변수를 선언 -> extract 이후 사라짐
        // 서비스에 필요한 익셉션을 개별적으로 만들어주는 것이 좋음
        // 중복되는 함수는 extract
        request.getDeveloperLevel().validateExperienceYears(
                request.getExperienceYears());

        // Optional 사용 - ifPresent
        developerRepository.findByMemberId(request.getMemberId())
                .ifPresent((developer -> {
                    throw new DMakerException(DUPLICATED_MEMBER_ID);
                }));

    }
    @Transactional(readOnly = true) // 내부에서 데이터가 바뀌지 않도록 할 수 있음
    public List<DeveloperDto> getAllEmployedDevelopers() {
        return developerRepository.findDevelopersByStatusCodeEquals(StatusCode.EMPLOYED)
                .stream() // 컬렉션의 요소를 하나씩 참조, 람다식으로 처리하는 반복자
                .map(DeveloperDto::fromEntity) // entity -> dto
                .collect(Collectors.toList()); // 리스트로
    }
    @Transactional(readOnly = true)
    public DeveloperDetailDto getDeveloperDetail(String memberId) {
        return DeveloperDetailDto.fromEntity(getDeveloperByMemberId(memberId));
    }
    private Developer getDeveloperByMemberId(String memberId){
        // naming convention ** get~ : 반드시 ~가 존재해야함
        return developerRepository.findByMemberId(memberId)
                .orElseThrow( () -> new DMakerException(NO_DEVELOPER));
    }
    @Transactional(readOnly = true)
    public DeveloperDetailDto editDeveloper(String memberId, EditDeveloper.Request request) {

        request.getDeveloperLevel().validateExperienceYears(
                request.getExperienceYears());
        // public 메소드에는 큼직한 내용을 담고
        // private 메소드에 상세 사항을 구현할 것

        // 수정할 때는, entity 로 받아와서 set 으로 수정
        // @Transactional 가 Dirty Checking 을 동작,
        // 알아서 DB에 반영해주기 때문에
        // 별도로 save 는 필요없다.
        // ** save 는 create, delete 에서만 사용
        return DeveloperDetailDto.fromEntity(
                getUpdatedDeveloperFromRequest(request, getDeveloperByMemberId(memberId)));
    }

    private Developer getUpdatedDeveloperFromRequest(EditDeveloper.Request request, Developer developer) {
        developer.setDeveloperLevel(request.getDeveloperLevel());
        developer.setDeveloperSkillType(request.getDeveloperSkillType());
        developer.setExperienceYears(request.getExperienceYears());

        return developer;
    }

    private static void validateDeveloperLevel(
            DeveloperLevel developerLevel, Integer experienceYears) {
        developerLevel.validateExperienceYears(experienceYears);
    }

    @Transactional
    public DeveloperDetailDto deleteDeveloper(String memberId) {
        // 1. EMPLOYED -> RETIRED
        Developer developer = developerRepository.findByMemberId(memberId)
                .orElseThrow(() -> new DMakerException(NO_DEVELOPER));
        developer.setStatusCode(StatusCode.RETIRED);

        if (developer != null) throw new DMakerException(NO_DEVELOPER);

        // 2. save into RetiredDeveloper
        RetiredDeveloper retiredDeveloper = RetiredDeveloper.builder()
                .memberId(memberId)
                .name(developer.getName())
                .build();
        retiredDeveloperRepository.save(retiredDeveloper);
        return DeveloperDetailDto.fromEntity(developer);
    }
}