package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.PhotoServiceImpl;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.util.List;

import static com.sopromadze.blogapi.utils.AppConstants.CREATED_AT;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

        MvcResult result1 = mockMvc.perform(get("/api/photos/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("titulo")))
                .andExpect(jsonPath("$.albumId", is(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(pr)))
                .andReturn();

    }

    @Test
    void getAllPhotos_thenReturnPagedResponse_test () throws Exception {
        Photo photo = new Photo();
        photo.setId(1L);
        photo.setCreatedAt(Instant.now());
        photo.setUpdatedAt(Instant.now());

        Album album = new Album();
        album.setTitle("album 1");
        album.setId(2L);
        album.setPhoto(List.of(photo));
        album.setCreatedAt(Instant.now());
        album.setUpdatedAt(Instant.now());

        PhotoResponse pr = new PhotoResponse(1L, "titulo", "url", "thumbnailUrl", 1L);

        PagedResponse<PhotoResponse> paged = new PagedResponse<>();
        paged.setContent(List.of(pr));
        paged.setSize(1);
        paged.setTotalPages(1);
        paged.setTotalElements(1);


        when(photoService.getAllPhotos(0,10)).thenReturn(paged);

        mockMvc.perform(get("/api/photos?page={page}&size={size}", 0, 10)
                        .contentType("application/json").content(objectMapper.writeValueAsString(paged))
                        .content(String.valueOf(jsonPath("$.content[0].id", is(1))))
                        .content(String.valueOf(jsonPath("$.content", hasSize(1)))))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    @WithUserDetails("admin")
    void deletePhoto_givenPhotoId_returnNoContent_test() throws Exception {

        UserPrincipal user1 = new UserPrincipal(1L, "Nombre1", "Apellido1", "admin", "admin@admin.com", "admin", List.of(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setStatus(HttpStatus.NO_CONTENT);
        apiResponse.setMessage("se ha eliminado la foto");
        apiResponse.setSuccess(true);

        when(photoService.deletePhoto(1L, user1)).thenReturn(apiResponse);

        mockMvc.perform(delete("/api/photos/{id}", 1)
                        .contentType("application/json").content(objectMapper.writeValueAsString(apiResponse)))
                .andExpect(status().isNoContent())
                .andReturn();

    }

    @Test
    void deletePhoto_givenPhotoId_returnUnauthorized_test() throws Exception {

        UserPrincipal user1 = new UserPrincipal(1L, "Nombre1", "Apellido1", "admin", "admin@admin.com", "admin", List.of(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));
        ApiResponse apiResponse = new ApiResponse();

        when(photoService.deletePhoto(1L, user1)).thenReturn(apiResponse);

        mockMvc.perform(delete("/api/photos/{id}", 1)
                        .contentType("application/json").content(objectMapper.writeValueAsString(apiResponse)))
                .andExpect(status().isUnauthorized())
                .andReturn();

    }


}
