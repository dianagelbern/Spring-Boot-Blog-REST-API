package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.user.User;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

@Log
@DataJpaTest
@ActiveProfiles("test")
public class PostRepositoryTests {

    @Autowired
    private PostRepository repository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void findByCreatedBy_givenExistUserId_ShouldSuccess() {

        User user = new User();
        user.setUsername("user");
        user.setFirstName("Richard");
        user.setLastName("Cespedes");
        user.setEmail("richard@richard.com");
        user.setPassword("1234");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        Post p1 = new Post();
        p1.setTitle("post1");
        p1.setUser(user);
        p1.setCreatedAt(Instant.now());
        p1.setUpdatedAt(Instant.now());
        p1.setCreatedBy(1L);

        testEntityManager.persist(user);
        testEntityManager.persist(p1);
        testEntityManager.flush();

        Page<Post> posts=repository.findByCreatedBy(user.getId(), PageRequest.of(0, 10));

        assertEquals(1, posts.getTotalElements());
        assertEquals(p1.getId(),posts.getContent().get(0).getUser().getId());
    }
}
