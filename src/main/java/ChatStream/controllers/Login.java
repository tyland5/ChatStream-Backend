package ChatStream.controllers;

import ChatStream.controllerObjects.CredentialsObj;
import ChatStream.controllerObjects.LoginReturnedJson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ChatStream.model.User;
import ChatStream.respository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.apache.commons.text.RandomStringGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Map;

// The Greeting object must be converted to JSON. Thanks to Spring’s HTTP message converter support, you need not do this conversion manually.
@RestController
public class Login {

    // https://stackoverflow.com/questions/63386079/no-bean-named-mongotemplate-available-spring-boot-mongodb
    // https://stackoverflow.com/questions/42907553/field-required-a-bean-of-type-that-could-not-be-found-error-spring-restful-ap
    @Autowired
    UserRepository userRepo;

    @GetMapping("/check-logged-in")
    public ResponseEntity<Map<String, Boolean>> checkLoggedIn(HttpServletRequest request){
        boolean sessionExists = !(request.getSession(false) == null); // this is accurate. checks if mongodb has your session
        return new ResponseEntity<>(Collections.singletonMap("loggedIn", sessionExists), HttpStatus.OK);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Map<String, Boolean>> logOut(HttpServletRequest request){
        HttpSession session = request.getSession(false);

        if(session != null){
            session.invalidate();
        }

        return new ResponseEntity<>(Collections.singletonMap("loggedOut", true), HttpStatus.OK);
    }

    @PostMapping(value = "/check-credentials")
    public ResponseEntity<LoginReturnedJson> checkCredentials(@RequestBody CredentialsObj credentials, HttpSession session) {
        String username = credentials.getUsername();
        String password = credentials.getPassword();

        List<User> result = userRepo.findByUsername(username);

        // username does not match an existing account
        if(result.isEmpty()){
            session.invalidate();
            return new ResponseEntity<>(new LoginReturnedJson(), HttpStatus.UNAUTHORIZED);
        }

        // check if password is correct. Do not encrypt user inputted password
        if(BCrypt.checkpw(password, result.getFirst().getPassword())){
            session.setAttribute("uid", result.getFirst().getId());

            RandomStringGenerator generator = new RandomStringGenerator.Builder()
                    .withinRange('a', 'z').build();
            String randomLetters = generator.generate(16);

            session.setAttribute("csrf", randomLetters);

            session.setMaxInactiveInterval(86400); // if user inactive for more than day, db deletes the session

            User user = result.getFirst();

            LoginReturnedJson json = new LoginReturnedJson(randomLetters, result.getFirst().getId(), user.getPfp(), user.getUsername(), user.getName());
            return new ResponseEntity<>(json, HttpStatus.OK);
        }
        else {
            session.invalidate();
            return new ResponseEntity<>(new LoginReturnedJson(), HttpStatus.UNAUTHORIZED);
        }
    }
}