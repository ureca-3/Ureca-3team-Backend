package com.ureca.child_recommend.contents.domain;

import com.ureca.child_recommend.global.entity.BaseTimeEntity;
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
public class ContentsMbtiScore extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contentsMbtiScore_id")
    private Long id;

    @Column
     private Integer eiScore;

    @Column
    private Integer snScore;

    @Column
    private Integer tfScore;

    @Column
    private Integer jpScore;

    public static ContentsMbtiScore saveContentsMbti(int eiScore, int snScore, int tfScore, int jpScore) {
        return ContentsMbtiScore.builder()
                .eiScore(eiScore)
                .snScore(snScore)
                .tfScore(tfScore)
                .jpScore(jpScore)
                .build();
    }

}
