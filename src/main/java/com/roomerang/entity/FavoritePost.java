package com.roomerang.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "Favorite_Posts")
public class FavoritePost {

    @ManyToOne
    @JoinColumn(name = "match_post_id")
    private Post post; // Post 엔티티와 연관 관계 설정

    @ManyToOne
    @JoinColumn(name = "share_post_id")
    private SharePost sharePost; // Post 엔티티와 연관 관계 설정

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long favoriteId;

    @Column(nullable = false)
    private Long userId; // 관심글을 등록한 사용자

    @Column(nullable = false)
    private Long postId; // 관심 등록한 게시글 ID

    @Column(nullable = false)
    private String postType; // "MATCH" (매칭해방) 또는 "SHARE" (나눔해방)

    @Column(nullable = false)
    private String postTitle; //게시글 제목

}
