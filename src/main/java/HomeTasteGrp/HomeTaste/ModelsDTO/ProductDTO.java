package HomeTasteGrp.HomeTaste.ModelsDTO;

import lombok.Data;

import java.util.Date;

@Data
public class ProductDTO {
    private String id;
    private String nameProduct;
    private String descriptionProduct;
    private String category;
    private String fileNameProduct;
    private Date creationDate;
    private UserDTO user;
}
