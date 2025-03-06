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

import com.tourapp.entity.Tourist;
import com.tourapp.service.TouristService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/signup")
public class TouristController {

    @Autowired
    private TouristService touristService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Tourist registerTourist(@Valid @RequestBody Tourist tourist)  {
    	System.out.println(tourist.getPassword());
        return touristService.registerTourist(tourist);
    }


}
