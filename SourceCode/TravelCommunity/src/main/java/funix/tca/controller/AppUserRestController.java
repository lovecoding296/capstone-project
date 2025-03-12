package funix.tca.controller;

import funix.tca.entity.AppUser;
import funix.tca.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class AppUserRestController {

    @Autowired
    private AppUserService appUserService;

    // Tìm người dùng theo email
    @GetMapping("/by-email")
    public ResponseEntity<AppUser> findUserByEmail(@RequestParam String email) {
        Optional<AppUser> user = appUserService.findByEmail(email);
        return user.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }
}

