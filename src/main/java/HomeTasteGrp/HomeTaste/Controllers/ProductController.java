package HomeTasteGrp.HomeTaste.Controllers;

import HomeTasteGrp.HomeTaste.Configurations.UserInfoUserDetails;
import HomeTasteGrp.HomeTaste.Models.Category;
import HomeTasteGrp.HomeTaste.Models.Product;
import HomeTasteGrp.HomeTaste.Models.UserEntity;
import HomeTasteGrp.HomeTaste.ModelsDTO.ProductDTO;
import HomeTasteGrp.HomeTaste.Services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:3000")
public class ProductController {
    private final ProductService productService;
    @Autowired
    public ProductController(ProductService productService){
        this.productService=productService;
    }
    @Value("${upload.dir}")
    private String uploadDir;
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductDTO> createProduct(
            @RequestParam("nameProduct") String nameProduct,
            @RequestParam("category") Category category,
            @RequestParam("descriptionProduct") String descriptionProduct,
            @RequestParam("fileNameProduct") MultipartFile fileNameProduct,
            @AuthenticationPrincipal UserDetails userDetails) {
        // Check if the authenticated user has the required role
        if (userDetails.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ADMIN") || role.getAuthority().equals("SELLER"))
        ) {

            UserEntity authenticatedUser = ((UserInfoUserDetails) userDetails).getUserEntity();

            ProductDTO createdProduct = productService.createProduct(
                    nameProduct, category, descriptionProduct, fileNameProduct, authenticatedUser);

            return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/file/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) throws MalformedURLException {
        Resource file = productService.loadAsResource(filename);
        Path filePath = Paths.get(uploadDir).resolve(filename);

        String contentType;
        try {
            contentType = Files.probeContentType(filePath);
        } catch (IOException e) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<ProductDTO> updateProductById(@PathVariable String id, @RequestBody ProductDTO updatedProduct,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ADMIN") || role.getAuthority().equals("SELLER"))
        ) {

            UserEntity authenticatedUser = ((UserInfoUserDetails) userDetails).getUserEntity();

            return productService.updateProductById(id, updatedProduct, authenticatedUser);
    }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();}

    @GetMapping("/my-products")
    public ResponseEntity<List<ProductDTO>> getProductsByAuthenticatedUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserEntity authenticatedUser = ((UserInfoUserDetails) userDetails).getUserEntity();

        List<ProductDTO> products = productService.getProductsByAuthenticatedUser(authenticatedUser);

        return ResponseEntity.ok(products);
    }
    @PutMapping("/archiveProduct/{id}")
    public ResponseEntity<Product> archiveProduct(@PathVariable String id){
        boolean success = productService.archiveProduct(id);
        if(success){
            return  ResponseEntity.ok().build();
        }
        else {
            return  ResponseEntity.notFound().build();
        }
    }

}
