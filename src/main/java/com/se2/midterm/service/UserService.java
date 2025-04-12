package com.se2.midterm.service;

import com.se2.midterm.entity.Cart;
import com.se2.midterm.entity.Role;
import com.se2.midterm.entity.User;
import com.se2.midterm.repository.CartRepository;
import com.se2.midterm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String registerUser(User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return "Tên đăng nhập đã tồn tại!";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        if (user.getRole() == null) {
            user.setRole(Role.ADMIN);
        }

        // Lưu user trước để có ID
        userRepository.save(user);

        // Tạo và gán Cart
        Cart cart = new Cart(user);
        cart.setUser(user);
        cartRepository.save(cart);
        return "successful";
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
    }
    public void updateUserAvatar(String username, String avatarPath) {
        User user = findByUsername(username);
        user.setAvatarUrl(avatarPath);
        userRepository.save(user);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public boolean checkPassword(String username, String rawPassword) {
        User user = findByUsername(username);
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public void updatePassword(String username, String newPassword) {
        User user = findByUsername(username);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
