package pl.puzzle.montroe_blog_cms_be.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CmsUserInitializer implements ApplicationRunner {

    private final CmsUserRepository cmsUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${cms.admin.email:}")
    private String adminEmail;

    @Value("${cms.admin.password:}")
    private String adminPassword;

    public CmsUserInitializer(
            CmsUserRepository cmsUserRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.cmsUserRepository = cmsUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            return;
        }

        if (cmsUserRepository.findByEmail(adminEmail).isPresent()) {
            return;
        }

        CmsUser admin = CmsUser.builder()
                .id(UUID.randomUUID())
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        cmsUserRepository.save(admin);
    }
}