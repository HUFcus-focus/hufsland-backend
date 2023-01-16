package com.hufcusfocus.hufsland.domain.entity.studentMajor;

import com.hufcusfocus.hufsland.domain.entity.major.Major;
import com.hufcusfocus.hufsland.domain.entity.student.Student;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class StudentMajor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_major_id")
    @Comment("아이디")
    private int id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    @Comment("학생")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "major_id")
    @Comment("학과")
    private Major major;

    @Enumerated(EnumType.STRING)
    @Comment("전공형태")
    private MajorType type;
}
