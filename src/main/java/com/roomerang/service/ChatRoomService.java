package com.roomerang.service;

import com.roomerang.entity.ChatRoom;
import com.roomerang.entity.Message;
import com.roomerang.repository.ChatRoomRepository;
import com.roomerang.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;

    // 채팅방 목록 조회 (로그인한 사용자가 참여한 모든 채팅방)
    public List<ChatRoom> getChatRooms(String userId) {
        return chatRoomRepository.findByUser1IdOrUser2Id(userId, userId);
    }

    // 채팅방 생성
    public ChatRoom createChatRoom(String user1Id, String user2Id, String type) {
        if (user1Id.equals(user2Id)) {
            throw new IllegalArgumentException("같은 사용자는 채팅을 시작할 수 없습니다.");
        }
        ChatRoom chatRoom = new ChatRoom(user1Id, user2Id, type);
        return chatRoomRepository.save(chatRoom);
    }

    // 특정 채팅방 조회
    public ChatRoom getChatRoom(Long roomId) {
        return chatRoomRepository.findById(roomId).orElse(null);
    }

    // 채팅방 삭제
    public void deleteChatRoom(Long roomId) {
        // 채팅방을 ID로 찾기
        ChatRoom chatRoom = new ChatRoom(roomId);  // roomId만으로 ChatRoom 객체 생성

        // 채팅방에 속한 모든 메시지 삭제
        List<Message> messages = messageRepository.findByChatRoom(chatRoom);
        if (!messages.isEmpty()) {
            messageRepository.deleteAll(messages);  // 메시지 삭제
        }

        // 채팅방 삭제
        chatRoomRepository.deleteById(roomId);  // 채팅방 삭제
    }
}
