package funix.tgcp.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.guide.request.GuideRequestService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Component
public class FileHelper {
    private static final Set<String> 	ALLOWED_MIME_TYPES 		= Set.of("image/jpeg", "image/png", "image/webp");
    private static final String 		UPLOAD_DIR 				= "uploads/";    
    private static final String 		UPLOAD_DIR_ABSOLUTE 	= Paths.get("uploads").toAbsolutePath().toString();
    
    
	private static final LogHelper logger = new LogHelper(FileHelper.class);
	
	
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
        
        logger.info(" " + fileName );
        
        
        File destination;        
        destination = new File(UPLOAD_DIR + fileName);              
        
        // Copy file to destination
        Files.copy(file.getInputStream(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        return "/" + UPLOAD_DIR + fileName;
    }
    
    
    public void deleteFile(String fileUrl) throws IOException {
        if (fileUrl != null) {
            // Log the original file path
            logger.info("path " + fileUrl.replaceFirst("^/uploads", ""));
            Path filePath = Paths.get(UPLOAD_DIR_ABSOLUTE, fileUrl.replaceFirst("^/uploads", ""));
            
            // Check if file exists
            if (Files.exists(filePath)) {
                logger.info("File exists: " + filePath);
                
                // Attempt to delete the file
                logger.info("Attempting to delete file at: " + filePath.toAbsolutePath());
                boolean isDeleted = Files.deleteIfExists(filePath);
                logger.info("isDeleted: " + isDeleted);
            } else {
                logger.info("File not found: " + filePath);
            }
        }
    }
}
