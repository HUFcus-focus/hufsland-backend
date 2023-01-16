package com.hufcusfocus.hufsland.domain.entity.result;

import com.hufcusfocus.hufsland.domain.entity.student.Student;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    @Comment("아이디")
    private int id;

    @OneToOne
    @JoinColumn(name = "student_id")
    @Comment("학생")
    private Student student;

    @Comment("학점")
    private double score;
}
