package HomeTasteGrp.HomeTaste.Controllers;

import HomeTasteGrp.HomeTaste.Configurations.UserInfoUserDetails;
import HomeTasteGrp.HomeTaste.Models.CompleteProfile;
import HomeTasteGrp.HomeTaste.Models.UserEntity;
import HomeTasteGrp.HomeTaste.Services.CompleteProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/Profile")
public class CompleteProfileController {
    private final CompleteProfileService completeProfileService;
    @Autowired
    public CompleteProfileController(CompleteProfileService completeProfileService){
        this.completeProfileService=completeProfileService;
    }

    @PostMapping(value = "/complete", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CompleteProfile> createInfoSupp(
            @RequestParam("description") String description,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("documentUrl") MultipartFile documentUrl,
            @RequestParam("profileImgUrl") MultipartFile profileImgUrl,
            @RequestParam("socialLinks") List<String> socialLinks) {
        if (userDetails.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("SELLER"))
        ) {

            UserEntity authenticatedUser = ((UserInfoUserDetails) userDetails).getUserEntity();
            CompleteProfile completeProfile = completeProfileService.createInfoSupp(
                    description, authenticatedUser, documentUrl, profileImgUrl, socialLinks);

            return new ResponseEntity<>(completeProfile, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
