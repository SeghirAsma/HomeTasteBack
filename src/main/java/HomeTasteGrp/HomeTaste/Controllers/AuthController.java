package HomeTasteGrp.HomeTaste.Controllers;

import HomeTasteGrp.HomeTaste.DTOModellAuth.AuthRequest;
import HomeTasteGrp.HomeTaste.DTOModellAuth.AuthResponse;
import HomeTasteGrp.HomeTaste.Models.UserEntity;
import HomeTasteGrp.HomeTaste.Services.JwtService;
import HomeTasteGrp.HomeTaste.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;
    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticationAndGetToken(@RequestBody AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            if (authentication.isAuthenticated()) {
                // Vérifier si l'utilisateur est approuvé avant de générer le token
                UserEntity user = userService.getUserByEmail(authRequest.getEmail());
                /*if ((!user.getRole().name().equalsIgnoreCase("ADMIN") &&
                        (!user.getRole().name().equalsIgnoreCase("CONSUMER")) && !user.isApproved())) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Le compte n'est pas encore approuvé.");
                } */

                String token = jwtService.generateToken(authRequest.getEmail());
                return ResponseEntity.ok(new AuthResponse("Utilisateur authentifié", token));
            } else {
                throw new UsernameNotFoundException("Invalid user request !!");
            }
        } catch (AuthenticationException e) {
            System.out.println("Authentication exception: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

    }



}
