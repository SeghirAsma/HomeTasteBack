package HomeTasteGrp.HomeTaste.Services;

import HomeTasteGrp.HomeTaste.Models.Category;
import HomeTasteGrp.HomeTaste.Models.Product;
import HomeTasteGrp.HomeTaste.ModelsDTO.ProductDTO;
import HomeTasteGrp.HomeTaste.ModelsDTO.UserDTO;
import HomeTasteGrp.HomeTaste.Models.UserEntity;
import HomeTasteGrp.HomeTaste.Repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    @Value("${upload.dir}")
    private String uploadDir;
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    @Autowired
    public ProductService(ProductRepository productRepository){
        this.productRepository=productRepository;
    }

    //recovery of all products
    public List<ProductDTO> getAllProducts() {
        Sort sortByCreationDateDesc = Sort.by(Sort.Direction.DESC, "creationDate");
        List<Product> products = productRepository.findAll(sortByCreationDateDesc);
        return products.stream()
                .map(this::mapToProductDTO)
                .collect(Collectors.toList());
    }
    //recovery all products by category
    public List<ProductDTO> getProductsByCategory(String category) {
        List<Product> products = productRepository.findByCategoryOrderByCreationDateDesc(category);
        return products.stream()
                .map(this::mapToProductDTO)
                .collect(Collectors.toList());
    }

    //mapping product
    private ProductDTO mapToProductDTO(Product product) {
        UserEntity user = product.getUserEntity();

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());

        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setNameProduct(product.getNameProduct());
        dto.setDescriptionProduct(product.getDescriptionProduct());
        dto.setCategory(product.getCategory().toString());
        dto.setFileNameProduct(product.getFileNameProduct());
        dto.setCreationDate(product.getCreationDate());
        dto.setUser(userDTO);

        return dto;
    }

    //create products
    public ProductDTO createProduct(String nameProduct, Category category, String descriptionProduct, MultipartFile fileNameProduct, UserEntity authenticatedUser) {
        try {

            Product product = new Product();
            product.setNameProduct(nameProduct);
            product.setDescriptionProduct(descriptionProduct);
            product.setCategory(category);
            product.setUserEntity(authenticatedUser);

            // Enregistrez le fichier sur le serveur et obtenez l'URL du fichier
            String FileUrl = saveFile(fileNameProduct);
            product.setFileNameProduct(FileUrl);

            Product savedProduct = productRepository.save(product);
            return mapToProductDTO(savedProduct);
        } catch (Exception e) {
            throw new RuntimeException("error", e);
        }
    }
    //saving file
    public String saveFile(MultipartFile fileNameProduct) throws IOException {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Générez un nom de fichier unique
        String fileName = StringUtils.cleanPath(fileNameProduct.getOriginalFilename());
        String uniqueFileName = fileName;

        // Chemin complet du fichier sur le serveur
        Path targetPath = Path.of(uploadDir, uniqueFileName);

        // Copiez le fichier vers le répertoire de stockage
        Files.copy(fileNameProduct.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        // Retournez l'URL ou le chemin du fichier
        return uploadDir + uniqueFileName;

    }

    //loading resource
    public Resource loadAsResource(String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            logger.info("Chemin du fichier  : {}", filePath);
            Resource videoFile = new UrlResource(filePath.toUri());

            if (videoFile.exists() && videoFile.isReadable()) {
                return videoFile;
            } else {
                logger.error("Could not read the file! Path: {}", filePath);
                throw new RuntimeException("Could not read the video file!");
            }
        } catch (MalformedURLException e) {
            logger.error("Error: {}", e.getMessage());
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    //update product
    public ResponseEntity<ProductDTO> updateProductById(String id, ProductDTO updatedProducted, UserEntity authenticatedUser) {
        Optional<Product> existingProductOptional = productRepository.findById(id);
        if (existingProductOptional.isPresent()) {
            Product existingProduct = existingProductOptional.get();
            existingProduct.setNameProduct(updatedProducted.getNameProduct());
            existingProduct.setDescriptionProduct(updatedProducted.getDescriptionProduct());
            existingProduct.setCategory(Category.valueOf(updatedProducted.getCategory()));
            Product savedProduct = productRepository.save(existingProduct);
            return new ResponseEntity<>(mapToProductDTO(savedProduct), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    //recovery products of user connected
    public List<ProductDTO> getProductsByAuthenticatedUser(UserEntity authenticatedUser) {
        List<Product> products = productRepository.findByUserEntityAndIsDeletedFalse(authenticatedUser);
        return products.stream()
                .map(this::mapToProductDTO)
                .collect(Collectors.toList());
    }
    //archived products
    public boolean archiveProduct(String id){
        if(productRepository.findById(id).isPresent()){
            Product product = productRepository.findById(id).get();
            product.setDeleted(true);
            productRepository.save(product);
            return  true;
        }
        return  false;
    }

}
