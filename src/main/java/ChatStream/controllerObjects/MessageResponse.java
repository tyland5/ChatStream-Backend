package ChatStream.controllerObjects;

public class MessageResponse{
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
