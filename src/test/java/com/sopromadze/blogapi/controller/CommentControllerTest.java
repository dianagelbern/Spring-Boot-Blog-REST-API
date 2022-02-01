package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.config.SecurityConfig;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.Comment;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.payload.*;
import com.sopromadze.blogapi.service.CommentService;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class})
@AutoConfigureMockMvc
@Log
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CommentService commentService;

    @Test
    void givenPostId_thenReturnAllComments() throws Exception {
        Category cat = Category.builder().id(1L).name("Cachorritos").build();
        Post post = Post.builder().id(1L).category(cat).title("Mi nueva mascota").body("Esta es mi nueva mascota").build();
        Comment comment = Comment.builder().id(1L).name("Esto es el nombre").body("Cuerpo de un comentario").post(post).build();
        PagedResponse<Comment> response = new PagedResponse<>();
        response.setContent(List.of(comment));
        response.setSize(1);
        response.setPage(0);

        Mockito.when(commentService.getAllComments(post.getId(), response.getPage(), response.getSize())).thenReturn(response);

        mockMvc.perform(get("/api/posts/{postId}/comments", 1L)
                        .contentType("application/json").content(objectMapper.writeValueAsString(response))
                        .content(String.valueOf(jsonPath("$.content[0].id", is(1))))
                        .content(String.valueOf(jsonPath("$.content", hasSize(1)))))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void givenPostId_thenReturnAComment() throws Exception {
        Category cat = Category.builder().id(1L).name("Cachorritos").build();
        Post post = Post.builder().id(1L).category(cat).title("Mi nueva mascota").body("Esta es mi nueva mascota").build();
        Comment comment = Comment.builder().id(1L).name("Esto es el nombre").body("Cuerpo de un comentario").post(post).build();


        Mockito.when(commentService.getComment(post.getId(), comment.getId())).thenReturn(comment);

        mockMvc.perform(get("/api/posts/{postId}/comments/{id}", 1L, 1L)
                .contentType("application/json").content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithMockUser(roles = "USER")
    void addComentSucces() throws Exception{
        Category cat = Category.builder().id(1L).name("Cachorritos").build();
        Post post = Post.builder().id(1L).category(cat).title("Mi nueva mascota").body("Esta es mi nueva mascota").build();
        User diana = User.builder()
                .id(1L)
                .firstName("Diana")
                .lastName("González")
                .username("Gelbern")
                .password("123456789")
                .email("diana@gmail.com")
                .build();
        diana.setCreatedAt(Instant.now());
        diana.setUpdatedAt(Instant.now());
        UserPrincipal usuario = UserPrincipal.builder().username(diana.getUsername()).authorities(Arrays.asList(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))).username(diana.getUsername()).password("1234567").build();
        Comment comment = Comment.builder().id(1L).name("Esto es el nombre").body("Cuerpo de un comentario").post(post).build();
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setBody(comment.getBody());

        Mockito.when(commentService.addComment(commentRequest, post.getId(), usuario)).thenReturn(comment);

        mockMvc.perform(post("/api/posts/{postId}/comments/", 1L)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isCreated());

    }

    @Test
    void addComentUnauthorized() throws Exception{
        Category cat = Category.builder().id(1L).name("Cachorritos").build();
        Post post = Post.builder().id(1L).category(cat).title("Mi nueva mascota").body("Esta es mi nueva mascota").build();
        User diana = User.builder()
                .id(1L)
                .firstName("Diana")
                .lastName("González")
                .username("Gelbern")
                .password("123456789")
                .email("diana@gmail.com")
                .build();
        diana.setCreatedAt(Instant.now());
        diana.setUpdatedAt(Instant.now());
        UserPrincipal usuario = UserPrincipal.builder().username(diana.getUsername()).username(diana.getUsername()).password("1234567").build();
        Comment comment = Comment.builder().id(1L).name("Esto es el nombre").body("Cuerpo de un comentario").post(post).build();
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setBody(comment.getBody());

        Mockito.when(commentService.addComment(commentRequest, post.getId(), usuario)).thenReturn(comment);

        mockMvc.perform(post("/api/posts/{postId}/comments/", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @WithMockUser(roles = "USER")
    void addComentError() throws Exception{
        Category cat = Category.builder().id(1L).name("Cachorritos").build();
        Post post = Post.builder().id(1L).category(cat).title("Mi nueva mascota").body("Esta es mi nueva mascota").build();
        User diana = User.builder()
                .id(1L)
                .firstName("Diana")
                .lastName("González")
                .username("Gelbern")
                .password("123456789")
                .email("diana@gmail.com")
                .build();
        diana.setCreatedAt(Instant.now());
        diana.setUpdatedAt(Instant.now());
        UserPrincipal usuario = UserPrincipal.builder().username(diana.getUsername()).authorities(Arrays.asList(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))).username(diana.getUsername()).password("1234567").build();
        Comment comment = Comment.builder().id(1L).name("Esto es el nombre").body("Cuerpo de un comentario").post(post).build();
        CommentRequest commentRequest = new CommentRequest();

        Mockito.when(commentService.addComment(commentRequest, post.getId(), usuario)).thenReturn(comment);

        mockMvc.perform(post("/api/posts/{postId}/comments/", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentRequest)))
                .andExpect(status().isBadRequest());
    }



    @Test
    void updateCommentUnauthorized()throws Exception{

        Category cat = Category.builder().id(1L).name("Cachorritos").build();
        Post post = Post.builder().id(1L).category(cat).title("Mi nueva mascota").body("Esta es mi nueva mascota").build();

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setBody("Nuevo cuerpo");
        User diana = User.builder()
                .id(1L)
                .firstName("Diana")
                .lastName("González")
                .username("Gelbern")
                .password("123456789")
                .email("diana@gmail.com")
                .build();
        diana.setCreatedAt(Instant.now());
        diana.setUpdatedAt(Instant.now());
        UserPrincipal usuario = UserPrincipal.builder().username(diana.getUsername()).authorities(Arrays.asList(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))).username(diana.getUsername()).password("1234567").build();

        Comment comment = Comment.builder().id(1L).name("Esto es el nombre").body("Cuerpo de un comentario").post(post).build();


        mockMvc.perform(put("/api/posts/{postId}/comments/{id}", 1L, 1L)
                        .contentType("application/json")
                        .param("commentRequest","commentRequest")
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void updateCommentSucces()throws Exception{

        Category cat = Category.builder().id(1L).name("Cachorritos").build();
        Post post = Post.builder().id(1L).category(cat).title("Mi nueva mascota").body("Esta es mi nueva mascota").build();

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.setBody("Nuevo cuerpo");
        User diana = User.builder()
                .id(1L)
                .firstName("Diana")
                .lastName("González")
                .username("Gelbern")
                .password("123456789")
                .email("diana@gmail.com")
                .build();
        diana.setCreatedAt(Instant.now());
        diana.setUpdatedAt(Instant.now());
        UserPrincipal usuario = UserPrincipal.builder().username(diana.getUsername()).authorities(Arrays.asList(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))).username(diana.getUsername()).password("1234567").build();

        Comment comment = Comment.builder().id(1L).name("Esto es el nombre").body("Cuerpo de un comentario").post(post).build();


        mockMvc.perform(put("/api/posts/{postId}/comments/{id}", 1L, 1L)
                        .with(SecurityMockMvcRequestPostProcessors.user("usuario").roles("USER"))
                        .contentType("application/json")
                        .param("commentRequest","commentRequest")
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk());
    }


    @Test
    void deleteCommentUnauthorized()throws Exception{
        Category cat = Category.builder().id(1L).name("Cachorritos").build();
        Post post = Post.builder().id(1L).category(cat).title("Mi nueva mascota").body("Esta es mi nueva mascota").build();

        User diana = User.builder()
                .id(1L)
                .firstName("Diana")
                .lastName("González")
                .username("Gelbern")
                .password("123456789")
                .email("diana@gmail.com")
                .build();
        diana.setCreatedAt(Instant.now());
        diana.setUpdatedAt(Instant.now());
        UserPrincipal usuario = UserPrincipal.builder().username(diana.getUsername()).authorities(Arrays.asList(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))).username(diana.getUsername()).password("1234567").build();
        Comment comment = Comment.builder().id(1L).name("Esto es el nombre").body("Cuerpo de un comentario").post(post).build();
        ApiResponse apiResponse = new ApiResponse(true, "Success", HttpStatus.OK);

        mockMvc.perform(delete("/api/posts/{postId}/comments/{id}", 1L, 1L)
                        .contentType("application/json")
                .content(objectMapper.writeValueAsString(apiResponse)))
                .andExpect(status().isUnauthorized());
    }


    @Test
    @WithMockUser(roles = "USER")
    void deleteCommentSucces()throws Exception{
        Category cat = Category.builder().id(1L).name("Cachorritos").build();
        Post post = Post.builder().id(1L).category(cat).title("Mi nueva mascota").body("Esta es mi nueva mascota").build();

        User diana = User.builder()
                .id(1L)
                .firstName("Diana")
                .lastName("González")
                .username("Gelbern")
                .password("123456789")
                .email("diana@gmail.com")
                .build();
        diana.setCreatedAt(Instant.now());
        diana.setUpdatedAt(Instant.now());
        UserPrincipal usuario = UserPrincipal.builder().username(diana.getUsername()).authorities(Arrays.asList(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))).username(diana.getUsername()).password("1234567").build();
        Comment comment = Comment.builder().id(1L).name("Esto es el nombre").body("Cuerpo de un comentario").post(post).build();
        ApiResponse apiResponse = new ApiResponse(true, "Success", HttpStatus.OK);
        Mockito.when(commentService.deleteComment(Mockito.anyLong(), Mockito.anyLong(), Mockito.any())).thenReturn(apiResponse);
        mockMvc.perform(delete("/api/posts/{postId}/comments/{id}", post.getId(), comment.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user(usuario.getUsername()).roles("USER"))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(apiResponse.getSuccess())))
                .andExpect(status().isOk());
    }

}