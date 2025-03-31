package com.se2.midterm.controller;

import com.se2.midterm.entity.User;
import com.se2.midterm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

//@AllArgsConstructor
@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "Login successful for user: " + username;
    }

    @GetMapping("/register")
    public String getRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {
        String result = userService.registerUser(user);
        System.out.println(result);
        if (result.equals("successful")) {
            return "registerSuccessfully"; // Chuyển hướng đến trang login
        } else {
            model.addAttribute("errorMessage", result);
            return "register"; // Hiển thị lỗi nếu username trùng
        }
    }
    @GetMapping("/profile")
    public String showAccountPage(Model model) {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Fetch the current user's details
        User currentUser = userService.findByUsername(username);

        // Add user to the model to populate account details
        model.addAttribute("user", currentUser);

        return "profile"; // This will return the profile.html template
    }
    @PostMapping("/upload-avatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file, Model model) {
        try {
            // Lấy username hiện tại
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            // Xử lý upload file
            String uploadDir = "uploads/avatars/";
            String fileName = username + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);

            // Tạo thư mục nếu chưa tồn tại
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Lưu file
            try (InputStream inputStream = file.getInputStream()) {
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }

            // Cập nhật avatar trong database
            String avatarPath = "/uploads/avatars/" + fileName;
            userService.updateUserAvatar(username, avatarPath);

            return "redirect:/profile";
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Lỗi upload ảnh");
            return "profile";
        }
    }

    @GetMapping("/myAccountOrder")
    public String showMyAccountOrder(Model model) {
        // Lấy thông tin người dùng hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Lấy thông tin người dùng
        User currentUser = userService.findByUsername(username);

        // Thêm thông tin người dùng vào model
        model.addAttribute("user", currentUser);

        // model.addAttribute("orders", orderService.findOrdersByUser(currentUser));

        return "myAccountOrder";
    }
}
