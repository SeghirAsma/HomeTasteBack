package HomeTasteGrp.HomeTaste.Repositories;

import HomeTasteGrp.HomeTaste.Models.UserRejection;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRejectionRepository extends MongoRepository<UserRejection, String> {
}
