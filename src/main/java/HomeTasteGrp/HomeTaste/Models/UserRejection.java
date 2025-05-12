package HomeTasteGrp.HomeTaste.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "UserRejection")
public class UserRejection implements Serializable {
    @Id
    private String id;
    @DBRef
    private UserEntity userEntity;
    private String reason;
}
