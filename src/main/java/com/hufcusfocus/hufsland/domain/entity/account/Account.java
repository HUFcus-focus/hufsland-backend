package com.hufcusfocus.hufsland.domain.entity.account;

import com.hufcusfocus.hufsland.domain.entity.student.Student;
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
    @Column(name = "account_id")
    @Comment("아이디")
    private int id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @Comment("학번")
    private Student student;

    @Comment("이메일")
    private String email;

    @Enumerated(EnumType.STRING)
    @Comment("소셜 서비스")
    private Provider provider;

    @Builder
    public Account(String email, Provider provider) {
        this.email = email;
        this.provider = provider;
    }
}
