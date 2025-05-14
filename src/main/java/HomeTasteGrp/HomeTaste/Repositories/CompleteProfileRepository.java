package HomeTasteGrp.HomeTaste.Repositories;

import HomeTasteGrp.HomeTaste.Models.CompleteProfile;
import HomeTasteGrp.HomeTaste.Models.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompleteProfileRepository extends MongoRepository<CompleteProfile,String> {
    Optional<CompleteProfile> findByUserEntity(UserEntity userEntity);
}
