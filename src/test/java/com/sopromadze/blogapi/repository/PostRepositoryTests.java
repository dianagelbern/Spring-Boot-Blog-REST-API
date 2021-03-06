package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.user.User;
import lombok.extern.java.Log;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Log
@DataJpaTest
@ActiveProfiles("test")
public class PostRepositoryTests {

    @Autowired
    private PostRepository repository;

    @Autowired
    private TestEntityManager testEntityManager;

    private User user;
    private Post p1;
    private Pageable pageable;
    private Tag t1;
    @BeforeEach
    void datos () {
        user = new User();
        user.setUsername("user");
        user.setFirstName("Richard");
        user.setLastName("Cespedes");
        user.setEmail("richard@richard.com");
        user.setPassword("1234");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        testEntityManager.persist(user);

        p1 = new Post();
        p1.setTitle("post1");
        p1.setUser(user);
        p1.setCreatedAt(Instant.now());
        p1.setUpdatedAt(Instant.now());
        p1.setCreatedBy(user.getId());

        t1 = new Tag();
        t1.setPosts(List.of(p1));
        t1.setCreatedBy(1L);
        t1.setName("tag1");
        t1.setPosts(List.of(p1));
        t1.setCreatedAt(Instant.now());
        t1.setUpdatedAt(Instant.now());

        pageable = PageRequest.of(0, 10);

        testEntityManager.persist(p1);
        testEntityManager.persist(t1);
        testEntityManager.flush();

    }

    @Test
    void findByCreatedBy_givenExistUserId_ShouldSuccess() {
        Page<Post> posts=repository.findByCreatedBy(user.getId(), pageable);
        assertEquals(1, posts.getTotalElements());
        assertEquals(p1,posts.getContent().get(0));
    }

    @Test
    void findByTagsIn_Test() {
        Page<Post> posts=repository.findByTagsIn(List.of(t1), pageable);
        assertEquals(p1, posts.getContent().get(0));
    }


    @Test
    void countByCreatedBy_giveUserId_thenReturnCountPost() {
        assertEquals(1, repository.countByCreatedBy(user.getId()));
    }
}