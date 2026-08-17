package pl.puzzle.montroe_blog_cms_be.user;

import org.springframework.stereotype.Service;
import pl.puzzle.montroe_blog_cms_be.user.dto.CmsUserResponse;

import java.util.List;

@Service
public class CmsUserGetAllService {

    private final CmsUserRepository cmsUserRepository;

    public CmsUserGetAllService(
            CmsUserRepository cmsUserRepository
    ) {
        this.cmsUserRepository = cmsUserRepository;
    }

    public List<CmsUserResponse> getAllUsers() {
        return cmsUserRepository.findAll()
                .stream()
                .map(CmsUserResponse::from)
                .toList();
    }
}