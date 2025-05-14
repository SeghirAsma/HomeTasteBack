package HomeTasteGrp.HomeTaste.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "CompleteProfiles")
public class CompleteProfile implements Serializable {
    @Id
    private String id;
    @DBRef
    private UserEntity userEntity;
    private String description;
    private String documentUrl;
    private List<String> socialLinks;
    public String profileImgUrl;
    public boolean submitted = false;
}
