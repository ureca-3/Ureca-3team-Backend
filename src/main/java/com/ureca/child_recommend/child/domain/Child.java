package com.ureca.child_recommend.child.domain;


import com.ureca.child_recommend.child.domain.Enum.ChildStatus;
import com.ureca.child_recommend.global.entity.BaseTimeEntity;
import com.ureca.child_recommend.user.domain.Users;
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
    private Integer age;

    @Column(nullable = false)
    private String profileUrl;

    private Integer currentFeedBackCount;


    @Enumerated(EnumType.STRING)
    private ChildStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    public static Child createChildProfile(String name, String gender, LocalDate birthday, String profileUrl, Integer age,  Users user) {
        return Child.builder()
                .name(name)
                .gender(gender)
                .birthday(birthday)
                .profileUrl(profileUrl)
                .age(age)
                .user(user)
                .currentFeedBackCount(0)
                .status(ChildStatus.ACTIVE)
                .build();
    }

    public void updateChildInfo(String name, String gender, LocalDate birthday, String profileUrl, Integer age) {
        if (name != null) {
            this.name = name; // 이름 업데이트
        }
        if (gender != null) {
            this.gender = gender; // 성별 업데이트
        }
        if (birthday != null) {
            this.birthday = birthday; // 생일 업데이트
        }
        if (profileUrl != null) {
            this.profileUrl = profileUrl; // 프로필 URL 업데이트
        }
        if (age != null) {
            this.age = age; // 나이 업데이트
        }
    }

    public static void updateChildStatus(Child child) {
        child.status = ChildStatus.NONACTIVE;
    }

    public void setProfileUrl(String profileUrl) {this.profileUrl = profileUrl; }

    public void upCurrentFeedBackCount(){
        currentFeedBackCount +=1;
    }
    public void downCurrentFeedBackCount(){
        currentFeedBackCount -=1;
    }

    public void resetCurrentFeedBackCount(){
        currentFeedBackCount -=1;
    }


}
