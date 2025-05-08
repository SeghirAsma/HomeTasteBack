package HomeTasteGrp.HomeTaste.Configurations;

import HomeTasteGrp.HomeTaste.Models.UserEntity;
import HomeTasteGrp.HomeTaste.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userEntityRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<UserEntity> userE = userEntityRepository.findByEmail(email);
         return userE.map(UserInfoUserDetails::new)
           .orElseThrow(() -> new UsernameNotFoundException("user not found " + email));

    }
}
