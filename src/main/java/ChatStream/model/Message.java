package ChatStream.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("messages")
public class Message {
    @Id
    public String id;

    public String chatId;
    public String sender;
    public String message;
    public String sentAt;

    public Message(){}

    public Message(String chatId, String sender, String message, String sentAt){
        this.chatId = chatId;
        this.sender = sender;
        this.message = message;
        this.sentAt = sentAt;
    }

    public String getMessage() {
        return this.message;
    }

    public String getChatId() {
        return this.chatId;
    }

    public String getId(){
        return this.id;
    }

    public String getSender() {
        return this.sender;
    }

    public String getSentAt() {
        return this.sentAt;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
