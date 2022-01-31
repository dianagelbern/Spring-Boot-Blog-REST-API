package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.TagServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class})
@AutoConfigureMockMvc
public class TagControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    TagServiceImpl tagService;

    @Test
    void addTag_givenUnauthorizedUser_shouldReturn401() throws Exception {

        Tag tag = new Tag();
        tag.setName("FailedTag");

        mockMvc.perform(post("/api/tags")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(tag)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithUserDetails("user")
    void addTag_givenUserAndTag_shouldReturn201() throws Exception {

        UserPrincipal ernestoPrincipal = new UserPrincipal(1L,"Ernesto","Fatuarte", "efatuarte","efatuarte@gmail.com","123456789", List.of(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));

        Tag tag = new Tag();
        tag.setName("FailedTag");

        when(tagService.addTag(tag, ernestoPrincipal)).thenReturn(tag);

        mockMvc.perform(post("/api/tags")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(tag)))
                .andExpect(status().isCreated())
                .andReturn();

    }
}
