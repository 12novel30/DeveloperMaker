package com.fastcampus.programming.dmaker.entity;

import com.fastcampus.programming.dmaker.code.StatusCode;
import com.fastcampus.programming.dmaker.code.DeveloperLevel;
import com.fastcampus.programming.dmaker.code.DeveloperSkillType;
import com.fastcampus.programming.dmaker.dto.CreateDeveloper;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class) // @EnableJpaAuditing
public class Developer {
    // DB에 접근할 때 사용하는 entity 의 경우,
    // 항상 모든 값을 사용하지 않을 수 있음 ->
    // 목적에 맞게 적절히 entity 를 분리하는 것이 좋음
    // <- 쓰지않는 값을 항상 할당하는 것도 귀찮음
    // -> DB를 이런 목적으로 분리하는 건가?

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 기본 키 생성을 DB에 맡긴다 ->
    // id를 null 로 하면 DB가 알아서 값을 할당
    // ex. auto_increment 등
    protected Long id;

    @Enumerated(EnumType.STRING) // enum 을 속성으로 사용할 때
    // EnumType.ORDINAL 도 있음
    private DeveloperLevel developerLevel;

    @Enumerated(EnumType.STRING)
    private DeveloperSkillType developerSkillType;

    private Integer experienceYears;
    private String memberId;
    private String name;
    private Integer age;

    @Enumerated(EnumType.STRING)
    private StatusCode statusCode;

    // @EnableJpaAuditing in DMakerApplication.java
    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // entity 에 메소드를 두는 것은 좋지 못함
}
