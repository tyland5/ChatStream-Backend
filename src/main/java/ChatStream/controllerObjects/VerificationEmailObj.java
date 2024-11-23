package ChatStream.controllerObjects;

public class VerificationEmailObj {
    private String recipientEmail;
    private String verificationCode;

    public VerificationEmailObj(String recipientEmail, String verificationCode){
        this.recipientEmail = recipientEmail;
        this.verificationCode = verificationCode;
    }

    public String getRecipientEmail() {
        return this.recipientEmail;
    }

    public String getVerificationCode(){
        return this.verificationCode;
    }
}
