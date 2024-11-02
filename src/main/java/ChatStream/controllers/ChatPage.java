package ChatStream.controllers;

import ChatStream.global.MediaUtils;
import ChatStream.global.RSAUtils;
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
import ChatStream.model.LatestMessage;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

class MessageResponse{
    public String id;
    public String chatId;
    public String sender;
    public String message;
    public long sentAt;
    public String type; // edit, delete, create
    public String media;
    public String mediaName;

    public MessageResponse(){}

    public MessageResponse(String id, String chatId, String sender, String message, long sentAt, String type){
        this.id = id;
        this.chatId = chatId;
        this.sender = sender;
        this.message = message;
        this.sentAt = sentAt;
        this.type = type;
    }

    public MessageResponse(String id, String chatId, String sender, String message, long sentAt, String type, String media, String mediaName){
        this.id = id;
        this.chatId = chatId;
        this.sender = sender;
        this.message = message;
        this.sentAt = sentAt;
        this.type = type;
        this.media = media;
        this.mediaName = mediaName;
    }

    public String getId(){
        return this.id;
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

    public String getType() {
        return this.type;
    }

    public String getMedia(){
        return this.media;
    }

    public String getMediaName(){
        return this.mediaName;
    }
}

class ChatResponse{
    public String id;

    private List<String> members;

    private String chatName;
    private String chatPic;
    private LatestMessage latestMessage;
    private long lastUpdated;

    public ChatResponse(String id, List<String> members, String chatName, String chatPic, LatestMessage latestMessage, long lastUpdated){
        this.id = id;
        this.members = members;
        this.chatName = chatName;
        this.chatPic = chatPic;
        this.latestMessage = latestMessage;
        this.lastUpdated = lastUpdated;
    }


    public String getId(){
        return this.id;
    }

    public List<String> getMembers(){
        return this.members;
    }

    public String getChatName() {
        return this.chatName;
    }

    public String getChatPic() {
        return this.chatPic;
    }

    public LatestMessage getLatestMessage() {
        return this.latestMessage;
    }

    public long getLastUpdated(){
        return this.lastUpdated;
    }
}

class NewChatBody{
    public String[] chatMembers;

    public NewChatBody(){

    }

    public NewChatBody(String[] chatMembers){
        this.chatMembers = chatMembers;
    }

    public String[] getChatMembers(){
        return this.chatMembers;
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



    @MessageMapping("/chat/updateChat/{chatId}") // Map incoming messages to /chat/sendChat/{chatId}
    @SendTo("/chat/{chatId}") // Send messages to subscribers of /chat/{chatId}
    MessageResponse updateMessages(@RequestBody MessageResponse messageObj) throws Exception { //@RequestHeader("csrf") String csrf, HttpSession session
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
    ResponseEntity<MessageResponse> createNewMessage(@RequestBody MessageResponse messageObj) throws Exception { //@RequestHeader("csrf") String csrf, HttpSession session
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
                return new ResponseEntity<MessageResponse>(new MessageResponse(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        if(!media.isBlank()) {
            mediaName = MediaUtils.uploadLocal(media, mediaName);
        }

        String messageId = messageRepo.save(new Message(chatId, sender, encryptedMessage, sentAt, mediaName)).getId();
        chatRepo.updateLatestMessage(chatId, sender, message, messageId, sentAt); // will also need to modify this

        return new ResponseEntity<MessageResponse>(new MessageResponse(messageId, chatId, sender, message, sentAt.getTime(), "create", "", mediaName),
                HttpStatus.OK);
    }

    @PostMapping("/create-chat")
    ResponseEntity<Object> createChat(@RequestBody NewChatBody newChatInfo, HttpSession session){
        Date lastUpdated = new Date();
        System.out.println("CREATE CHAT HIT");
        System.out.println(newChatInfo.getChatMembers().toString());
        List<String> chatMemberList = Arrays.asList(newChatInfo.chatMembers);
        String chatName = chatMemberList.size() > 2 ? "New Gc" : "";

        String chatId = chatRepo.save( new Chat(chatMemberList, chatName, "", null, lastUpdated)).getId();

        return new ResponseEntity<Object>(new ChatResponse(chatId, chatMemberList, chatName, "", null, lastUpdated.getTime()), HttpStatus.OK);
    }

    @MessageMapping("/chatlist/updateChatlist/{userId}") // publish to
    @SendTo("/chatlist/{userId}") // broadcast to
    ChatResponse newChat(@RequestBody ChatResponse chat){
        return chat;
    }

    @GetMapping("/get-messages")
    ResponseEntity<Object> getMessages(@RequestParam(value="chatId") String chatId, HttpSession session){
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
                return new ResponseEntity<Object>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return new ResponseEntity<Object>(messages, HttpStatus.OK);
    }
}
