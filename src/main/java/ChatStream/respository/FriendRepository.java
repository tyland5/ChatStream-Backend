package ChatStream.respository;

import ChatStream.model.Friend;
import ChatStream.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;

public interface FriendRepository extends MongoRepository<Friend, String> {

    @Query(value="{$and: [{$or: [{user1: '?0'}, {user2: '?0'}]}, {accepted: true}]}")
    List<Friend> findByUid(String uid);

    @Query(value="{$and: [{user2:'?0'}, {accepted: false}]}")
    List<Friend> findIncomingRequestsById(String uid);

    @Query(value="{$and: [{user1:'?0'}, {accepted: false}]}")
    List<Friend> findOutgoingRequestsById(String uid);

    @Query(value = "{$and: [{user1: '?0'}, {user2: '?1'}]}", delete = true)
    void deleteOutgoingRequest(String uid, String otherUid);

    @Query(value = "{$and: [{user1: '?0'}, {user2: '?1'}]}", delete = true)
    void deleteIncomingRequest(String otherUid, String uid);

    @Query(value="{$and: [ {$or: [{user1: '?0'}, {user2: '?0'}]}, {$or: [{user1: '?1'}, {user2: '?1'}]} ] }")
    @Update("{$set: {accepted: true}}")
    void acceptFriendRequest(String uid, String otherUid);
}
