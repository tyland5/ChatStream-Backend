package ChatStream.model;

public class LatestMessage{
    private String uid;
    private String message;
    private String messageId;

    public LatestMessage(String uid, String message, String messageId){
        this.uid = uid;
        this.message = message;
        this.messageId = messageId;
    }

    public String getMessage() {
        return this.message;
    }

    public String getUid() {
        return this.uid;
    }

    public String getMessageId() {
        return this.messageId;
    }
}