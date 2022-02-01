package com.sopromadze.blogapi.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class PhotoRepositoryTests {

    @Autowired
    private PhotoRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void repositoryNotNull_success(){
        assertNotNull(repository);
    }

    @Test
    void findByAlbumId_givenAlbumId_ShouldShowAlbum(){
        Album album = new Album();
        album.setTitle("Portadas de Discos de Michael Jackson");
        album.setCreatedAt(Instant.now());
        album.setUpdatedAt(Instant.now());


        Photo photo = new Photo();
        photo.setTitle("Thriller");
        photo.setUrl("https://www.michael_jackson.com/thriller");
        photo.setThumbnailUrl("https://vinylroute.com/wp-content/uploads/2021/03/MichaelJackson1-scaled.jpg");
        photo.setCreatedAt(Instant.now());
        photo.setUpdatedAt(Instant.now());
        photo.setAlbum(album);

        album.setPhoto(List.of(photo));

        entityManager.persist(album);
        entityManager.persist(photo);

        Pageable pageable = PageRequest.of(0,5);

        assertEquals(1,repository.findByAlbumId(1L,pageable).getTotalElements());
    }
}
