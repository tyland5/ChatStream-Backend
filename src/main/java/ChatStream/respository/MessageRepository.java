package ChatStream.respository;

import ChatStream.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    @Query("{chatId:'?0'}")
    List<Message> findByChatId(String chatId);
}
