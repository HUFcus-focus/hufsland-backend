package com.hufcusfocus.hufsland.domain.entity.department;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    @Comment("아이디")
    private int id;

    @Comment("학부명")
    private String name;
}
