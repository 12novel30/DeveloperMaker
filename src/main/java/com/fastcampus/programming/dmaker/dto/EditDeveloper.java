package com.fastcampus.programming.dmaker.dto;

import com.fastcampus.programming.dmaker.code.DeveloperLevel;
import com.fastcampus.programming.dmaker.code.DeveloperSkillType;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class EditDeveloper {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @ToString
    public static class Request{
        @NotNull
        private DeveloperLevel developerLevel;
        @NotNull
        private DeveloperSkillType developerSkillType;
        @NotNull
        @Min(0)
        @Max(20)
        private Integer experienceYears;
        // 수정할 필요 없는 정보
//        @NotNull
//        @Size(min = 3, max = 50, message = "memberId size must be 3~50")
//        private String memberId;
//        @NotNull
//        @Size(min = 3, max = 20, message = "name size must be 3~20")
//        private String name;
//        @Min(18)
//        private Integer age;
    }
}
