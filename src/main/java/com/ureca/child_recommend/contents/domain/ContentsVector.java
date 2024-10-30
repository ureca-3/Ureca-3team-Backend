package com.ureca.child_recommend.contents.domain;

import com.ureca.child_recommend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ContentsVector extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contentsMbtiScore_id")
    private Long id;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 1536)
    private float[] embedding;

    @OneToOne
    @JoinColumn(name = "contents_id")
    private Contents contents;

    public static ContentsVector createContentsVector(float[] contentsEmbedding, Contents contents){
        return ContentsVector.builder()
                .embedding(contentsEmbedding)
                .contents(contents)
                .build();

    }
}
