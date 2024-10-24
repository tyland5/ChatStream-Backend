package ChatStream.model;

public class LatestMessage{
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