package com.hikers.hikemate.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "likes")
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 누가 (User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 어떤 글에 (ReviewPost)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_post_id")
    private ReviewPost reviewPost;

    // 기본 생성자 + 생성 편하게 하는 생성자 추가할 수도 있어
    public Like(User user, ReviewPost reviewPost) {
        this.user = user;
        this.reviewPost = reviewPost;
    }
}
