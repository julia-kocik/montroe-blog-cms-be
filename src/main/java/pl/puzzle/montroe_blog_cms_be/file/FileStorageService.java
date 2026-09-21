package pl.puzzle.montroe_blog_cms_be.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.puzzle.montroe_blog_cms_be.exception.InvalidFileException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final long MAX_FILE_SIZE =
            5 * 1024 * 1024;

    private static final Map<String, String> ALLOWED_TYPES =
            Map.of(
                    "image/jpeg", ".jpg",
                    "image/png", ".png",
                    "image/webp", ".webp"
            );

    private final S3Client r2Client;
    private final String bucketName;

    public FileStorageService(
            S3Client r2Client,
            @Value("${r2.bucket}") String bucketName
    ) {
        this.r2Client = r2Client;
        this.bucketName = bucketName;
    }

    public String uploadImage(MultipartFile file) {
        validateImage(file);

        byte[] bytes;

        try {
            bytes = file.getBytes();
        } catch (IOException exception) {
            throw new InvalidFileException(
                    "Failed to read image"
            );
        }

        validateFileSignature(
                bytes,
                file.getContentType()
        );

        String extension =
                ALLOWED_TYPES.get(file.getContentType());

        String key =
                "articles/"
                        + UUID.randomUUID()
                        + extension;

        PutObjectRequest request =
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .contentType(file.getContentType())
                        .build();

        r2Client.putObject(
                request,
                RequestBody.fromBytes(bytes)
        );

        return key;
    }

    public void deleteImage(String key) {
        validateKey(key);

        DeleteObjectRequest request =
                DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build();

        r2Client.deleteObject(request);
    }

    private void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException(
                    "Image cannot be empty"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException(
                    "Image cannot exceed 5 MB"
            );
        }

        String contentType = file.getContentType();

        if (
                contentType == null
                        || !ALLOWED_TYPES.containsKey(
                        contentType
                )
        ) {
            throw new InvalidFileException(
                    "Only JPG, PNG and WebP images are allowed"
            );
        }
    }

    private void validateFileSignature(
            byte[] bytes,
            String contentType
    ) {
        boolean valid = switch (contentType) {
            case "image/jpeg" -> isJpeg(bytes);
            case "image/png" -> isPng(bytes);
            case "image/webp" -> isWebp(bytes);
            default -> false;
        };

        if (!valid) {
            throw new InvalidFileException(
                    "File content does not match image type"
            );
        }
    }

    private boolean isJpeg(byte[] bytes) {
        return bytes.length >= 2
                && (bytes[0] & 0xFF) == 0xFF
                && (bytes[1] & 0xFF) == 0xD8;
    }

    private boolean isPng(byte[] bytes) {
        return bytes.length >= 8
                && (bytes[0] & 0xFF) == 0x89
                && (bytes[1] & 0xFF) == 0x50
                && (bytes[2] & 0xFF) == 0x4E
                && (bytes[3] & 0xFF) == 0x47
                && (bytes[4] & 0xFF) == 0x0D
                && (bytes[5] & 0xFF) == 0x0A
                && (bytes[6] & 0xFF) == 0x1A
                && (bytes[7] & 0xFF) == 0x0A;
    }

    private boolean isWebp(byte[] bytes) {
        return bytes.length >= 12
                && bytes[0] == 'R'
                && bytes[1] == 'I'
                && bytes[2] == 'F'
                && bytes[3] == 'F'
                && bytes[8] == 'W'
                && bytes[9] == 'E'
                && bytes[10] == 'B'
                && bytes[11] == 'P';
    }

    private void validateKey(String key) {
        if (
                key == null
                        || !key.startsWith("articles/")
        ) {
            throw new InvalidFileException(
                    "Invalid image key"
            );
        }
    }
}