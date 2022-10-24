package com.ll.ebook.app.member.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ll.ebook.app.base.entity.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Member extends BaseEntity {
    @Column(unique = true)
    private String username;
    @JsonIgnore
    private String password;
    private String nickname;
    private String email;
    private int authLevel;

    public Member(long id) {
        super(id);
    }

    public String getName() {
        if (nickname != null) {
            return nickname;
        }

        return username;
    }

    public String getJdenticon() {
        return "member__" + getId();
    }
}