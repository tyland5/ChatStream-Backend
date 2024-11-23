package ChatStream.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

// be careful, mistyped as user which didnt connect to collection
@Document("users")
public class User {
    @Id
    public String id;

    private String username;
    private String name;
    private String pfp;
    private String password;
    private String email;

    public User(){}

    public User(String username, String name, String pfp, String password, String email) {
        this.username = username;
        this.name = name;
        this.pfp = pfp;
        this.password = password;
        this.email = email;
    }

    public User(String username, String name, String pfp) {
        this.username = username;
        this.name = name;
        this.pfp = pfp;
    }

    public String getId(){
        return this.id;
    }

    public String getPassword(){
        return this.password;
    }

    public String getUsername(){
        return this.username;
    }

    public String getName(){
        return this.name;
    }

    public String getPfp(){
        return this.pfp;
    }

    public String getEmail(){
        return this.email;
    }
}
