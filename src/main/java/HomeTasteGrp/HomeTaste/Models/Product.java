package HomeTasteGrp.HomeTaste.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "Product")
public class Product {
    private String id;
    private String nameProduct;
    private String descriptionProduct;
    @Field("category")
    private Category category;
    private String fileNameProduct;
    @DBRef
    private UserEntity userEntity;
    private boolean isDeleted;
    @Field("creation_Date")
    @CreatedDate
    private Date creationDate=new Date();
}
