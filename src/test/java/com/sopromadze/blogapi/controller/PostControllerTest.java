package com.sopromadze.blogapi.controller;

import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.payload.*;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.PostServiceImpl;
import com.sopromadze.blogapi.service.impl.UserServiceImpl;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class})
@AutoConfigureMockMvc
@Log
class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PostServiceImpl postService;

    @Test
    void givenACategory_thenReturnPosts() throws Exception{
        Category cat = Category.builder().id(1L).name("Cachorritos").build();
        Post post = Post.builder().id(1L).category(cat).title("Mi nueva mascota").body("Esta es mi nueva mascota").build();
        PagedResponse<Post> response = new PagedResponse<>();
        response.setContent(List.of(post));
        response.setSize(1);
        response.setPage(0);

        when(postService.getPostsByCategory(cat.getId(), response.getPage(), response.getSize())).thenReturn(response);

        mockMvc.perform(get("/api/posts/category/{id}", 1L)
                .contentType("application/json").content(objectMapper.writeValueAsString(post))
                        .content(String.valueOf(jsonPath("$.content[0].id", is(1))))
                        .content(String.valueOf(jsonPath("$.content", hasSize(1)))))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getAllPost_givenPostList_shouldReturnPagedResponse() throws Exception{

        Post post = Post.builder()
                .id(1L)
                .title("Un Test")
                .body("Esto es un post para tests")
                .build();

        PagedResponse<Post> pagedResult = new PagedResponse<>();
        pagedResult.setContent(List.of(post));
        pagedResult.setSize(1);
        pagedResult.setPage(10);

        when(postService.getAllPosts(0, 10)).thenReturn(pagedResult);

        mockMvc.perform(get("/api/posts/?page={page}&size={size}", 0, 10)
                        .contentType("application/json").content(objectMapper.writeValueAsString(post))
                        .content(String.valueOf(jsonPath("$.content[0].id", is(1))))
                        .content(String.valueOf(jsonPath("$.content", hasSize(1)))))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    @WithUserDetails("admin")
    void updatePost_givenPostId_thenReturnResponseEntity_test() throws Exception{
        UserPrincipal richard = new UserPrincipal(1L, "Richard", "Céspedes", "rick4", "richard@cespedes.com", "123456", List.of(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));

        Category c = new Category();
        c.setName("categoria 1");
        c.setId(1L);
        c.setUpdatedAt(Instant.now());
        c.setCreatedAt(Instant.now());

        PostRequest pr = new PostRequest();
        pr.setCategoryId(1L);
        pr.setTitle("titulo con mas de 10 caracteres");
        pr.setBody("descripcion de una categoria con id 1 y titulo con mas de 50 caracteres y nada mas que decir de la descripcion de la categoria");

        Post p = new Post();
        p.setId(1L);
        p.setCategory(c);
        p.setTitle("post");
        p.setBody("body");
        p.setCreatedAt(Instant.now());
        p.setUpdatedAt(Instant.now());

        when(postService.updatePost(1L, pr, richard)).thenReturn(p);

        mockMvc.perform(put("/api/posts/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(pr)))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    void updatePost_givenPostId_thenReturnUnauthorized_test() throws Exception{
        UserPrincipal richard = new UserPrincipal(1L, "Richard", "Céspedes", "rick4", "richard@cespedes.com", "123456", List.of(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));

        Category c = new Category();
        c.setName("categoria 1");
        c.setId(1L);
        c.setUpdatedAt(Instant.now());
        c.setCreatedAt(Instant.now());

        PostRequest pr = new PostRequest();
        pr.setCategoryId(1L);
        pr.setTitle("titulo con mas de 10 caracteres");
        pr.setBody("descripcion de una categoria con id 1 y titulo con mas de 50 caracteres y nada mas que decir de la descripcion de la categoria");

        Post p = new Post();
        p.setId(1L);
        p.setCategory(c);
        p.setTitle("post");
        p.setBody("body");
        p.setCreatedAt(Instant.now());
        p.setUpdatedAt(Instant.now());

        when(postService.updatePost(1L, pr, richard)).thenReturn(p);

        mockMvc.perform(put("/api/posts/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(pr)))
                .andExpect(status().isUnauthorized())
                .andReturn();

    }
}