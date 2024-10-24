package ChatStream.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;


@Document("chats")
public class Chat {
    @Id
    public String id;

    private List<String> members;

    private String chatName;
    private String chatPic;
    private LatestMessage latestMessage;
    private Date lastUpdated;

    public Chat(){}

    public Chat(String id, List<String> members, String chatName, String chatPic, LatestMessage latestMessage, Date lastUpdated){
        this.id = id;
        this.members = members;
        this.chatName = chatName;
        this.chatPic = chatPic;
        this.latestMessage = latestMessage;
        this.lastUpdated = lastUpdated;
    }

    public Chat(List<String> members, String chatName, String chatPic, LatestMessage latestMessage, Date lastUpdated){
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

    public Date getLastUpdated(){
        return this.lastUpdated;
    }
}
