package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.service.PostService;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.modelmapper.internal.bytebuddy.matcher.ElementMatchers.is;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Log
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT, classes =
        {SpringSecurityTestWebConfig.class})
@AutoConfigureMockMvc
public class UserControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PostService postService;

    @Test
    void getPostsCreatedBy_givenUsername_ShouldShowPostList() throws Exception{

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
        post.setTitle("Controlador de post");
        post.setBody("Esto es un post de prueba para testear un método");
        post.setCreatedBy(user.getId());
        post.setUpdatedAt(Instant.now());

        PagedResponse<Post> postList = new PagedResponse<Post>();
        postList.setContent(List.of(post));
        postList.setPage(0);
        postList.setSize(10);

        when(postService.getPostsByCreatedBy(user.getUsername(), 0, 10)).thenReturn(postList);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(1L)))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(postList)));

        log.info(result.getResponse().getContentAsString());

    }
}
