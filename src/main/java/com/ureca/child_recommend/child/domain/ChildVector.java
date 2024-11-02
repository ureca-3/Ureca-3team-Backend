package com.ureca.child_recommend.child.domain;

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
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChildVector extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "child_vector_id")
    private Long id;

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 1536)
    private float[] embedding;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id")
    private Child child;



    public static ChildVector createChildVector(float[] childEmbedding,Child child){
        return ChildVector.builder()
                .embedding(childEmbedding)
                .child(child)
                .build();

    }

}
