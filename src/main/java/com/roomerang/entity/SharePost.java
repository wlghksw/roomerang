package com.roomerang.entity;

import com.roomerang.repository.SharePostRepository;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "TXN_Posts")
public class SharePost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long txnPostId;

    @Column(nullable = false, length = 200)
    private String txnBoardTitle;

    @Column(nullable = false, length = 255)
    private String txnBoardContent;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false, length = 255)
    private String userId;

    @Column(nullable = false, length = 255)
    private String location;

    @Column(nullable = false, length = 255)
    private String authorName;

    @Column(nullable = false)
    private LocalDateTime postDate;

    @Column(nullable = false)
    private Integer viewCount;

    @ElementCollection
    @CollectionTable(name = "sharepost_images", joinColumns = @JoinColumn(name = "txn_post_id"))
    @Column(name = "photo_url", length = 500)
    private List<String> photoUrls = new ArrayList<>();
}

