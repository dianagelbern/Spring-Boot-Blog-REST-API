package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.service.impl.AlbumServiceImpl;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.time.Instant;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class})
@Log
@AutoConfigureMockMvc
public class AlbumControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @MockBean
    private AlbumServiceImpl albumService;

    @Test
    @WithUserDetails("admin")
    void getAllAlbums_ShouldShowSuccessTest() throws Exception {


        AlbumResponse a = new AlbumResponse();
        a.setId(1L);
        a.setTitle("album 1");
        a.setCreatedAt(Instant.now());
        a.setUpdatedAt(Instant.now());
        PagedResponse<AlbumResponse> result = new PagedResponse<AlbumResponse>();
        result.setContent(List.of(a));

        when(albumService.getAllAlbums(0,10)).thenReturn(result);

        MvcResult result1 = mockMvc.perform(get("/api/albums/?page={page}&size={size}", 0, 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(result)))
                .andReturn();
    }
}
