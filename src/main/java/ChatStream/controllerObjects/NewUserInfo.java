package ChatStream.controllerObjects;

public class NewUserInfo {
    private String username;
    private String name;
    private String oldPfp;
    private String newPfp; // base 64 string rep of image
    private String newPfpName;

    public NewUserInfo(){}

    // update profile without pfp
    public NewUserInfo(String username, String name) {
        this.username = username;
        this.name = name;
    }

    // update profile without pfp
    public NewUserInfo(String username, String name, String oldPfp, String newPfp, String newPfpName){
        this.username = username;
        this.name = name;
        this.oldPfp = oldPfp;
        this.newPfp = newPfp;
        this.newPfpName = newPfpName;
    }

    public String getUsername(){
        return this.username;
    }

    public String getName(){
        return this.name;
    }

    public String getOldPfp(){
        return this.oldPfp;
    }

    public String getNewPfp(){
        return this.newPfp;
    }

    public String getNewPfpName(){
        return this.newPfpName;
    }
}
