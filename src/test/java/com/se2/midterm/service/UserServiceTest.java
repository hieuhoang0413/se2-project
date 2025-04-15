package com.se2.midterm.service;

import com.se2.midterm.entity.Cart;
import com.se2.midterm.entity.Role;
import com.se2.midterm.entity.User;
import com.se2.midterm.repository.CartRepository;
import com.se2.midterm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Spy
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private User testUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("plainpassword");
        testUser.setRole(Role.USER);
    }

    @Test
    public void testRegisterUser_Successful() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        String result = userService.registerUser(testUser);

        assertEquals("successful", result);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    public void testRegisterUser_UsernameExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        String result = userService.registerUser(testUser);

        assertEquals("Tên đăng nhập đã tồn tại!", result);
        verify(cartRepository, never()).save(any());
    }

    @Test
    public void testLoadUserByUsername_Successful() {
        testUser.setPassword(passwordEncoder.encode("plainpassword"));

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertEquals(testUser.getPassword(), userDetails.getPassword());
    }

    @Test
    public void testLoadUserByUsername_NotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("unknown");
        });
    }

    @Test
    public void testFindByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        User result = userService.findByUsername("testuser");

        assertEquals(testUser, result);
    }

    @Test
    public void testFindByUsername_NotFound() {
        when(userRepository.findByUsername("notfound")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.findByUsername("notfound");
        });
    }

    @Test
    public void testUpdateUserAvatar() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        String newAvatar = "/images/avatar.png";

        userService.updateUserAvatar("testuser", newAvatar);

        assertEquals(newAvatar, testUser.getAvatarUrl());
        verify(userRepository).save(testUser);
    }

    @Test
    public void testUpdateUser() {
        userService.updateUser(testUser);
        verify(userRepository).save(testUser);
    }

    @Test
    public void testCheckPassword_CorrectPassword() {
        String rawPassword = "plainpassword";
        testUser.setPassword(passwordEncoder.encode(rawPassword));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        boolean match = userService.checkPassword("testuser", rawPassword);
        assertTrue(match);
    }

    @Test
    public void testCheckPassword_WrongPassword() {
        testUser.setPassword(passwordEncoder.encode("correctpass"));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        boolean match = userService.checkPassword("testuser", "wrongpass");
        assertFalse(match);
    }

    @Test
    public void testUpdatePassword() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        String newRawPassword = "newsecurepass";

        userService.updatePassword("testuser", newRawPassword);

        assertTrue(passwordEncoder.matches(newRawPassword, testUser.getPassword()));
        verify(userRepository).save(testUser);
    }
}
