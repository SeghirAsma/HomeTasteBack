package HomeTasteGrp.HomeTaste.Configurations;

import HomeTasteGrp.HomeTaste.Models.Role;
import HomeTasteGrp.HomeTaste.Models.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserInfoUserDetails implements UserDetails {
    private String username;
    private String email;
    private String password;
    private List<GrantedAuthority> authorities;
    private UserEntity userEntity;  //

    private PasswordEncoder passwordEncoder;
    private GrantedAuthority authority;
    private Long id;
    private Role role;
    // private String token;


    public UserInfoUserDetails(UserEntity user) {
        this.userEntity = user;

        this.email=user.getEmail();

        this.authorities= Arrays.stream(user.getRole().name().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        this.password = user.getPassword();


    }
    public UserEntity getUserEntity() {
        return userEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email ;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
