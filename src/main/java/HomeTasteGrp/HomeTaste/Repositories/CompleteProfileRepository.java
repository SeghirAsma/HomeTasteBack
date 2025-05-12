package HomeTasteGrp.HomeTaste.Repositories;

import HomeTasteGrp.HomeTaste.Models.CompleteProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompleteProfileRepository extends MongoRepository<CompleteProfile,String> {
}
