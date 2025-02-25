package com.roomerang.contoller;

import com.roomerang.entity.*;
import com.roomerang.service.*;
import com.roomerang.util.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final MessageService messageService;
    private final PostService postService;
    private final SharePostService sharePostService;

    // 채팅방 생성
    @PostMapping("/chat/start/{postId}")
    public String startChat(@PathVariable Long postId,
                            @RequestParam("type") String type, //게시글 (수현이가 실패한거 한번 해봤음)
                            HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User loginUser = (session != null) ? (User) session.getAttribute(SessionConst.LOGIN_USER) : null;

        if (loginUser == null) {
            return "redirect:/auth/login";
        }

        String postAuthor = null;


        //if문을 사용하여 type값으로 방 조회
        if ("matching".equals(type)) {
            //매칭해방 게시글 조회
            Post post = postService.getPostById(postId);
            if (post == null) {
                return "redirect:/board/rooms"; //존재하지 않는 경우 리다이렉트
            }
            postAuthor = post.getUserId();
        } else if ("share".equals(type)) {
            //나눔해방 게시글 조회
            SharePost post = sharePostService.getPostById(postId);
            if (post == null) {
                return "redirect:/share/list"; //존재하지 않는 경우 리다이렉트
            }
            postAuthor = post.getUserId();
        } else {
            return "redirect:/";
        }

        //본인 글이면 채팅 불가
        String currentUser = loginUser.getUsername();
        if (postAuthor.equals(currentUser)) {
            return "redirect:/" + (type.equals("matching") ? "board/rooms" : "share/list");
        }

        //채팅방 생성
        ChatRoom chatRoom = chatRoomService.createChatRoom(postAuthor, currentUser, type);
        return "redirect:/chat/room/" + chatRoom.getRoomId();
    }


    //채팅방 목록 조회
    @GetMapping("/chat/list")
    public String chatList(HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        User loginUser = (session != null) ? (User) session.getAttribute(SessionConst.LOGIN_USER) : null;

        if (loginUser == null) {
            return "redirect:/auth/login";  //로그인하지 않으면 로그인 페이지로 리다이렉트
        }

        List<ChatRoom> chatRooms = chatRoomService.getChatRooms(loginUser.getUsername());
        model.addAttribute("chatRooms", chatRooms);

        return "chat/chatList";  //채팅 목록 페이지로 이동
    }

    //채팅방 상세 보기
    @GetMapping("/chat/room/{roomId}")
    public String chatRoom(@PathVariable Long roomId, HttpServletRequest request, Model model) {
        HttpSession session = request.getSession(false);
        User loginUser = (session != null) ? (User) session.getAttribute(SessionConst.LOGIN_USER) : null;

        if (loginUser == null) {
            return "redirect:/auth/login";
        }

        ChatRoom chatRoom = chatRoomService.getChatRoom(roomId);
        if (chatRoom == null) {
            return "redirect:/chat/list";
        }

        List<Message> messages = messageService.getMessagesByRoom(chatRoom);
        model.addAttribute("chatRoom", chatRoom);
        model.addAttribute("messages", messages);

        return "chat/chatRoom";  //채팅방 페이지로 이동
    }

    //메시지 전송 처리
    @PostMapping("/chat/send/{roomId}")
    public String sendMessage(@PathVariable Long roomId, HttpServletRequest request,
                              @RequestParam("messageContent") String messageContent,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        HttpSession session = request.getSession(false);
        User loginUser = (session != null) ? (User) session.getAttribute(SessionConst.LOGIN_USER) : null;

        if (loginUser == null) {
            return "redirect:/auth/login";
        }

        //채팅방 정보 가져오기
        ChatRoom chatRoom = chatRoomService.getChatRoom(roomId);
        if (chatRoom == null) {
            return "redirect:/board/rooms";
        }

        //메시지 저장 (이미지 포함)
        messageService.saveMessage(chatRoom, loginUser.getUsername(), messageContent, imageFile, LocalDateTime.now());

        return "redirect:/chat/room/" + roomId;  // 채팅방으로 리다이렉트
    }


    //채팅방 삭제
    @PostMapping("/chat/delete/{roomId}")
    public String deleteChatRoom(@PathVariable Long roomId, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        User loginUser = (session != null) ? (User) session.getAttribute(SessionConst.LOGIN_USER) : null;

        if (loginUser == null) {
            return "redirect:/auth/login";  // 로그인되지 않으면 로그인 페이지로 리다이렉트
        }

        //해당 채팅방 삭제
        chatRoomService.deleteChatRoom(roomId);

        //채팅방 삭제 후 채팅방 목록 페이지로 리다이렉트
        return "redirect:/chat/list";
    }
}
