package com.ureca.child_recommend.user.domain;

import com.ureca.child_recommend.child.domain.Child;
import com.ureca.child_recommend.global.entity.BaseTimeEntity;
import com.ureca.child_recommend.user.domain.Enum.UserRole;
import com.ureca.child_recommend.user.domain.Enum.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column
    private String nickname;

    @Column
    private String profileUrl;

    @Column
    private String email;

    @Column
    private String gender;

    @Column
    private Integer age;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    private UserRole role;

//    @OneToMany(mappedBy = "child",cascade = CascadeType.ALL)
//    List<Child> childList  =new ArrayList<>();

}
