package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.UserIdentityAvailability;
import com.sopromadze.blogapi.repository.UserRepository;

import com.sopromadze.blogapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;


import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserServiceImpl userService;



    @Test
    void givenEmailExistent_thenReturnFalse() {
        User otro = User.builder()
                .email("diana@gmail.com")
                .build();

        when(!userRepository.existsByEmail(otro.getEmail())).thenReturn(false);

        assertEquals(new UserIdentityAvailability(true), userService.checkEmailAvailability("diana@gmail.com"));
    }



    @Test
    void getUserProfile() {
    }
}