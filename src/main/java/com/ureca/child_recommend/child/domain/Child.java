package com.ureca.child_recommend.child.domain;


import com.ureca.child_recommend.global.entity.BaseTimeEntity;
import com.ureca.child_recommend.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Child extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String gender;

    @Column(nullable = false)
    private LocalDate birthday;

    @Column(nullable = false)
    private String profileUrl;

    @OneToOne
    @JoinColumn(name = "childMbti_id")
    private ChildMbti childMbti;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public static Child createChildProfile(String name, String gender, LocalDate birthday, String profileUrl, User user) {
        return Child.builder()
                .name(name)
                .gender(gender)
                .birthday(birthday)
                .profileUrl(profileUrl)
                .user(user)
                .build();
    }

    public void updateChildInfo(String name, String gender, LocalDate birthday, String profileUrl) {
        this.name = name;
        this.gender = gender;
        this.birthday = birthday;
        this.profileUrl = profileUrl;
    }

}
