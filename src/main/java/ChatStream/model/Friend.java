package ChatStream.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("friends")
public class Friend {

    @Id
    public String id;

    private String user1;
    private String user2;
    private Boolean accepted;

    public Friend(){}

    public Friend(String user1, String user2, Boolean accepted){
        this.user1 = user1;
        this.user2 = user2;
        this.accepted = accepted;
    }

    public String getId() {
        return this.id;
    }

    public String getUser1() {
        return this.user1;
    }

    public String getUser2() {
        return this.user2;
    }

    public Boolean getAccepted() {
        return this.accepted;
    }
}
