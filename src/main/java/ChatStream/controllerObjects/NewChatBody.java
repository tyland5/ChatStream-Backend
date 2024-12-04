package ChatStream.controllerObjects;

public class NewChatBody{
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
