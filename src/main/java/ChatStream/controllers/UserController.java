package ChatStream.controllers;

import ChatStream.model.Friend;
import ChatStream.model.User;
import ChatStream.respository.FriendRepository;
import ChatStream.respository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    FriendRepository friendRepo;

    @Autowired
    UserRepository userRepo;


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
}
