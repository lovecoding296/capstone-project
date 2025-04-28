package funix.tgcp.home;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import funix.tgcp.guide.service.GroupSizeCategory;
import funix.tgcp.guide.service.GuideService;
import funix.tgcp.guide.service.ServiceType;
import funix.tgcp.post.Post;
import funix.tgcp.post.PostCategory;
import funix.tgcp.post.PostService;
import funix.tgcp.user.City;
import funix.tgcp.user.Language;
import funix.tgcp.user.User;
import funix.tgcp.user.UserService;
import funix.tgcp.util.LogHelper;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

	private static final LogHelper logger = new LogHelper(HomeController.class);

	@Autowired
	private UserService userService;

	@Autowired
	private PostService postService;

	@GetMapping("/")
	public String home(Model model) {

		List<Post> posts = postService.findTop4ByOrderByCreatedAtDesc();
		List<User> users = userService.findTop6UsersWithGuideServicesAndRoleGuide();
		
		
		logger.info("home users.size(); " + users.size());
		
		for (User u : users) {
			Set<City> cities = new HashSet<>();
			Set<ServiceType> types = new HashSet<>();
			Set<Language> languages = new HashSet<>();
			Set<GroupSizeCategory> groupSizes = new HashSet<>();

			for (GuideService g : u.getGuideServices()) {
				cities.add(g.getCity());
				types.add(g.getType());
				languages.add(g.getLanguage());
				groupSizes.add(g.getGroupSizeCategory());
			}
			u.setCities(cities);
			u.setServiceTypes(types);
			u.setLanguages(languages);
			u.setGroupSizes(groupSizes);
		}
		

		model.addAttribute("postCategories", PostCategory.values());
		model.addAttribute("posts", posts);
		model.addAttribute("users", users);		

		return "home";
	}

	@GetMapping("/dashboard")
	public String dashboard() {
		return "/dashboard/dashboard";
	}

	@GetMapping("/admin/dashboard")
	public String adminDashboard() {
		return "/dashboard/admin-dashboard";
	}







}
