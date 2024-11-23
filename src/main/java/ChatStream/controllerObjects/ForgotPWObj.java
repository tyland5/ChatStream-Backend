package ChatStream.controllerObjects;

public class ForgotPWObj {
    private String email;
    private String password;

    public ForgotPWObj(String email, String password){
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }
}
