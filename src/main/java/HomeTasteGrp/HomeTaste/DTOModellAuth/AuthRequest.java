package HomeTasteGrp.HomeTaste.DTOModellAuth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest implements Serializable {
    private String email;
    private String password;
}
