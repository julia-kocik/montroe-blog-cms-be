package pl.puzzle.montroe_blog_cms_be.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.puzzle.montroe_blog_cms_be.auth.dto.ChangePasswordRequest;
import pl.puzzle.montroe_blog_cms_be.exception.InvalidPasswordException;
import pl.puzzle.montroe_blog_cms_be.exception.NotFoundException;
import pl.puzzle.montroe_blog_cms_be.user.CmsUser;
import pl.puzzle.montroe_blog_cms_be.user.CmsUserRepository;

@Service
public class ChangePasswordService {

    private final CmsUserRepository cmsUserRepository;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordService(
            CmsUserRepository cmsUserRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.cmsUserRepository = cmsUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void changePassword(
            String email,
            ChangePasswordRequest request
    ) {
        CmsUser user = cmsUserRepository.findByEmail(email)
                .orElseThrow(() ->
                        new NotFoundException("User not found")
                );

        if (!passwordEncoder.matches(
                request.currentPassword(),
                user.getPassword()
        )) {
            throw new InvalidPasswordException(
                    "Current password is incorrect"
            );
        }

        user.changePassword(
                passwordEncoder.encode(request.newPassword())
        );
    }
}