package ChatStream.controllers;

import ChatStream.controllerObjects.UidObj;
import ChatStream.model.Friend;
import ChatStream.model.User;
import ChatStream.respository.FriendRepository;
import ChatStream.respository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class Friends {

    @Autowired
    FriendRepository friendRepo;

    @Autowired
    UserRepository userRepo;

    public String[] extractFriendId(List<Friend> friends, String uid){
        return friends.stream().map(friend -> {
            if(friend.getUser1().equals(uid)){
                return friend.getUser2();
            }
            return friend.getUser1();
        }).toList().toArray(new String[friends.size()]);
    }

    @PostMapping(value = "/send-friend-request")
    public ResponseEntity<Map<String, Boolean>> sendFriendRequest(@RequestBody UidObj otherUser, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String uid = session.getAttribute("uid").toString();
        String otherUid = otherUser.getUid();

        friendRepo.save(new Friend(uid, otherUid, false));
        return new ResponseEntity<>(Collections.singletonMap("inserted", true), HttpStatus.OK);
    }

    @PutMapping(value="/accept-friend-request")
    public ResponseEntity<Map<String, Boolean>> acceptFriendRequest(@RequestBody UidObj otherUser, HttpServletRequest request){
        HttpSession session = request.getSession(false);
        String uid = session.getAttribute("uid").toString();
        String otherUid = otherUser.getUid();

        friendRepo.acceptFriendRequest(uid, otherUid);
        return new ResponseEntity<>(Collections.singletonMap("accepted", true), HttpStatus.OK);
    }

    @GetMapping("/get-friends")
    public ResponseEntity<Map<String, List<User>>> getFriends(HttpSession session){
        String uid = session.getAttribute("uid").toString();
        List<Friend> friends = friendRepo.findByUid(uid);

        String[] friendIds = extractFriendId(friends, uid);
        List <User> friendList = userRepo.findByIds(friendIds);

        return new ResponseEntity<>(Collections.singletonMap("friends", friendList), HttpStatus.OK);
    }

    @GetMapping(value = "/get-incoming-friend-requests", produces = "application/json")
    public ResponseEntity<Map<String, List<User>>> getIncomingFriendRequests(HttpSession session) {
        String uid = session.getAttribute("uid").toString();
        List<Friend> users = friendRepo.findIncomingRequestsById(uid);

        String[] userIds = extractFriendId(users, uid);
        List <User> incomingList = userRepo.findByIds(userIds);

        return new ResponseEntity<>(Collections.singletonMap("incoming", incomingList), HttpStatus.OK);
    }

    @GetMapping(value = "/get-outgoing-friend-requests", produces = "application/json")
    public ResponseEntity<Map<String, List<User>>> getOutgoingFriendRequests(HttpSession session) {
        String uid = session.getAttribute("uid").toString();
        List<Friend> users = friendRepo.findOutgoingRequestsById(uid);

        String[] userIds = extractFriendId(users, uid);
        List <User> outgoingList = userRepo.findByIds(userIds);

        return new ResponseEntity<>(Collections.singletonMap("outgoing", outgoingList), HttpStatus.OK);
    }

    @PostMapping(value = "/remove-outgoing-friend-request")
    public ResponseEntity<Map<String, Boolean>> removeOutgoingFriendRequest(@RequestBody UidObj otherUser, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String uid = session.getAttribute("uid").toString();
        String otherUid = otherUser.getUid();

        friendRepo.deleteOutgoingRequest(uid, otherUid);
        return new ResponseEntity<>(Collections.singletonMap("removed", true), HttpStatus.OK);
    }

    @PostMapping(value = "/remove-incoming-friend-request")
    public ResponseEntity<Map<String, Boolean>> removeIncomingFriendRequest(@RequestBody UidObj otherUser, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        String uid = session.getAttribute("uid").toString();
        String otherUid = otherUser.getUid();

        friendRepo.deleteIncomingRequest(otherUid, uid);
        return new ResponseEntity<>(Collections.singletonMap("removed", true), HttpStatus.OK);
    }
}
