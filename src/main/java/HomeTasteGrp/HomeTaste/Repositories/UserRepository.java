package HomeTasteGrp.HomeTaste.Repositories;

import HomeTasteGrp.HomeTaste.Models.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);
    List<UserEntity> findByApprovedFalseAndIsDeletedFalse();

}
