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
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        System.out.println("GEtting chatlist");
        String userId = session.getAttribute("uid").toString();
        System.out.println(userId);

        List<Chat> result = chatRepo.findByMemberId(userId);
        System.out.println(result.size());

        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @PostMapping("/send-message")
    ResponseEntity<Object> newMessage(@RequestBody Message messageObj, @RequestHeader("csrf") String csrf, HttpSession session) {
        String message = messageObj.getMessage();
        String sender = messageObj.getSender();
        String chatId = messageObj.getChatId();
        String sentAt = messageObj.getSentAt();
        String encryptedMessage = "";

        if(!csrf.equals(session.getAttribute("csrf"))){
            return new ResponseEntity<Object>(null, HttpStatus.UNAUTHORIZED);
        }

        try{
            encryptedMessage = RSAUtils.encrypt(message, this.publicKey);
        } catch (Exception e) {
            return new ResponseEntity<Object>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        messageRepo.save(new Message(chatId, sender, encryptedMessage, sentAt));

        return new ResponseEntity<Object>(null, HttpStatus.OK);
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
