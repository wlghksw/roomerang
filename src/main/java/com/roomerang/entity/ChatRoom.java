package com.roomerang.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false)
    private String user1Id;

    @Column(nullable = false)
    private String user2Id;

    @Column(nullable = false)
    private String type;

    //기존 생성자
    public ChatRoom(String user1Id, String user2Id, String type) {
        this.user1Id = user1Id;
        this.user2Id = user2Id;
        this.type = type;
    }

    //추가된 생성자: roomId만으로 생성할 수 있도록 생성자 하나로 통일해도 되지만 구분을 위해서 두개로 나눔
    public ChatRoom(Long roomId) {
        this.roomId = roomId;
    }
}

