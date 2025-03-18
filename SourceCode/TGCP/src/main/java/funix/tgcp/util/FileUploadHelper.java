package funix.tgcp.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Component
public class FileUploadHelper {
    private final Set<String> ALLOWED_MIME_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private final String UPLOAD_DIR = "uploads/";

    public String uploadFile(MultipartFile file) throws IOException {
        // Validate file type
    	
    	if (file == null || file.isEmpty()) {
            // Không có file nào được upload, bỏ qua cập nhật avatar
    		System.out.println("file null or empty");
            return null;
        }
    	
        String contentType = file.getContentType();        
        
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid file type. Only JPEG, PNG, and WEBP are allowed.");
        }

        // Create directory if not exists
        File uploadPath = new File(UPLOAD_DIR);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }
        


        // Generate unique file name
        String fileName = System.currentTimeMillis() + "_" + UUID.randomUUID()  + "_" + file.getOriginalFilename();
        
        
        File destination;        
        destination = new File(UPLOAD_DIR + fileName);              
        
        // Copy file to destination
        Files.copy(file.getInputStream(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        return "/" + UPLOAD_DIR + fileName;
    }
}
