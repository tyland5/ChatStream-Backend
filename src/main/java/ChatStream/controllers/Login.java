package ChatStream.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ChatStream.model.User;
import ChatStream.respository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.apache.commons.text.RandomStringGenerator;
import java.util.List;

class LoginReturnedJson{
    String csrf;
    String uid;

    public LoginReturnedJson(String csrf, String uid){
        this.csrf = csrf;
        this.uid = uid;
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
}

// The Greeting object must be converted to JSON. Thanks to Spring’s HTTP message converter support, you need not do this conversion manually.
@RestController
public class Login {

    // https://stackoverflow.com/questions/63386079/no-bean-named-mongotemplate-available-spring-boot-mongodb
    // https://stackoverflow.com/questions/42907553/field-required-a-bean-of-type-that-could-not-be-found-error-spring-restful-ap
    @Autowired
    UserRepository userRepo;


    public String hashPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    @GetMapping(value = "/get-user-info", produces = "application/json")
    public ResponseEntity<Object> getBasicUserInfo(@RequestParam(value = "uids") String[] uids, HttpSession session) {
        List<User> result = userRepo.findByIds(uids);
        return new ResponseEntity<Object>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/check-credentials", produces = "application/json")
    public ResponseEntity<Object> checkCredentials(@RequestParam(value = "username") String username, @RequestParam(value = "password") String password, HttpSession session) {
        List<User> result = userRepo.findByUsername(username);
        HttpHeaders headers = new HttpHeaders();

        // username does not match an existing account
        if(result.isEmpty()){
            session.invalidate();
            return new ResponseEntity<Object>(null, headers, HttpStatus.UNAUTHORIZED);
        }

        // check if password is correct. Do not encrypt user inputted password
        if(BCrypt.checkpw(password, result.getFirst().getPassword())){
            session.setAttribute("uid", result.getFirst().getId());

            RandomStringGenerator generator = new RandomStringGenerator.Builder()
                    .withinRange('a', 'z').build();
            String randomLetters = generator.generate(16);

            session.setAttribute("csrf", randomLetters);

            LoginReturnedJson json = new LoginReturnedJson(randomLetters, result.getFirst().getId());
            return new ResponseEntity<Object>(json, headers, HttpStatus.OK);
        }
        else {
            session.invalidate();
            return new ResponseEntity<Object>(null, headers, HttpStatus.UNAUTHORIZED);
        }
    }
}