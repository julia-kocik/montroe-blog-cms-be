package pl.puzzle.montroe_blog_cms_be.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.puzzle.montroe_blog_cms_be.user.CmsUser;
import pl.puzzle.montroe_blog_cms_be.user.CmsUserRepository;

@Service
public class CmsUserDetailsService implements UserDetailsService {

    private final CmsUserRepository cmsUserRepository;

    public CmsUserDetailsService(
            CmsUserRepository cmsUserRepository
    ) {
        this.cmsUserRepository = cmsUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        CmsUser user = cmsUserRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .disabled(!user.isEnabled())
                .build();
    }
}