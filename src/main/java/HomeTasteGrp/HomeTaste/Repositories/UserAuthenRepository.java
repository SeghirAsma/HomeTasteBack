package HomeTasteGrp.HomeTaste.Repositories;

import HomeTasteGrp.HomeTaste.DTOModellAuth.SignUp;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAuthenRepository extends MongoRepository<SignUp, String> {
}
