package com.hufcusfocus.hufsland.domain.entity.major;

import com.hufcusfocus.hufsland.domain.entity.department.Department;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "major_id")
    @Comment("아이디")
    private int id;

    @ManyToOne
    @JoinColumn(name = "department_id")
    @Comment("학부")
    private Department department;

    @Comment("학과명")
    private String name;
}
