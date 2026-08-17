package pl.puzzle.montroe_blog_cms_be.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.puzzle.montroe_blog_cms_be.exception.LastActiveAdminException;
import pl.puzzle.montroe_blog_cms_be.exception.NotFoundException;
import pl.puzzle.montroe_blog_cms_be.user.dto.CmsUserEnabledUpdateRequest;
import pl.puzzle.montroe_blog_cms_be.user.dto.CmsUserResponse;

import java.util.UUID;

@Service
public class CmsUserUpdateService {

    private final CmsUserRepository cmsUserRepository;

    public CmsUserUpdateService(
            CmsUserRepository cmsUserRepository
    ) {
        this.cmsUserRepository = cmsUserRepository;
    }

    @Transactional
    public CmsUserResponse updateEnabled(
            UUID id,
            CmsUserEnabledUpdateRequest request
    ) {
        CmsUser user = cmsUserRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("User not found")
                );

        if (!request.enabled()
                && user.getRole() == Role.ADMIN
                && user.isEnabled()
                && cmsUserRepository.countByRoleAndEnabledTrue(Role.ADMIN) <= 1) {

            throw new LastActiveAdminException();
        }

        user.changeEnabled(request.enabled());

        return CmsUserResponse.from(user);
    }
}