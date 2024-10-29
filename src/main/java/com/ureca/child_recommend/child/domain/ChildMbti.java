package com.ureca.child_recommend.child.domain;

import com.ureca.child_recommend.child.domain.Enum.ChildMbtiStatus;
import com.ureca.child_recommend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ChildMbti extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "childMbti_id")
    private Long id;

    @Column
    private String mbtiResult;

    @Enumerated(EnumType.STRING)
    private ChildMbtiStatus status; //ACTIVE, NONACTIVE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Child child;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_mbti_score_id")
    private ChildMbtiScore childMbtiScore;

    public void updateStatus(ChildMbtiStatus childMbtiStatus) {
        this.status = childMbtiStatus;
    }


    public static ChildMbti enrollToMbti(String result,Child child,ChildMbtiScore newChildMbtiScore){
        return ChildMbti.builder()
                .mbtiResult(result)
                .status(ChildMbtiStatus.ACTIVE)
                .child(child)
                .childMbtiScore(newChildMbtiScore)
                .build();
    }

}
