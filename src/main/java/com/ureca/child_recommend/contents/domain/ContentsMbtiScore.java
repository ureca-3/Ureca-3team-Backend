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
    private Integer ieScore;

    @Column
    private Integer snScore;

    @Column
    private Integer ftScore;

    @Column
    private Integer jpScore;

}
