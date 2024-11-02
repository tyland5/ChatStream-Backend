package ChatStream.respository;

import ChatStream.model.Friend;
import ChatStream.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface FriendRepository extends MongoRepository<Friend, String> {

    @Query(value="{$or: [{user1: '?0'}, {user2: '?0'}]}")
    List<Friend> findByUid(String uid);
}
