package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.user.Address;
import com.sopromadze.blogapi.model.user.Company;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.UserIdentityAvailability;
import com.sopromadze.blogapi.payload.UserProfile;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.UserRepository;

import com.sopromadze.blogapi.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;


import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PostRepository postRepository;

    @InjectMocks
    UserServiceImpl userService;

    @MockBean
    TestEntityManager entityManager;

    @Test
    void givenEmailExistent_thenReturnFalse() {
        User diana = User.builder()
                .email("diana@gmail.com")
                .build();

        when(!userRepository.existsByEmail(diana.getEmail())).thenReturn(false);

        assertEquals(new UserIdentityAvailability(true), userService.checkEmailAvailability("diana@gmail.com"));
    }


    @Test
    void givenUsername_thenReturnUserProfile() {

        Post p = new Post();
        p.setTitle("Título");
        List<Post> listaDePost = List.of(p);
        Company c = Company.builder().build();
        Address a = Address.builder().build();

        User diana = User.builder()
                .id(1L)
                .firstName("Diana")
                .lastName("González")
                .username("Gelbern")
                .password("123456789")
                .phone("293040586")
                .website("www.diana.com")
                .company(c)
                .address(a)
                .posts(listaDePost)
                .email("diana@gmail.com")
                .build();
        userRepository.save(diana);
        diana.setCreatedAt(Instant.now());
        diana.setUpdatedAt(Instant.now());

        UserProfile up = new UserProfile(diana.getId(), diana.getUsername(), diana.getFirstName(), diana.getLastName(),
                diana.getCreatedAt(), diana.getEmail(), diana.getAddress(), diana.getPhone(), diana.getWebsite(),
                diana.getCompany(), postRepository.countByCreatedBy(diana.getId()));

        when(userRepository.getUserByName(diana.getUsername())).thenReturn(diana);
        when(postRepository.countByCreatedBy(any(Long.class))).thenReturn((long) diana.getPosts().size());
        assertEquals(up.getUsername(), userService.getUserProfile(diana.getUsername()).getUsername());

    }

}