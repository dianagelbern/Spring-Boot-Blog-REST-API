package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.UserIdentityAvailability;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.PostServiceImpl;
import com.sopromadze.blogapi.service.impl.UserServiceImpl;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class})
@AutoConfigureMockMvc
@Log
public class UserControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PostServiceImpl postService;

    @MockBean
    UserServiceImpl userService;

    @Test
    void getPostsCreatedBy_givenUsername_ShouldShowPostList() throws Exception {

        User user = User.builder()
                .id(1L)
                .firstName("Ernesto")
                .lastName("Fatuarte")
                .username("efatuarte")
                .password("123456789")
                .email("ernesto.fatuarte@gmail.com")
                .phone("666666666")
                .build();

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Controlador de post");
        post.setBody("Esto es un post de prueba para testear un método");
        post.setCreatedBy(1L);
        post.setUpdatedAt(Instant.now());

        PagedResponse<Post> postList = new PagedResponse<>();
        postList.setContent(List.of(post));
        postList.setPage(0);
        postList.setSize(10);

        when(postService.getPostsByCreatedBy(user.getUsername(), 0, 10)).thenReturn(postList);

        MvcResult result = mockMvc.perform(get("/api/users/{username}/posts?page={page}&size={size}", "efatuarte", 0, 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(postList)))
                .andReturn();

    }


    @Test
    void givenUsername_thenCheckAvaiability() throws Exception {

        UserPrincipal userPrincipal = new UserPrincipal(1L, "Diana", "González", "Gelbern", "diana@gmail.com", "123456789", List.of(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())));

        UserIdentityAvailability userIdentityAvailability = new UserIdentityAvailability(true);
        when(userService.checkUsernameAvailability(userPrincipal.getUsername())).thenReturn(userIdentityAvailability);

        mockMvc.perform(get("/api/users/checkUsernameAvailability")
                .contentType("application/json")
                .param("username", userPrincipal.getUsername())
                .content(objectMapper.writeValueAsString(userIdentityAvailability)))
                .andExpect(status().isOk());
    }

}
