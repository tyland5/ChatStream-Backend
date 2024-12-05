package ChatStream.controllers;

import ChatStream.controllerObjects.ChatResponse;
import ChatStream.controllerObjects.MessageResponse;
import ChatStream.controllerObjects.NewChatBody;
import ChatStream.global.MediaUtils;
import ChatStream.global.RSAUtils;
import ChatStream.model.Chat;
import ChatStream.respository.ChatRepository;
import ChatStream.model.Message;
import ChatStream.respository.MessageRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import ChatStream.model.LatestMessage;

import java.util.*;


@RestController
public class ChatPage{

    @Autowired
    ChatRepository chatRepo;

    @Autowired
    MessageRepository messageRepo;

    @Value("${public.key}")
    private String publicKey;

    @Value("${private.key}")
    private String privateKey;

    @GetMapping("/get-chatlist")
    public ResponseEntity<Map<String, List<Chat>>> getChatList(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        String userId = session.getAttribute("uid").toString();

        List<Chat> result = chatRepo.findByMemberId(userId);

        return new ResponseEntity<>(Collections.singletonMap("list", result), HttpStatus.OK);
    }

    @MessageMapping("/chat/updateChat/{chatId}") // Map incoming messages to /chat/sendChat/{chatId}
    @SendTo("/chat/{chatId}") // Send messages to subscribers of /chat/{chatId}
    public MessageResponse updateMessages(@RequestBody MessageResponse messageObj) throws Exception { //@RequestHeader("csrf") String csrf, HttpSession session
        String id = messageObj.getId();
        String message = messageObj.getMessage();
        String sender = messageObj.getSender();
        String chatId = messageObj.getChatId();
        Date sentAt = new Date();
        String type = messageObj.getType();
        String mediaName = messageObj.getMediaName();

        if(type.equals("delete")) {
            // first delete media
            if(!mediaName.isEmpty()){
                MediaUtils.deleteLocal(mediaName);
            }

            messageRepo.deleteByMessageId(id);
            Chat chat = chatRepo.findByChatId(chatId).getFirst();

            // if latest message deleted
            if(chat.getLatestMessage().getMessageId().equals(id)){
                chatRepo.updateLatestMessage(chatId, chat.getLatestMessage().getUid(), "Message Deleted", id, sentAt);
            }

            return new MessageResponse(id, chatId, "", "", 0, type);
        }

        if(type.equals("edit")){
            String encryptedMessage = "";

            try{
                encryptedMessage = RSAUtils.encrypt(message, this.publicKey);
            } catch (Exception e) {
                return new MessageResponse();
            }

            messageRepo.editByMessageId(id, encryptedMessage);
            Chat chat = chatRepo.findByChatId(chatId).getFirst();

            // if latest message edited
            if(chat.getLatestMessage().getMessageId().equals(id)){
                chatRepo.updateLatestMessage(chatId, chat.getLatestMessage().getUid(), message, id, sentAt);
            }

            return new MessageResponse(id, chatId, "", message, 0, type);
        }

        // when you do create message, you already inserted into db, so just broadcast input here
        return new MessageResponse(id, chatId, sender, message, sentAt.getTime(), type, mediaName, "");
    }


    @PostMapping("/create-new-message")
    public ResponseEntity<Map<String, MessageResponse>> createNewMessage(@RequestBody MessageResponse messageObj) throws Exception { //@RequestHeader("csrf") String csrf, HttpSession session
        String id = messageObj.getId();
        String message = messageObj.getMessage();
        String sender = messageObj.getSender();
        String chatId = messageObj.getChatId();
        Date sentAt = new Date();
        String encryptedMessage = message;
        String media = messageObj.getMedia();
        String mediaName = messageObj.getMediaName();

        if(!message.isEmpty()) {
            try{
                encryptedMessage = RSAUtils.encrypt(message, this.publicKey);
            } catch (Exception e) {
                return new ResponseEntity<>(Collections.singletonMap("message", new MessageResponse()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        if(!media.isBlank()) {
            mediaName = MediaUtils.uploadLocal(media, mediaName);
        }

        String messageId = messageRepo.save(new Message(chatId, sender, encryptedMessage, sentAt, mediaName)).getId();
        chatRepo.updateLatestMessage(chatId, sender, message, messageId, sentAt); // will also need to modify this

        return new ResponseEntity<>(Collections.singletonMap("message", new MessageResponse(messageId, chatId, sender, message, sentAt.getTime(), "create", "", mediaName)),
                HttpStatus.OK);
    }

    @PostMapping("/create-chat")
    public ResponseEntity<Map<String, ChatResponse>> createChat(@RequestBody NewChatBody newChatInfo){
        Date lastUpdated = new Date();

        List<String> chatMemberList = Arrays.asList(newChatInfo.chatMembers);
        String chatName = chatMemberList.size() > 2 ? "New Gc" : "";

        String chatId = chatRepo.save( new Chat(chatMemberList, chatName, "", null, lastUpdated)).getId();

        return new ResponseEntity<>(Collections.singletonMap("chat", new ChatResponse(chatId, chatMemberList, chatName, "", null, lastUpdated.getTime())),
                HttpStatus.OK);
    }

    @MessageMapping("/chatlist/updateChatlist/{userId}") // publish to
    @SendTo("/chatlist/{userId}") // broadcast to
    public ChatResponse newChat(@RequestBody ChatResponse chat){
        return chat;
    }

    @GetMapping("/get-messages")
    public ResponseEntity<Map<String, List<Message>>> getMessages(@RequestParam(value="chatId") String chatId, HttpSession session){
        // if session doesn't exist for current user, return unauthorized response

        List<Message> messages = messageRepo.findByChatId(chatId);

        // all msgs in db are encrypted, so need to decrypt before giving back to user
        for(int index = 0; index < messages.size(); index++){
            Message msg = messages.get(index);
            if(msg.message.isBlank()){
                continue;
            }

            try{
                msg.setMessage(RSAUtils.decrypt(msg.getMessage(), this.privateKey));
            } catch (Exception e) {
                return new ResponseEntity<>(Collections.singletonMap("messages",null), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<>(Collections.singletonMap("messages", messages), HttpStatus.OK);
    }


    // accepts chat id, chat name, and chat picture
    @PutMapping("/update-gc-info")
    public ResponseEntity<Map<String, Boolean>> updateGCInfo(@RequestBody ChatResponse chat){
        String newChatName = chat.getChatName();
        String chatId = chat.getId();
        
        chatRepo.updateChatName(chatId, newChatName);
        return new ResponseEntity<>(Collections.singletonMap("updated", true), HttpStatus.OK);
    }
}
