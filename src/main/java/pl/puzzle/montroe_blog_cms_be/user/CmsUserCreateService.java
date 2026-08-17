package pl.puzzle.montroe_blog_cms_be.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.puzzle.montroe_blog_cms_be.exception.UserAlreadyExistsException;
import pl.puzzle.montroe_blog_cms_be.user.dto.CmsUserCreateRequest;

@Service
public class CmsUserCreateService {

    private final CmsUserRepository cmsUserRepository;
    private final PasswordEncoder passwordEncoder;

    public CmsUserCreateService(
            CmsUserRepository cmsUserRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.cmsUserRepository = cmsUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CmsUser createUser(CmsUserCreateRequest request) {
        if (cmsUserRepository.findByEmail(request.email()).isPresent()) {
            throw new UserAlreadyExistsException();
        }

        String encodedPassword =
                passwordEncoder.encode(request.password());

        CmsUser user = CmsUser.create(
                request,
                encodedPassword
        );

        return cmsUserRepository.save(user);
    }
}