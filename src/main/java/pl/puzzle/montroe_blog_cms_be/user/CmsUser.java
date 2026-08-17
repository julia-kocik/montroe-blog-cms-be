package pl.puzzle.montroe_blog_cms_be.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.puzzle.montroe_blog_cms_be.user.dto.CmsUserCreateRequest;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "cms_user")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class CmsUser {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public static CmsUser create(
            CmsUserCreateRequest request,
            String encodedPassword
    ) {
        return CmsUser.builder()
                .id(UUID.randomUUID())
                .email(request.email())
                .password(encodedPassword)
                .role(Role.ADMIN)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void changeEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}