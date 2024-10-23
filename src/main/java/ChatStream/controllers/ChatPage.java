package ChatStream.controllers;

import ChatStream.RSAUtils;
import ChatStream.model.Chat;
import ChatStream.respository.ChatRepository;
import ChatStream.model.Message;
import ChatStream.respository.MessageRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

class MessageResponse{
    public String id;
    public String chatId;
    public String sender;
    public String message;
    public long sentAt;

    public MessageResponse(){}

    public MessageResponse(String id, String chatId, String sender, String message, long sentAt){
        this.id = id;
        this.chatId = chatId;
        this.sender = sender;
        this.message = message;
        this.sentAt = sentAt;
    }

    public String getChatId() {
        return this.chatId;
    }

    public String getMessage(){
        return this.message;
    }

    public String getSender(){
        return this.sender;
    }
}

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
    ResponseEntity<Object> getChatList(HttpSession session){
        String userId = session.getAttribute("uid").toString();

        List<Chat> result = chatRepo.findByMemberId(userId);

        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }


    @MessageMapping("/chat/sendChat/{chatId}") // Map incoming messages to /chat/sendChat/{chatId}
    @SendTo("/chat/{chatId}") // Send messages to subscribers of /chat/{chatId}
    MessageResponse newMessage(@RequestBody MessageResponse messageObj) { //@RequestHeader("csrf") String csrf, HttpSession session
        String message = messageObj.getMessage();
        String sender = messageObj.getSender();
        String chatId = messageObj.getChatId();
        Date sentAt = new Date();
        String encryptedMessage = "";

        /*
        if(!csrf.equals(session.getAttribute("csrf"))){
            return new Message();
        }
        */

        try{
            encryptedMessage = RSAUtils.encrypt(message, this.publicKey);
        } catch (Exception e) {
            return new MessageResponse();
        }

        String messageId = messageRepo.save(new Message(chatId, sender, encryptedMessage, sentAt)).getId();
        chatRepo.updateLatestMessage(chatId, sender, message, sentAt);

        return new MessageResponse(messageId, chatId, sender, message, sentAt.getTime());
    }

    @GetMapping("/get-messages")
    ResponseEntity<Object> getMessages(@RequestParam(value="chatId") String chatId, HttpSession session){
        // if session doesn't exist for current user, return unauthorized response

        List<Message> messages = messageRepo.findByChatId(chatId);

        // all msgs in db are encrypted, so need to decrypt before giving back to user
        for(int index = 0; index < messages.size(); index++){
            try{
                Message msg = messages.get(index);
                msg.setMessage(RSAUtils.decrypt(msg.getMessage(), this.privateKey));
            } catch (Exception e) {
                return new ResponseEntity<Object>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Object>(messages, HttpStatus.OK);
    }
}
