package ChatStream.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/*
class LatestMessage{
    private String uid;
    private String message;

    public LatestMessage(String uid, String message){
        this.uid = uid;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public String getUid() {
        return this.uid;
    }
}
*/


@Document("chats")
public class Chat {
    @Id
    public String id;

    private List<String> members;

    private String chatName;
    private String chatPic;
    private Object latestMessage;

    public Chat(){}

    /*
    // 2 person chat
    public Chat(List<String> members, Object latestMessage) {
        super();
        this.members = members;
        this.latestMessage = latestMessage;
    }

    // group chat
    @PersistenceCreator
    public Chat(List<String> members, String chatName, String chatPic, Object latestMessage){
        super();
        this.members = members;
        this.chatName = chatName;
        this.chatPic = chatPic;
        this.latestMessage = latestMessage;
    }
    */


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

    public Object getLatestMessage() {
        return this.latestMessage;
    }
}
