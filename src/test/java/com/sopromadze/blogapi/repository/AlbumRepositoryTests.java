package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import static org.junit.jupiter.api.Assertions.*;
import java.time.Instant;

@DataJpaTest
@ActiveProfiles("test")
public class AlbumRepositoryTests {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private AlbumRepository albumRepository;

    @Test
    void findAlbumByCreatedBy_givenExistUserId_ShouldSuccess() {

        User u1 = new User();
        u1.setUsername("ricesp");
        u1.setFirstName("Richard");
        u1.setLastName("Cespedes Pedrazas");
        u1.setEmail("cespedes.peric@triana.edu");
        u1.setPassword("12345");
        u1.setCreatedAt(Instant.now());
        u1.setUpdatedAt(Instant.now());


        Album a1 = new Album();
        a1.setUser(u1);
        a1.setTitle("album 1");
        a1.setCreatedAt(Instant.now());
        a1.setUpdatedAt(Instant.now());
        a1.setCreatedBy(1L);

        testEntityManager.persist(u1);
        testEntityManager.persist(a1);
        testEntityManager.flush();

        Page<Album> result = albumRepository.findByCreatedBy(u1.getId(), PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(a1.getUser().getId(), result.getContent().get(0).getUser().getId());
        assertEquals(a1, result.getContent().get(0));

    }
}
