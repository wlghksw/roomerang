package com.roomerang.service;

import com.roomerang.entity.ChatRoom;
import com.roomerang.entity.Message;
import com.roomerang.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final FileStorageService fileStorageService;

    //ChatRoom, String, String을 받는 saveMessage 메서드
    public Message saveMessage(ChatRoom chatRoom, String senderId, String messageContent, MultipartFile imageFile, LocalDateTime sentAt) {
        String imageUrl = null;

        //이미지 파일이 있으면 저장 후 URL 반환
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = fileStorageService.storeFile(imageFile);
        }

        Message message = new Message(chatRoom, senderId, messageContent, imageUrl, sentAt);
        return messageRepository.save(message);
    }


    //채팅방의 모든 메시지 조회
    public List<Message> getMessagesByRoom(ChatRoom chatRoom) {
        return messageRepository.findByChatRoom(chatRoom);
    }
}
