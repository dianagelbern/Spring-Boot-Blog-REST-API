package com.sopromadze.blogapi.repository;

import com.sopromadze.blogapi.model.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TagRepositoryTest {

    @Autowired
    TagRepository tagRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void whenFindByName_thenReturnTag() {


        Tag puppies = Tag.builder()
                .name("Cachorritos").build();
        puppies.setCreatedAt(Instant.now());
        puppies.setUpdatedAt(Instant.now());

        entityManager.persist(puppies);
        entityManager.flush();


        assertEquals(puppies, tagRepository.findByName(puppies.getName()));
    }
}