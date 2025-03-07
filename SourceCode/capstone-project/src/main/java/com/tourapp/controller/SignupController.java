package com.tourapp.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import com.tourapp.entity.AppUser;
import com.tourapp.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth/signup")
public class SignupController {

    private static final Logger logger = LoggerFactory.getLogger(SignupController.class);

    
    @Autowired
    private UserService accountService;

    @PostMapping("/tourist")
    @ResponseStatus(HttpStatus.CREATED)
    public AppUser registerTourist(@Valid @RequestBody AppUser user)  {
    	logger.info("New tourist registered: {}", user.getEmail());
        return accountService.registerTourist(user);
    }


    @PostMapping("/tour-guide")
    @ResponseStatus(HttpStatus.CREATED)
    public AppUser registerTourGuide(@Valid @RequestBody AppUser user)  {
    	logger.info("New tour guide registered: {}", user.getEmail());
        return accountService.registerTourGuide(user);
    }
}
