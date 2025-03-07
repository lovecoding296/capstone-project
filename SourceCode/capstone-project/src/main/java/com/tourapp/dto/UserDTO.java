package com.tourapp.dto;

import lombok.*;
import java.util.List;
import java.util.stream.Collectors;

import com.tourapp.entity.AppUser;
import com.tourapp.entity.Role;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String facebook;
    private String tiktok;
    private String instagram;
    private String profilePicture;
    private String city;
    private Role role;

    // Thuộc tính dành riêng cho hướng dẫn viên du lịch
    private String bio;
    private List<String> languages;
    private List<String> cities;
    private double rating;
    private String guideLicense;
    private String experience;

    // Thuộc tính dành riêng cho khách du lịch
    private String preferences;

    // Constructor chuyển đổi từ AppUser
    public UserDTO(AppUser user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.facebook = user.getFacebook();
        this.tiktok = user.getTiktok();
        this.instagram = user.getInstagram();
        this.profilePicture = user.getProfilePicture();
        this.city = user.getCity();
        this.role = user.getRole();
        this.bio = user.getBio();
        this.languages = user.getLanguages();
        this.cities = user.getCities();
        this.rating = user.getRating();
        this.guideLicense = user.getGuideLicense();
        this.experience = user.getExperience();
        this.preferences = user.getPreferences();
    }

    // Phương thức chuyển đổi từ UserDTO về AppUser
    public AppUser toEntity() {
        AppUser user = new AppUser();
        user.setId(this.id);
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPhone(this.phone);
        user.setFacebook(this.facebook);
        user.setTiktok(this.tiktok);
        user.setInstagram(this.instagram);
        user.setProfilePicture(this.profilePicture);
        user.setCity(this.city);
        user.setRole(this.role);
        user.setBio(this.bio);
        user.setLanguages(this.languages);
        user.setCities(this.cities);
        user.setRating(this.rating);
        user.setGuideLicense(this.guideLicense);
        user.setExperience(this.experience);
        user.setPreferences(this.preferences);
        return user;
    }

    // Phương thức cập nhật thông tin User từ DTO
    public void updateEntity(AppUser user) {
        user.setName(this.name);
        user.setPhone(this.phone);
        user.setFacebook(this.facebook);
        user.setTiktok(this.tiktok);
        user.setInstagram(this.instagram);
        user.setProfilePicture(this.profilePicture);
        user.setCity(this.city);
        user.setBio(this.bio);
        user.setLanguages(this.languages);
        user.setCities(this.cities);
        user.setRating(this.rating);
        user.setGuideLicense(this.guideLicense);
        user.setExperience(this.experience);
        user.setPreferences(this.preferences);
    }

    // Phương thức chuyển đổi danh sách AppUser thành danh sách UserDTO
    public static List<UserDTO> fromEntityList(List<AppUser> users) {
        return users.stream().map(UserDTO::new).collect(Collectors.toList());
    }
}
