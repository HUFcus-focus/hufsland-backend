package com.hufcusfocus.hufsland.domain.entity.auditor;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hufcusfocus.hufsland.domain.entity.user.User;
import lombok.Data;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
public class Auditor {

    @CreatedDate
    @Column(name = "REGISTED_DATETIME", updatable = false, nullable = true)
    @Comment("등록일")
    private LocalDateTime registeredDateTime = LocalDateTime.now();

    @LastModifiedDate
    @Column(name = "MODIFIED_DATETIME")
    @Comment("수정일")
    private LocalDateTime modifiedDateTime;

    @CreatedBy
    @JoinColumn(name = "REGISTER_ID", updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @Comment("등록자")
    private User register;

    @LastModifiedBy
    @JoinColumn(name = "MODIFIER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @Comment("수정자")
    private User modifier;
}
