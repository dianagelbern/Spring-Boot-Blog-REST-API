package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.BlogapiException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.CommentRequest;
import com.sopromadze.blogapi.repository.CommentRepository;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

    @Test
    void addComment_givenPostAndUser_shouldReturnComment(){

        CommentRequest cr = new CommentRequest();
        cr.setBody("Este es el cuerpo del PostRequest");

        Post post= new Post();
        post.setTitle("Post para agregar comentarios");

        UserPrincipal ernestoPrincipal = new UserPrincipal(1L,"Ernesto","Fatuarte", "efatuarte","efatuarte@gmail.com","123456789", List.of(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));

        User ernesto = new User("Ernesto","Fatuarte", "efatuarte","efatuarte@gmail.com","123456789");
        ernesto.setId(1L);

        Comment comment = new Comment(cr.getBody());
        comment.setUser(ernesto);
        comment.setPost(post);
        comment.setName("Primer Comentario");
        comment.setEmail("efatuarte@gmail.com");

        when(postRepository.findById(any())).thenReturn(Optional.of(post));
        when(userRepository.getUser(any())).thenReturn(ernesto);
        when(commentRepository.save(any())).thenReturn(comment);

        assertEquals(commentService.addComment(cr, any(), ernestoPrincipal).getName(), comment.getName());
        assertEquals(commentService.addComment(cr, any(), ernestoPrincipal).getEmail(), comment.getEmail());
        assertEquals(commentService.addComment(cr, any(), ernestoPrincipal).getUser().getUsername(), ernestoPrincipal.getUsername());
        assertEquals(commentService.addComment(cr, any(), ernestoPrincipal).getPost().getTitle(), post.getTitle());
    }

    @Test
    void addComment_givenPostAndNonExistingUser_shouldThrowResourceNotFoundException(){

        CommentRequest cr = new CommentRequest();
        cr.setBody("Este es el cuerpo del PostRequest");

        Post post= new Post();
        post.setTitle("Post para agregar comentarios");

        UserPrincipal ernestoPrincipal = new UserPrincipal(1L,"Ernesto","Fatuarte", "efatuarteP","efatuarte@gmail.com","123456789", List.of(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));

        User ernesto = new User("Ernesto","Fatuarte", "efatuarte","efatuarte@gmail.com","123456789");
        ernesto.setId(1L);

        Comment comment = new Comment(cr.getBody());
        comment.setUser(ernesto);
        comment.setPost(post);
        comment.setName("Primer Comentario");
        comment.setEmail("efatuarte@gmail.com");

        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,()->commentService.addComment(cr, any(), ernestoPrincipal));
    }

    @Test
    void getCommentById_givenPostIdAndCommentId_shouldReturnComment() {

        Post post = new Post();
        post.setId(1L);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setPost(post);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        assertEquals(commentService.getComment(1L, 1L), comment);

    }

    @Test
    void getCommentById_givenNonExistingPostIdOrCommentId_shouldThrowResourceNotFoundException() {

        Post post = new Post();
        post.setId(1L);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setPost(post);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        assertThrows(ResourceNotFoundException.class, () -> commentService.getComment(0L, 0L));

    }

}
