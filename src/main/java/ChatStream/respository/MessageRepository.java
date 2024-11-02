package ChatStream.respository;

import ChatStream.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    @Query("{chatId:'?0'}")
    List<Message> findByChatId(String chatId);

    @Query(value = "{_id: '?0'}", delete = true)
    void deleteByMessageId(String messageId);

    @Query("{'_id' : '?0'}")
    @Update("{'$set': {'message': '?1'}}")
    void editByMessageId(String messageId, String newMessage);
}
