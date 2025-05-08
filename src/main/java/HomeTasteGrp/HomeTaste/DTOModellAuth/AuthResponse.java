package HomeTasteGrp.HomeTaste.DTOModellAuth;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Builder
@Data
@NoArgsConstructor
public class AuthResponse implements Serializable {
    private String message;
    private String token;
    public AuthResponse(String message, String token) {
        this.message = message;
        this.token = token;

    }
}
