package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.BlogapiException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.repository.CommentRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CommentServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    CommentServiceImpl commentService;

    @Test
    void getAlbumById_givenNotExistingPostId_shouldThrowResourceNotFoundException() {

        when(postRepository.findById(1L)).thenThrow(new ResourceNotFoundException("a", "b", "c"));

        assertThrows(ResourceNotFoundException.class, () -> commentService.getComment(1L, 1L));
    }

    @Test
    void getAlbumById_givenNotExistingCommentId_shouldThrowResourceNotFoundException() {

        when(commentRepository.findById(1L)).thenThrow(new ResourceNotFoundException("a", "b", "c"));

        assertThrows(ResourceNotFoundException.class, () -> commentService.getComment(1L, 1L));
    }

    @Test
    void getAlbumId_givenPostAndComment_shouldReturnComment() {

        User user = new User();
        user.setUsername("efatuarte");
        user.setFirstName("Ernesto");
        user.setLastName("Fatuarte");
        user.setEmail("efatuarte@gmail.com");
        user.setPassword("12345");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        Post p1 = new Post();
        p1.setId(1L);
        p1.setTitle("Post para método del servicio");
        p1.setUser(user);
        p1.setCreatedAt(Instant.now());
        p1.setUpdatedAt(Instant.now());
        p1.setCreatedBy(1L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(p1));

        Comment c1 = new Comment();
        c1.setId(1L);
        c1.setName("Primer comentario");
        c1.setEmail("efatuarte@gmail.com");
        c1.setBody("Esto es un comentario de prueba");
        c1.setPost(p1);
        c1.setCreatedAt(Instant.now());
        c1.setUpdatedAt(Instant.now());

        when(commentRepository.findById(1L)).thenReturn(Optional.of(c1));

        assertEquals(c1, commentService.getComment(1L, 1L));
    }

    @Test
    void getAlbumId_givenPostAndComment_shouldReturnBlogapiException() {

        User user = new User();
        user.setUsername("efatuarte");
        user.setFirstName("Ernesto");
        user.setLastName("Fatuarte");
        user.setEmail("efatuarte@gmail.com");
        user.setPassword("12345");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        Post p1 = new Post();
        p1.setId(1L);
        p1.setTitle("Post para método del servicio");
        p1.setUser(user);
        p1.setCreatedAt(Instant.now());
        p1.setUpdatedAt(Instant.now());
        p1.setCreatedBy(1L);

        Post p2 = new Post();
        p1.setId(2L);
        p1.setTitle("Post para hacer fallar el método del servicio");
        p1.setUser(user);
        p1.setCreatedAt(Instant.now());
        p1.setUpdatedAt(Instant.now());
        p1.setCreatedBy(1L);

        when(postRepository.findById(1L)).thenReturn(Optional.of(p1));
        when(postRepository.findById(2L)).thenReturn(Optional.of(p2));

        Comment c1 = new Comment();
        c1.setId(1L);
        c1.setName("Primer comentario");
        c1.setEmail("efatuarte@gmail.com");
        c1.setBody("Esto es un comentario de prueba");
        c1.setPost(p1);
        c1.setCreatedAt(Instant.now());
        c1.setUpdatedAt(Instant.now());

        when(commentRepository.findById(1L)).thenReturn(Optional.of(c1));

        assertThrows(BlogapiException.class, () -> commentService.getComment(2L, 1L));
    }
}
