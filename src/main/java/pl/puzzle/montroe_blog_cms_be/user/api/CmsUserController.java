package pl.puzzle.montroe_blog_cms_be.user.api;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.puzzle.montroe_blog_cms_be.user.CmsUser;
import pl.puzzle.montroe_blog_cms_be.user.CmsUserCreateService;
import pl.puzzle.montroe_blog_cms_be.user.CmsUserGetAllService;
import pl.puzzle.montroe_blog_cms_be.user.CmsUserUpdateService;
import pl.puzzle.montroe_blog_cms_be.user.dto.CmsUserCreateRequest;
import pl.puzzle.montroe_blog_cms_be.user.dto.CmsUserEnabledUpdateRequest;
import pl.puzzle.montroe_blog_cms_be.user.dto.CmsUserResponse;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/users")
public class CmsUserController {

    private final CmsUserCreateService cmsUserCreateService;
    private final CmsUserGetAllService cmsUserGetAllService;
    private final CmsUserUpdateService cmsUserUpdateService;

    public CmsUserController(CmsUserCreateService cmsUserCreateService, CmsUserGetAllService cmsUserGetAllService, CmsUserUpdateService cmsUserUpdateService) {
        this.cmsUserCreateService = cmsUserCreateService;
        this.cmsUserGetAllService = cmsUserGetAllService;
        this.cmsUserUpdateService = cmsUserUpdateService;
    }

    @PostMapping
    public ResponseEntity<CmsUserResponse> createUser(
            @Valid @RequestBody CmsUserCreateRequest request
    ) {
        CmsUser user = cmsUserCreateService.createUser(request);

        return ResponseEntity.ok(
                CmsUserResponse.from(user)
        );
    }

    @GetMapping
    public ResponseEntity<List<CmsUserResponse>> getAllUsers() {
        return ResponseEntity.ok(
                cmsUserGetAllService.getAllUsers()
        );
    }

    @PatchMapping("/{id}/enabled")
    public ResponseEntity<CmsUserResponse> updateEnabled(
            @PathVariable UUID id,
            @RequestBody CmsUserEnabledUpdateRequest request
    ) {
        return ResponseEntity.ok(
                cmsUserUpdateService.updateEnabled(id, request)
        );
    }
}