package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.service.impl.PhotoServiceImpl;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class})
@Log
@AutoConfigureMockMvc
public class PhotoControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @MockBean
    private PhotoServiceImpl photoService;

    @Test
    @WithUserDetails("admin")
    void getPhoto_givenPhotoid_thenReturnPhoto_Test() throws Exception{
        Photo photo = new Photo();
        photo.setId(1L);

        Album album = new Album();
        album.setTitle("album 1");
        album.setId(1L);
        album.setPhoto(List.of(photo));

        PhotoResponse pr = new PhotoResponse(1L, "titulo", "url", "thumbnailUrl", 1L);

        when(photoService.getPhoto(1L)).thenReturn(pr);
        log.info(pr.toString());
        ResponseEntity response = new ResponseEntity< >(pr, HttpStatus.OK);
        MvcResult result1 = mockMvc.perform(get("/api/photos/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("titulo")))
                .andExpect(jsonPath("$.albumId", is(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(pr)))
                .andReturn();

    }


}
