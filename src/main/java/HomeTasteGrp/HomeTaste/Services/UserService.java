package HomeTasteGrp.HomeTaste.Services;

import HomeTasteGrp.HomeTaste.EmailSending.UserApprovedEvent;
import HomeTasteGrp.HomeTaste.Models.UserEntity;
import HomeTasteGrp.HomeTaste.Models.UserRejection;
import HomeTasteGrp.HomeTaste.Repositories.UserRejectionRepository;
import HomeTasteGrp.HomeTaste.Repositories.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserRejectionRepository userRejectionRepository;
    private final JavaMailSender emailSender;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private UserService(UserRepository userRepository,JavaMailSender emailSender,UserRejectionRepository userRejectionRepositor){
        this.userRepository=userRepository;
        this.emailSender=emailSender;
        this.userRejectionRepository=userRejectionRepositor;
    }
    public List<UserEntity> getAllUsers(){
        return userRepository.findAll();
    }
    public UserEntity signup(UserEntity user) {
        // Vérifiez si un utilisateur avec la même adresse e-mail existe déjà
        Optional<UserEntity> existingUserOptional = userRepository.findByEmail(user.getEmail());
        if (existingUserOptional.isPresent()) {
            // Si l'utilisateur existe, mettez à jour les informations au lieu de créer un nouvel utilisateur
            UserEntity existingUser = existingUserOptional.get();
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setLastName(user.getAddress());
            existingUser.setLastName(user.getPhoneNumber());
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            if ((existingUser.getRole().name().equalsIgnoreCase("ADMIN")) ||
            (existingUser.getRole().name().equalsIgnoreCase("CONSUMER"))){
                existingUser.setApproved(true);
            } else {
                existingUser.setApproved(true);
            }
            return userRepository.save(existingUser);
        } else {
            // Sinon, créez un nouvel utilisateur
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            if ((user.getRole().name().equalsIgnoreCase("ADMIN")) ||
            (user.getRole().name().equalsIgnoreCase("CONSUMER"))) {
                user.setApproved(true);
            } else {
                user.setApproved(false);
            }
            return userRepository.save(user);
        }
    }
    public ResponseEntity<UserEntity> updateUserById(String userId, UserEntity updatedUser) {
        Optional<UserEntity> existingUserOptional = userRepository.findById(userId);

        if (existingUserOptional.isPresent()) {
            UserEntity existingUser = existingUserOptional.get();
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setEmail(updatedUser.getEmail());
            existingUser.setAddress(updatedUser.getAddress());
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
            UserEntity savedUser = userRepository.save(existingUser);
            return new ResponseEntity<>(savedUser, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'adresse e-mail: " + email));
    }
    public ResponseEntity<String> approveUser(String userId) {
        try {
            UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setApproved(true);
            userRepository.save(user);

            String firstName = user.getFirstName();
            String lastName = user.getLastName();

            String userEmail = user.getEmail();
            String subject = "Account Approved";
            String logoHtml = "<img src=\"https://img.freepik.com/premium-vector/creative-logo-small-business-owners-thank-you-shopping-small-quote-vector-illustration-flat_87447-1440.jpg\" " +
                    "alt=\"Logo\" width=\"150\" height=\"150\">";

            String emailText = "<html><body>" +
                    "<p>Dear " + firstName + " " + lastName + ",</p>" +
                    "<p>Your account has been approved. You can now access the platform and start using our services.</p>" +
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

            return new ResponseEntity<>("User approved successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error approving user: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    public boolean archiveUser(String id, String reason){
        if(userRepository.findById(id).isPresent()){
            UserEntity user= userRepository.findById(id).get();
            user.setDeleted(true);
            userRepository.save(user);

            UserRejection rejection = new UserRejection();
            rejection.setUserEntity(user);
            rejection.setReason(reason);
            userRejectionRepository.save(rejection);

            try {
                String firstName = user.getFirstName();
                String lastName = user.getLastName();

                String userEmail = user.getEmail();
                String subject = "Account Rejected";
                String logoHtml = "<img src=\"https://img.freepik.com/premium-vector/creative-logo-small-business-owners-thank-you-shopping-small-quote-vector-illustration-flat_87447-1440.jpg\" " +
                        "alt=\"Logo\" width=\"150\" height=\"150\">";

                String emailText = "<html><body>" +
                        "<p>Dear " + firstName + " " + lastName + ",</p>" +
                        "<p>Your account has been rejected.</p>" +
                        "<p><strong>Reason:</strong> " + reason + "</p>" +
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
            }
                catch (MessagingException e) {
                    throw new RuntimeException("Failed to send rejection email", e);
                }
            return  true;
        }
        return  false;
    }

}
