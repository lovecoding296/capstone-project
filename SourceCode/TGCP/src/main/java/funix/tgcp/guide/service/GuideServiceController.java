package funix.tgcp.guide.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import funix.tgcp.booking.payment.PaymentOption;
import funix.tgcp.user.City;
import funix.tgcp.user.Language;

@Controller
public class GuideServiceController {
	
	private static final Logger logger = LoggerFactory.getLogger(GuideServiceController.class);

	@Autowired
	private GuideServiceService guideServiceService;
	
	@GetMapping("/guide-services")
    public String searchGuideServices(
            @RequestParam(required = false) ServiceType serviceType,
            @RequestParam(required = false) City city,
            @RequestParam(required = false) Language language,
            @RequestParam(required = false) GroupSizeCategory groupSize,
            @RequestParam(required = false) PaymentOption paymentOption,    
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Integer minRating,          
            @RequestParam(required = false) String guideName,  
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

    	Pageable pageable = PageRequest.of(page, size); // Tạo Pageable    	
    	Page<GuideService> servicePage = guideServiceService.searchServices(serviceType, city, language, groupSize, paymentOption, maxPrice, minRating, guideName, pageable);

		
		
        model.addAttribute("services", servicePage.getContent()); // Lấy nội dung trang
        model.addAttribute("totalPages", servicePage.getTotalPages()); // Số trang
        model.addAttribute("currentPage", page); // Trang hiện tại
        
        model.addAttribute("cities", City.values());
        model.addAttribute("languages", Language.values());
        model.addAttribute("serviceTypes", ServiceType.values());
        model.addAttribute("groupSizes", GroupSizeCategory.values());
        model.addAttribute("paymentOptions", PaymentOption.values());
        
        return "guide-service/guide-service-list"; // Tên view Thymeleaf trả về, ví dụ: templates/guides/list.html
    }

}
