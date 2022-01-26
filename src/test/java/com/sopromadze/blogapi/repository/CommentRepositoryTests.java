package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class CommentRepositoryTests {

    @Autowired
    private CommentRepository repository;

    @Autowired
    private TestEntityManager testEntityManager;



    @Test
    void findByPostId_givenPostId_ShouldShowSuccess () {
        Comment c1 = new Comment();
        c1.setName("comment1");
        c1.setEmail("email@email.com");
        c1.setBody("hola a todos");

        Post p1 = new Post();
        c1.setPost(p1);
        c1.setCreatedAt(Instant.now());
        c1.setCreatedBy(p1.getId());
        c1.setUpdatedAt(Instant.now());

        p1.setCreatedAt(Instant.now());
        p1.setUpdatedAt(Instant.now());

        testEntityManager.persist(c1);
        testEntityManager.persist(p1);
        testEntityManager.flush();

        Page<Comment> result = repository.findByPostId(p1.getId(), PageRequest.of(0,1));

        assertEquals(1, result.getTotalElements());
        assertEquals(c1,result.getContent().get(0));
    }

}
