package ChatStream.respository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ChatStream.model.User;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    @Query("{username:'?0'}")
    List<User> findByUsername(String username);

    @Query(value = "{_id: {$in: ?0}}", fields = "{username: 1, name:1, pfp: 1}")
    List<User> findByIds(String[] ids);
}
