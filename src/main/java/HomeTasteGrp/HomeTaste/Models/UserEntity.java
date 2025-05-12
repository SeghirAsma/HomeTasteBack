package HomeTasteGrp.HomeTaste.Models;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Users")

public class UserEntity implements Serializable {
    @Id
    private String id;
    private String firstName ;
    private String lastName;
    private String email;
    private String password;
    private String address;
    private String phoneNumber;
    @Field("role")
    private Role role;
    private boolean approved ;
    private boolean isDeleted = false;
    public String profileImageUrl;
    private String rejectionReason;
    @Field("creation_Date")
    @CreatedDate
    private Date creationDate=new Date();

}
