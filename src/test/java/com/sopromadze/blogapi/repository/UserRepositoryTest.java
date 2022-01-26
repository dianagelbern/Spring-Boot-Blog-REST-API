package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;


    @Test
    void whenFindByUsername_thenReturnUser() {
        User diana = User.builder()
                .firstName("Diana")
                .lastName("González")
                .username("Gelbern")
                .password("123456789")
                .email("diana@gmail.com")
                .build();
        diana.setCreatedAt(Instant.now());
        diana.setUpdatedAt(Instant.now());

        entityManager.persist(diana);
        entityManager.flush();

        assertEquals(Optional.of(diana), userRepository.findByUsername(diana.getUsername()));

    }


    @Test
    void whenExistByUsername_thenReturnTrue() {
        User diana = User.builder()
                .firstName("Diana")
                .lastName("González")
                .username("Gelbern")
                .password("123456789")
                .email("diana@gmail.com")
                .build();
        diana.setCreatedAt(Instant.now());
        diana.setUpdatedAt(Instant.now());

        entityManager.persist(diana);
        entityManager.flush();

        assertTrue(userRepository.existsByUsername(diana.getUsername()));

    }

    @Test
    void findByUsernameOrEmail_givenExistUsernameOrEmail_ShouldSuccess() {

        User user1 = User.builder()
                .firstName("Ernesto")
                .lastName("Fatuarte")
                .username("efatuarte")
                .password("123456789")
                .email("efatuarte@gmail.com")
                .build();
        user1.setCreatedAt(Instant.now());
        user1.setUpdatedAt(Instant.now());

        entityManager.persist(user1);
        entityManager.flush();

        assertEquals(Optional.of(user1), userRepository.findByUsernameOrEmail(user1.getUsername(), user1.getEmail()));

    }

    @Test
    void getUserByName_givenName_ShouldReturnUser() {
        User user1 = User.builder()
                .firstName("Ernesto")
                .lastName("Fatuarte")
                .username("efatuarte")
                .password("123456789")
                .email("efatuarte@gmail.com")
                .build();
        user1.setCreatedAt(Instant.now());
        user1.setUpdatedAt(Instant.now());

        entityManager.persist(user1);
        entityManager.flush();

        assertEquals(user1, userRepository.getUserByName("efatuarte"));
    }
}