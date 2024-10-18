    package com.ureca.child_recommend.user.domain;


import com.ureca.child_recommend.config.oauth.dto.OauthInfo;
import com.ureca.child_recommend.global.entity.BaseTimeEntity;
import com.ureca.child_recommend.user.domain.Enum.UserRole;
import com.ureca.child_recommend.user.domain.Enum.UserStatus;
import jakarta.persistence.*;
import lombok.*;


@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class User extends BaseTimeEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "user_id")
        private Long id;

        @Embedded
        private OauthInfo oauthInfo;

        @Enumerated(EnumType.STRING)
        private UserStatus status;

        @Enumerated(EnumType.STRING)
        @Column(length = 20)
        private UserRole role;

        @Column(length = 50)
        private String nickname;  // 닉네임 추가

        @Column(length = 15)
        private String phone;  // 전화번호 추가

    //    @OneToMany(mappedBy = "child",cascade = CascadeType.ALL)
    //    List<Child> childList  =new ArrayList<>();

        //== 테스트 ==//
        public String test(){
            System.out.println("ㅎㅎ");
            return "user";
        }

        private User(OauthInfo oauthInfo){
            this.oauthInfo = oauthInfo;
            this.status = UserStatus.ACTIVE;
            this.role = UserRole.USER;
        }

        public static User create(OauthInfo oauthInfo){
            return new User(oauthInfo);
        }

        public void updateNickname(String nickname) {
            this.nickname = nickname;
        }

        public void updatePhone(String phone) {
            this.phone = phone;
        }

    public void updateStatus(UserStatus status) {this.status = status;}

    }
