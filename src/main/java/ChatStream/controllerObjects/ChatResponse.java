package ChatStream.controllerObjects;

import ChatStream.model.LatestMessage;
import ChatStream.model.User;

import java.util.List;

public class ChatResponse{
    public String id;

    private List<String> members;

    private String chatName;
    private String chatPic;
    private LatestMessage latestMessage;
    private long lastUpdated;
    private List<User> memberObjects;

    public ChatResponse(){}

    public ChatResponse(String id, List<String> members, String chatName, String chatPic, LatestMessage latestMessage, long lastUpdated){
        this.id = id;
        this.members = members;
        this.chatName = chatName;
        this.chatPic = chatPic;
        this.latestMessage = latestMessage;
        this.lastUpdated = lastUpdated;
    }

    public ChatResponse(String id, List<String> members, String chatName, String chatPic, LatestMessage latestMessage, long lastUpdated, List<User> memberObjects){
        this.id = id;
        this.members = members;
        this.chatName = chatName;
        this.chatPic = chatPic;
        this.latestMessage = latestMessage;
        this.lastUpdated = lastUpdated;
        this.memberObjects = memberObjects;
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

    public List<User> getMemberObjects() {
        return this.memberObjects;
    }

    public void setMemberObjects(List<User> memberObjects) {
        this.memberObjects = memberObjects;
    }
}