package HomeTasteGrp.HomeTaste.Services;

import HomeTasteGrp.HomeTaste.EmailSending.UserApprovedEvent;
import HomeTasteGrp.HomeTaste.Models.CompleteProfile;
import HomeTasteGrp.HomeTaste.Models.UserEntity;
import HomeTasteGrp.HomeTaste.Repositories.CompleteProfileRepository;
import HomeTasteGrp.HomeTaste.Repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        profile.setSubmitted(true);

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


  //@Scheduled(cron = "0 * * * * ?") // chaque minute
  @Scheduled(cron = "0 0 10 * * ?") // tous les jours à 10h
    public void remindUsersToCompleteProfile() {
        Date now = new Date();
        List<UserEntity> pendingUsers = userRepository.findByApprovedFalseAndIsDeletedFalse();

        for (UserEntity user : pendingUsers) {
            long diffInMillies = now.getTime() - user.getCreationDate().getTime();
            long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
            if (diffInDays >= 2) {
                boolean hasSubmittedProfile = completeProfileRepository
                        .findByUserEntity(user)
                        .map(CompleteProfile::isSubmitted)
                        .orElse(false);

                if (!hasSubmittedProfile) {
                    sendReminderEmail(user);
                }
            }
        }
    }
    private void sendReminderEmail(UserEntity user) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

            String subject = "Complete your profile";
            String logoHtml = "<img src=\"https://img.freepik.com/premium-vector/creative-logo-small-business-owners-thank-you-shopping-small-quote-vector-illustration-flat_87447-1440.jpg\" " +
                    "alt=\"Logo\" width=\"150\" height=\"150\">";
            String content = "<p>Dear " + user.getFirstName() + " " + user.getLastName() + ",</p>" +
                    "<p>It looks like you haven't completed your profile yet. Please finish it as soon as possible to be considered by the admin team.</p>" +
                    "<p>Sincerely,</p>" +
                    "<p>MadeHome Team</p>" +
                    logoHtml;

            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(content, true);
            helper.setFrom("madehome.team@gmail.com");

            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error while sending email", e);
        }
    }
}
