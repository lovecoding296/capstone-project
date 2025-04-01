package funix.tgcp.guide.request;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.home.controller.HomeController;
import funix.tgcp.user.Role;
import funix.tgcp.user.User;
import funix.tgcp.user.UserRepository;
import funix.tgcp.util.FileUploadHelper;
import funix.tgcp.util.LogHelper;

@Service
public class GuideRequestService {
	private static final LogHelper logger = new LogHelper(GuideRequestService.class);

	
    @Autowired
    private GuideRequestRepository guideRequestRepository;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FileUploadHelper fileUploadHelper;

    public boolean registerGuide(Long userId, 
    		MultipartFile guideLicenseFile, 
    		String guideLicense, 
    		String experience) {
    	
    	System.out.println("GuideRequestService registerGuide");
    	logger.info("yyy");
    	
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            if (guideRequestRepository.existsByUserAndStatus(user, GuideRequestStatus.PENDING)) {
                return false;
            }

            GuideRequest request = new GuideRequest();
            request.setUser(user);
            request.setStatus(GuideRequestStatus.PENDING);
            request.setExperience(experience);
            request.setGuideLicense(guideLicense);
            try {
				request.setGuideLicenseUrl(fileUploadHelper.uploadFile(guideLicenseFile));
				guideRequestRepository.save(request);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return false;
    }

    public boolean approveGuide(Long requestId) {
        Optional<GuideRequest> requestOpt = guideRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            GuideRequest request = requestOpt.get();
            request.setStatus(GuideRequestStatus.APPROVED);
            guideRequestRepository.save(request);

            // Cập nhật User thành hướng dẫn viên
            User user = request.getUser();
            user.setRole(Role.ROLE_GUIDE);
            userRepository.save(user);

            return true;
        }
        return false;
    }

    public boolean rejectGuide(Long requestId, String reason) {
        Optional<GuideRequest> requestOpt = guideRequestRepository.findById(requestId);
        if (requestOpt.isPresent()) {
            GuideRequest request = requestOpt.get();
            request.setReason(reason);
            request.setStatus(GuideRequestStatus.REJECTED);
            guideRequestRepository.save(request);
            return true;
        }
        return false;
    }

	public List<GuideRequest> findAll() {
		return guideRequestRepository.findAll();
	}

	public List<GuideRequest> getAllGuideRequests() {
		return guideRequestRepository.findByStatus(GuideRequestStatus.PENDING);
	}



}

