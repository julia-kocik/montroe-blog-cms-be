package pl.puzzle.montroe_blog_cms_be.file;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(
            FileStorageService fileStorageService
    ) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/images")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file
    ) {
        String key =
                fileStorageService.uploadImage(file);

        return ResponseEntity.ok(
                Map.of("key", key)
        );
    }

    @DeleteMapping("/images")
    public ResponseEntity<Void> deleteImage(
            @RequestParam String key
    ) {
        fileStorageService.deleteImage(key);

        return ResponseEntity.noContent().build();
    }
}