package com.ureca.child_recommend.config.oauth.dto;

import com.ureca.child_recommend.user.domain.Enum.SocialType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OauthInfo {

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String oid;

    private String nickname;

    private String profileUrl;

    private String email;

    private String gender;

    private String ageRange;

    @Builder
    public OauthInfo(SocialType socialType, String oid, String nickname,String email,String profileUrl, String gender,String ageRange){
        this.socialType =socialType;
        this.oid =oid;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        this.email = email;
        this.gender = gender;
        this.ageRange = ageRange;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setEmail(String email) {this.email = email; }

    public void setGender(String gender) {this.gender = gender; }

    public void setAgeRange(String ageRange) {this.ageRange = ageRange; }


}
