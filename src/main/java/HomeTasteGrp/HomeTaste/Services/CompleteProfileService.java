package HomeTasteGrp.HomeTaste.Services;

import HomeTasteGrp.HomeTaste.EmailSending.UserApprovedEvent;
import HomeTasteGrp.HomeTaste.Models.Category;
import HomeTasteGrp.HomeTaste.Models.CompleteProfile;
import HomeTasteGrp.HomeTaste.Models.Product;
import HomeTasteGrp.HomeTaste.Models.UserEntity;
import HomeTasteGrp.HomeTaste.ModelsDTO.ProductDTO;
import HomeTasteGrp.HomeTaste.Repositories.CompleteProfileRepository;
import HomeTasteGrp.HomeTaste.Repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class CompleteProfileService {
    @Autowired
    private CompleteProfileRepository completeProfileRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    private final ProductService productService;
    private final JavaMailSender emailSender;
    @Autowired
    public CompleteProfileService(ProductService productService,JavaMailSender emailSender){
        this.productService=productService;
        this.emailSender=emailSender;
    }

    public CompleteProfile createInfoSupp(String description, UserEntity authenticatedUser,
                                          MultipartFile documentUrl, MultipartFile profileImgUrl, List<String> socialLinks) {
        CompleteProfile profile = new CompleteProfile();
        profile.setDescription(description);
        profile.setUserEntity(authenticatedUser);
        profile.setSocialLinks(socialLinks);

        try {
            String fileUrl = productService.saveFile(documentUrl);
            profile.setDocumentUrl(fileUrl);
            String ImageUrl = productService.saveFile(profileImgUrl);
            profile.setProfileImgUrl(ImageUrl);
            authenticatedUser.setProfileImageUrl(ImageUrl);
            userRepository.save(authenticatedUser);

            String firstName = authenticatedUser.getFirstName();
            String lastName = authenticatedUser.getLastName();

            String userEmail = authenticatedUser.getEmail();
            String subject = "Profile received – pending admin approval";
            String logoHtml = "<img src=\"https://img.freepik.com/premium-vector/creative-logo-small-business-owners-thank-you-shopping-small-quote-vector-illustration-flat_87447-1440.jpg\" " +
                    "alt=\"Logo\" width=\"150\" height=\"150\">";

            String emailText = "<html><body>" +
                    "<p>Dear " + firstName + " " + lastName + ",</p>" +
                    "<p>Your profile has been successfully submitted. An administrator will review your account, and you will receive an email once it is approved.</p>" +
                    "<p>Sincerely,</p>" +
                    "<p>MadeHome Team</p>" +
                    logoHtml +
                    "</body></html>";

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            helper.setTo(userEmail);
            helper.setSubject(subject);
            helper.setText(emailText, true);
            helper.setFrom("madehome.team@gmail.com");

            emailSender.send(message);

            // Déclencher l'événement d'approbation
            eventPublisher.publishEvent(new UserApprovedEvent(this, userEmail));
        } catch (IOException | MessagingException e) {
            throw new RuntimeException("Error while processing profile or sending email", e);
        }
        return completeProfileRepository.save(profile);
    }

}
