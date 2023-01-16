package com.hufcusfocus.hufsland.domain.entity.student;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    @Comment("학번")
    private int id;

    @Comment("닉네임")
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Comment("학년")
    private Grade grade;

    @Comment("생성일자")
    private LocalDateTime createdAt;

    @Comment("삭제일자")
    private LocalDateTime deletedAt;

    @Comment("닉네임 수정일자")
    private LocalDateTime nicknameUpdatedAt;

    @Enumerated(EnumType.STRING)
    @Comment("사용자 유형")
    private StudentType type;
}
