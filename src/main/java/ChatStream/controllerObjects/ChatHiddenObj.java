package ChatStream.controllerObjects;

public class ChatHiddenObj {
    private String chatId;
    private String[] uids; // uids hiding the chat

    public ChatHiddenObj(){}

    public ChatHiddenObj(String chatId, String[] uids ){
        this.chatId = chatId;
        this.uids = uids;
    }

    public String getChatId() {
        return this.chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String[] getUids() {
        return this.uids;
    }

    public void setUids(String[] uids) {
        this.uids = uids;
    }
}
