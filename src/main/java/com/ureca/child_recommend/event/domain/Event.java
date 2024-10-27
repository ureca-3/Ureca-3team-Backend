package com.ureca.child_recommend.event.domain;

import com.ureca.child_recommend.contents.domain.ContentsMbtiScore;
import com.ureca.child_recommend.contents.domain.Enum.ContentsStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long id;

    @Column
    private String name;

    @Column
    private LocalDateTime date;

    @Column(columnDefinition = "TEXT")
    private String description;

//    @OneToMany
//    @JoinColumn(name = "contentsMbti_id")
//    private ContentsMbtiScore contentsMbti;

//    @OneToMany(mappedBy = "contents",cascade = CascadeType.ALL)
//    List<FeedBack> feedBackList = new ArrayList<>();
}

