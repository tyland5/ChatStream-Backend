package ChatStream.controllers;
import ChatStream.controllerObjects.VerificationEmailObj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.Map;

@Controller
public class EmailService {


    @Autowired private JavaMailSender mailSender;

    @Value("${spring.mail.username}") private String sender;

    public EmailService(){}

    @PostMapping("/send-verif-email-register")
    public ResponseEntity<Map<String, Boolean>> registerVerifyEmail(@RequestBody() VerificationEmailObj verificationObj){
        sendEmail(verificationObj.getRecipientEmail(), "Verification code for register",
                "This is your verification code for registration: " + verificationObj.getVerificationCode());
        return new ResponseEntity<>(Collections.singletonMap("sent", true), HttpStatus.OK);
    }

    @PostMapping("/send-verif-email-forgot-pw")
    public ResponseEntity<Map<String, Boolean>> forgotPwVerifyEmail(@RequestBody() VerificationEmailObj verificationObj){
        sendEmail(verificationObj.getRecipientEmail(), "Verification code for password change",
                "Please use this verification code to complete your password change: " + verificationObj.getVerificationCode());
        return new ResponseEntity<>(Collections.singletonMap("sent", true), HttpStatus.OK);
    }

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(this.sender);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        this.mailSender.send(message);
    }
}
