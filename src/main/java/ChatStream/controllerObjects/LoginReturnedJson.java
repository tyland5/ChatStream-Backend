package ChatStream.controllerObjects;

public class LoginReturnedJson{
    String csrf;
    String uid;
    String pfp;
    String username;
    String name;

    public LoginReturnedJson(){}

    public LoginReturnedJson(String csrf, String uid, String pfp, String username, String name){
        this.csrf = csrf;
        this.uid = uid;
        this.pfp = pfp;
        this.username = username;
        this.name = name;
    }

    // I NEED THIS OR ELSE I GET
    //Resolved [org.springframework.http.converter.HttpMessageNotWritableException: No converter for [class ChatStream.controllers.CsrfJson] with preset Content-Type 'null']
    // https://stackoverflow.com/questions/63832966/httpmessagenotwritableexception-no-converter-for-with-preset-content-type
    public String getCsrf() {
        return this.csrf;
    }

    public String getUid() {
        return this.uid;
    }

    public String getUsername(){
        return this.username;
    }

    public String getPfp(){
        return this.pfp;
    }

    public String getName(){
        return this.name;
    }
}