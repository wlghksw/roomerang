package com.roomerang.repository;

import com.roomerang.entity.ChatRoom;
import com.roomerang.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // 특정 채팅방의 모든 메시지 조회
    List<Message> findByChatRoom(ChatRoom chatRoom);
}
