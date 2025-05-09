package HomeTasteGrp.HomeTaste.Repositories;

import HomeTasteGrp.HomeTaste.Models.Product;
import HomeTasteGrp.HomeTaste.Models.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByCategoryOrderByCreationDateDesc(String category);
    List<Product> findByUserEntityAndIsDeletedFalse(UserEntity userEntity);


}
