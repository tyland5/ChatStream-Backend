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
    @Query(value = "{members:'?0'}", sort="{lastUpdated:-1}")
    List<Chat> findByMemberId(String id);

    @Query(value="{_id:'?0'}")
    List<Chat> findByChatId(String chatId);

    @Query("{'_id' : ?0}")
    @Update("{'$set': {'latestMessage.uid': '?1', 'latestMessage.message': '?2', 'latestMessage.messageId': '?3', lastUpdated: ?4}}")
    Integer updateLatestMessage(String id, String uid, String message, String messageId, Date lastUpdated);

    @Query("{'_id' : ?0}")
    @Update("{'$set': {'chatName':'?1'}}")
    void updateChatName(String id, String chatName);
}
