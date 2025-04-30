package funix.tgcp.guide.request;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import funix.tgcp.notification.NotificationService;
import funix.tgcp.user.Role;
import funix.tgcp.user.User;
import funix.tgcp.user.UserRepository;
import funix.tgcp.util.FileHelper;
import funix.tgcp.util.LogHelper;

@Service
public class GuideRequestService {
	private static final LogHelper logger = new LogHelper(GuideRequestService.class);

	@Autowired
	private GuideRequestRepository guideRequestRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private NotificationService notiService;

	@Autowired
	private FileHelper fileHelper;

	@Transactional
	public GuideRequest registerGuide(Long userId, MultipartFile cccdFile, MultipartFile guideLicenseFile,
			String guideLicense, String experience, boolean isInternationalGuide, boolean isLocalGuide) {

		logger.info(" experience " + experience);

		Optional<User> userOpt = userRepository.findById(userId);
		if (userOpt.isPresent()) {
			User user = userOpt.get();

			GuideRequest request = guideRequestRepository.findByUserId(user.getId());
			if (request != null && request.getStatus() == GuideRequestStatus.PENDING) {
				logger.info(" da ton tai 1 request");
				return null;
			}
			if (request == null) {
				logger.info(" tao guide request moi");
				request = new GuideRequest();
				request.setUser(user);
			}
			request.setStatus(GuideRequestStatus.PENDING);
			request.setExperience(experience);
			request.setGuideLicense(guideLicense);
			request.setInternationalGuide(isInternationalGuide);
			request.setLocalGuide(isLocalGuide);
			try {
				logger.info(" upload file ");
				request.setGuideLicenseUrl(fileHelper.uploadFile(guideLicenseFile));
				request.setCccdUrl(fileHelper.uploadFile(cccdFile));
			} catch (IOException e) {
				e.printStackTrace();
			}

			notiService.sendNotificationToAdmin(
					user.getFullName() + " has sent an application to be a guide, please check it out.",
					"/admin/dashboard#magage-guide-requests");

			return guideRequestRepository.save(request);
		}
		return null;
	}

	public GuideRequest updateGuideRequest(Long userId, MultipartFile cccdFile, MultipartFile guideLicenseFile,
			String guideLicense, String experience, boolean isLocalGuide, boolean isInternationalGuide) {

		logger.info(" experience " + experience);
		GuideRequest existingRequest = guideRequestRepository.findByUserId(userId);

		if (existingRequest != null) {
			existingRequest.setStatus(GuideRequestStatus.PENDING);
			existingRequest.setGuideLicense(guideLicense);
			existingRequest.setExperience(experience);
			existingRequest.setInternationalGuide(isInternationalGuide);
			existingRequest.setLocalGuide(isLocalGuide);
			try {
				String filePath = fileHelper.uploadFile(guideLicenseFile);
				if (filePath != null) {
					fileHelper.deleteFile(existingRequest.getGuideLicenseUrl());
					existingRequest.setGuideLicenseUrl(filePath);
				}

				filePath = fileHelper.uploadFile(cccdFile);
				if (filePath != null) {
					fileHelper.deleteFile(existingRequest.getCccdUrl());
					existingRequest.setCccdUrl(filePath);
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			notiService.sendNotificationToAdmin(
					existingRequest.getUser().getFullName()
							+ " has sent an application to be a guide, please check it out.",
					"/admin/dashboard#magage-guide-requests");
			return guideRequestRepository.save(existingRequest);
		}
		return null;
	}

	@Transactional
	public boolean approveGuide(Long requestId) {
		Optional<GuideRequest> requestOpt = guideRequestRepository.findById(requestId);
		if (requestOpt.isPresent()) {
			GuideRequest request = requestOpt.get();
			request.setStatus(GuideRequestStatus.APPROVED);

			guideRequestRepository.save(request);

			// Cập nhật User thành hướng dẫn viên
			User user = request.getUser();

			user.setRole(Role.ROLE_GUIDE);
			user.setExperience(request.getExperience());
			user.setGuideLicense(request.getGuideLicense());
			user.setGuideLicenseUrl(request.getGuideLicenseUrl());
			user.setInternationalGuide(request.isInternationalGuide());
			user.setLocalGuide(request.isLocalGuide());
			user.setCccd(request.getCccdUrl());

			userRepository.save(user);

			notiService.sendNotification(user, "Your guide application has been approved! Plz create guide services.",
					"/dashboard#manage-services");

			return true;
		}
		return false;
	}

	@Transactional
	public boolean rejectGuide(Long requestId, String reason) {
		Optional<GuideRequest> requestOpt = guideRequestRepository.findById(requestId);
		if (requestOpt.isPresent()) {
			GuideRequest request = requestOpt.get();
			request.setReason(reason);
			request.setStatus(GuideRequestStatus.REJECTED);
			guideRequestRepository.save(request);

			notiService.sendNotification(request.getUser(),
					"Your guide application has been rejected! Plz check the reason.", "/dashboard#guide-register");
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

	public GuideRequest findByUserId(Long userId) {
		return guideRequestRepository.findByUserId(userId);
	}

}
