package HomeTasteGrp.HomeTaste.Controllers;

import HomeTasteGrp.HomeTaste.Configurations.UserInfoUserDetails;
import HomeTasteGrp.HomeTaste.Models.UserEntity;
import HomeTasteGrp.HomeTaste.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    private final UserService userService;
    @Autowired
    public UserController(UserService userService){
        this.userService=userService;
    }
    @PostMapping("/add")
    public ResponseEntity<UserEntity> createUser(@RequestBody UserEntity user) {
        try {
            UserEntity createdUser = userService.signup(user);
            if (createdUser.isApproved()) {
                return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(createdUser, HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
    @PutMapping("archive/{id}")
    public ResponseEntity<UserEntity> archiveUser(@PathVariable String id, @RequestBody Map<String, String> requestBody){
        String reason = requestBody.get("reason");
        boolean success = userService.archiveUser(id, reason);
        if(success){
            return  ResponseEntity.ok().build();
        }
        else {
            return  ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/update/{userId}")
    public ResponseEntity<UserEntity> updateUserById(@PathVariable String userId, @RequestBody UserEntity updatedUser) {
        return userService.updateUserById(userId, updatedUser);
    }
    @PutMapping("/approve/{userId}")
    public ResponseEntity<Void> approveUser(@PathVariable String userId) {
        try {
            userService.approveUser(userId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/currentUser")
    public UserInfoUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserInfoUserDetails) {
            return (UserInfoUserDetails) authentication.getPrincipal();
        }
        return null;
    }
}
