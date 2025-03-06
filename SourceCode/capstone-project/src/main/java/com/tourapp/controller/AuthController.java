package com.tourapp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;

import com.tourapp.entity.Account;
import com.tourapp.entity.Tourist;
import com.tourapp.service.AccountService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/signup")
public class AuthController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/tourist")
    @ResponseStatus(HttpStatus.CREATED)
    public Account registerTourist(@Valid @RequestBody Account account)  {
        return accountService.registerTourist(account);
    }


    @PostMapping("/tour-guide")
    @ResponseStatus(HttpStatus.CREATED)
    public Account registerTourGuide(@Valid @RequestBody Account account)  {
        return accountService.registerTourGuide(account);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate(); // Hủy session
        return ResponseEntity.ok("Đăng xuất thành công!");
    }
}
