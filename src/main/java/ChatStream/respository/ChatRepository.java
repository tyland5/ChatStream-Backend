package ChatStream.respository;

import ChatStream.model.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ChatStream.model.User;
import org.springframework.data.mongodb.repository.Update;

import java.util.Date;
import java.util.List;

public interface ChatRepository extends MongoRepository<Chat, String>{

    // get most recently updated chats at top of list
    // hidden:{$nin:[?0]}, thought about including it. but what happens if hidden and user gets message? lots of db calls
    @Query(value = "{members:'?0'}", sort="{lastUpdated:-1}")
    List<Chat> findByMemberId(String id);

    @Query(value="{_id:'?0'}")
    List<Chat> findByChatId(String chatId);

    @Query("{'_id' : ?0}")
    @Update("{'$set': {'latestMessage.uid': '?1', 'latestMessage.message': '?2', 'latestMessage.messageId': '?3', lastUpdated: ?4}, '$unset':{hidden:''}}")
    void updateLatestMessage(String id, String uid, String message, String messageId, Date lastUpdated);

    @Query("{'_id' : ?0}")
    @Update("{'$set': {'chatName':'?1'}}")
    void updateChatName(String id, String chatName);

    @Query("{'_id' : ?0}")
    @Update("{'$unset': {hidden:''}}")
    void unsetHidden(String chatId);

    @Query("{'_id' : ?0}")
    @Update("{'$set': {hidden:?1}}")
    void setHidden(String chatId, String[] uids);

    @Query("{'_id' : ?0}")
    @Update("{'$push': {hidden:?1}}")
    void pushHidden(String chatId, String uid);

    @Query("{'_id' : ?0}")
    @Update("{'$pull': {hidden: {$in: [?1] }}}")
    void pullHidden(String chatId, String uid);

    @Query("{'_id' : ?0}")
    @Update("{'$pull': {members: {$in: [?1] }}}")
    void leaveGc(String chatId, String uid);

    @Query("{'_id' : ?0}")
    @Update("{'$push': {members:{ $each: ?1 }} }")
    void addToGc(String chatId, String[] uids);
}
