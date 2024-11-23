package ChatStream.respository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import ChatStream.model.User;
import org.springframework.data.mongodb.repository.Update;

import java.util.Date;
import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    @Query("{username:'?0'}")
    List<User> findByUsername(String username);

    @Query("{email:'?0'}")
    List<User> findByEmail(String email);

    @Query(value = "{_id: {$in: ?0}}", fields = "{username: 1, name:1, pfp: 1}")
    List<User> findByIds(String[] ids);

    @Query("{'_id' : ?0}")
    @Update("{'$set': {'username': '?1', 'name': '?2'}}")
    void updateProfile(String uid, String username, String name);

    @Query("{'email' : ?0}")
    @Update("{'$set': {'password': '?1'}}")
    void updatePasswordByEmail(String email, String password);

    @Query("{'_id' : ?0}")
    @Update("{'$set': {'username': '?1', 'name': '?2', 'pfp': '?3'}}")
    void updateProfileWithPfp(String uid, String username, String name, String pfp);
}
