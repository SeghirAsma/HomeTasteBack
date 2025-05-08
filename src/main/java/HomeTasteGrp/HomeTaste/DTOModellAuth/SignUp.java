package HomeTasteGrp.HomeTaste.DTOModellAuth;

import HomeTasteGrp.HomeTaste.Models.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="usersAuth")
public class SignUp implements Serializable {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @Field("role")
    private Role role;
}
