package funix.tca.util;

import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Set;

public class FileUploadHelper {
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final String UPLOAD_DIR = "uploads/";

    public static String uploadFile(MultipartFile file, String folder, String fileNamePrefix) throws IOException {
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
        
        if(folder != null ) {
        	File uploadFolderPath = new File(UPLOAD_DIR + folder);
        	if (!uploadFolderPath.exists()) {
        		uploadFolderPath.mkdirs();
            }
        }

        // Generate unique file name
        String fileName = fileNamePrefix + "_" + file.getOriginalFilename();
        
        System.out.println("file name : " + fileName);
        
        File destination;
        if(folder != null) {
        	destination = new File(UPLOAD_DIR + folder + fileName);
        } else {
        	destination = new File(UPLOAD_DIR + fileName);
        }
        
        
        // Copy file to destination
        Files.copy(file.getInputStream(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        return "/" + UPLOAD_DIR + (folder != null ? folder : "") + fileName;
    }
}
