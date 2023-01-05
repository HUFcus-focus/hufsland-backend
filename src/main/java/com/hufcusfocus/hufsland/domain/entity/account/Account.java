package com.hufcusfocus.hufsland.domain.entity.account;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("아이디")
    private int id;

    @Comment("학번")
    private int studentId;

    @Comment("이메일")
    private String email;

    @Enumerated(EnumType.STRING)
    @Comment("소셜 서비스")
    private Provider provider;

    @Builder
    public Account(int studentId, String email, Provider provider) {
        this.studentId = studentId;
        this.email = email;
        this.provider = provider;
    }
}
