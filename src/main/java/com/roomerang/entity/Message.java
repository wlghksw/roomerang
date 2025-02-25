package com.roomerang.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private ChatRoom chatRoom;

    @Column(nullable = false)
    private String senderId;

    @Column(nullable = false, length = 500)
    private String messageContent;

    @Column
    private String imageUrl;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    public Message(ChatRoom chatRoom, String senderId, String messageContent, String imageUrl, LocalDateTime sentAt) {
        this.chatRoom = chatRoom;
        this.senderId = senderId;
        this.messageContent = messageContent;
        this.imageUrl = imageUrl;
        this.sentAt = sentAt;
    }

    public String getOtherUser(String currentUserId) {
        // 현재 사용자와 채팅방에 참여한 두 사용자의 ID를 비교하여 상대방을 반환
        if (chatRoom.getUser1Id().equals(currentUserId)) {
            return chatRoom.getUser2Id();
        }
        return chatRoom.getUser1Id();
    }
}



