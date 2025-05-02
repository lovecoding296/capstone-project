package funix.tgcp.guide.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import funix.tgcp.booking.payment.PaymentOption;
import funix.tgcp.config.CustomUserDetails;
import funix.tgcp.user.City;
import funix.tgcp.user.Language;
import funix.tgcp.user.User;

@RestController
@RequestMapping("/api/guide-services")
public class GuideServiceRestController {

	private static final Logger logger = LoggerFactory.getLogger(GuideServiceRestController.class);

	@Autowired
	private GuideServiceService guideServiceService;

	@PostMapping
	public ResponseEntity<String> createGuideService(@RequestBody GuideService guideService,
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		logger.info("userDetails " + userDetails);
		if (userDetails == null) {
			User guide = new User();
			guide.setId(5L);
			guideService.setGuide(guide);
		} else {
			guideService.setGuide(userDetails.getUser());
		}

		logger.info(guideService.getType() + " " + guideService.getPrice() + " " + guideService.getCity() + " "
				+ guideService.getGroupSizeCategory() + " " + guideService.getLanguage() + " ");

		try {
			guideServiceService.createGuideService(guideService);
			return ResponseEntity.status(HttpStatus.CREATED).body("Service added successfully!");
		}
		catch (Exception e) {
			logger.info("guideservice exist");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Service already exists with the same parameters.");
		}
	}

	@GetMapping
	public Page<GuideService> getAllGuideServicesByCurrentUser(
            @RequestParam(required = false) ServiceType serviceType,
            @RequestParam(required = false) City city,
            @RequestParam(required = false) Language language,
            @RequestParam(required = false) GroupSizeCategory groupSize,
            @RequestParam(required = false) PaymentOption paymentOption,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "6") int size, 
			@AuthenticationPrincipal CustomUserDetails userDetails) {

		logger.info("userDetails " + userDetails);
		Pageable pageable = PageRequest.of(page, size, Sort.by("type").ascending()
		        .and(Sort.by("city").ascending())
		        .and(Sort.by("language").ascending())
		        .and(Sort.by("groupSizeCategory").ascending()));

		return guideServiceService.findByGuideIdAndFilter(userDetails.getId(), serviceType, city, language, groupSize, paymentOption, pageable);
	}
	
	@GetMapping("/guides/{guideId}")
	public List<GuideService> getAllGuideServicesByUserId(@PathVariable Long guideId) {
		return guideServiceService.findByGuideId(guideId);
	}

	@GetMapping("/{id}")
	public GuideService getGuideServiceById(@PathVariable Long id) {
		return guideServiceService.getGuideServiceById(id)
				.orElseThrow(() -> new RuntimeException("GuideService not found"));
	}

	@PutMapping("/{id}")
	public ResponseEntity<String> updateGuideService(@PathVariable Long id, @RequestBody GuideService guideService) {
		try {
			guideServiceService.updateGuideService(id, guideService);
			return ResponseEntity.status(HttpStatus.OK).body("Service updated successfully!");

		} catch (Exception e) {
			logger.info("guideservice exist");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Service already exists with the same parameters!");
		}
		
	}

	@DeleteMapping("/{id}")
	public void deleteGuideService(@PathVariable Long id) {
		guideServiceService.deleteGuideService(id);
	}
}