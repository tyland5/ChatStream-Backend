package ChatStream.controllers;

import ChatStream.controllerObjects.ForgotPWObj;
import ChatStream.controllerObjects.NewUserInfo;
import ChatStream.global.MediaUtils;
import ChatStream.model.Friend;
import ChatStream.model.User;
import ChatStream.respository.FriendRepository;
import ChatStream.respository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    FriendRepository friendRepo;

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

    @GetMapping("/get-friends")
    ResponseEntity<Object> getFriends(@RequestParam(value="uid") String uid, HttpSession session){
        List<Friend> friends = friendRepo.findByUid(uid);
        String[] friendIds = friends.stream().map(friend -> {
            if(friend.getUser1().equals(session.getAttribute("uid").toString())){
                return friend.getUser2();
            }
            return friend.getUser1();
        }).toList().toArray(new String[friends.size()]);

        List <User> friendList = userRepo.findByIds(friendIds);

        return new ResponseEntity<Object>(friendList,HttpStatus.OK);
    }

    @GetMapping("/check-username-available")
    ResponseEntity<Map<String, Boolean>> checkUsernameAvailable(@RequestParam(value="username") String username, HttpServletRequest request){
        List<User> user = userRepo.findByUsername(username);
        if(user.isEmpty()){
            return new ResponseEntity<>(Collections.singletonMap("available", true), HttpStatus.OK);
        }

        HttpSession session = request.getSession(false);

        // if the in use username doesn't belong to the current user, then not available
        if(session == null || !user.getFirst().getId().equals(session.getAttribute("uid").toString())){
            return new ResponseEntity<>(Collections.singletonMap("available", false), HttpStatus.OK);
        }

        return new ResponseEntity<>(Collections.singletonMap("available", true), HttpStatus.OK);
    }

    @GetMapping("/check-email-used")
    ResponseEntity<Map<String, Boolean>> checkIfEmailInUse(@RequestParam(value="email") String email){
        List<User> users = userRepo.findByEmail(email);
        boolean isAvailable = users.isEmpty();
        return new ResponseEntity<>(Collections.singletonMap("available", isAvailable), HttpStatus.OK);
    }

    @PostMapping("/create-user")
    ResponseEntity<Map<String, Boolean>> createUser(@RequestBody User user){
        String username = user.getUsername();
        String name = user.getName();
        String password = user.getPassword();
        String email = user.getEmail();

        userRepo.save(new User(username, name, "", hashPassword(password), email));
        return new ResponseEntity<>(Collections.singletonMap("inserted", true), HttpStatus.OK);
    }

    @PutMapping("/update-profile")
    ResponseEntity<Map<String, String>> updateProfileInfo(@RequestBody NewUserInfo updatedInfo, HttpSession session) throws Exception{
        String new_username= updatedInfo.getUsername();
        String new_name = updatedInfo.getName();
        String oldPfp = updatedInfo.getOldPfp();
        String newPfp = updatedInfo.getNewPfp();
        String newPfpName = updatedInfo.getNewPfpName();

        if(newPfpName != null){
            // change pfp by deleting then inserting into bucket or webserver
            String newPfpUrl = MediaUtils.replaceLocal(oldPfp, newPfp, newPfpName);
            userRepo.updateProfileWithPfp(session.getAttribute("uid").toString(), new_username, new_name, newPfpUrl);
            return new ResponseEntity<>(Collections.singletonMap("newPfpUrl", newPfpUrl), HttpStatus.OK);
        }

        userRepo.updateProfile(session.getAttribute("uid").toString(), new_username, new_name);

        return new ResponseEntity<>(Collections.singletonMap("inserted", "true"), HttpStatus.OK);

    }

    @PutMapping("/change-password")
    ResponseEntity<Map<String, String>> updateProfileInfo(@RequestBody ForgotPWObj obj) {
        String email = obj.getEmail();
        String password = obj.getPassword();

        userRepo.updatePasswordByEmail(email, hashPassword(password));
        return new ResponseEntity<>(Collections.singletonMap("updated", "true"), HttpStatus.OK);

    }

}
