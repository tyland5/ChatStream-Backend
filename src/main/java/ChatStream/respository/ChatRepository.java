package ChatStream.respository;

import ChatStream.model.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ChatStream.model.User;

import java.util.List;

public interface ChatRepository extends MongoRepository<Chat, String>{

    @Query("{members:'?0'}")
    List<Chat> findByMemberId(String id);
}
