package com.roomerang.repository;

import com.roomerang.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    //사용자 ID를 기준으로 해당 사용자가 참여한 채팅방을 조회
    List<ChatRoom> findByUser1IdOrUser2Id(String user1Id, String user2Id);
}
