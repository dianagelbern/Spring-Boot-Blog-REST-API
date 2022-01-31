package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.payload.request.AlbumRequest;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.AlbumServiceImpl;
import com.sopromadze.blogapi.service.impl.PhotoServiceImpl;
import lombok.extern.java.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.time.Instant;
import java.util.List;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @MockBean
    private PhotoServiceImpl photoService;

    UserPrincipal user2;
    AlbumRequest al;
    AlbumResponse albumResponse;
    @BeforeEach
    void datos() {
        user2 = new UserPrincipal(2L, "Nombre2", "Apellido2", "user", "user@user.com", "user", List.of(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())));
        al = new AlbumRequest();
        al.setTitle("titulo del nuevo album");
        al.setCreatedBy(1L);
        al.setCreatedAt(Instant.now());
        al.setUpdatedAt(Instant.now());
        albumResponse = new AlbumResponse();
        albumResponse.setTitle(al.getTitle());
        albumResponse.setCreatedBy(al.getCreatedBy());
        albumResponse.setCreatedAt(Instant.now());
        albumResponse.setUpdatedAt(Instant.now());
    }

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

    @Test
    @WithUserDetails("user")
    void addAlbum_givenAlbumRequest_ReturnOk() throws Exception {

        ResponseEntity <Album> rp;
        rp = new ResponseEntity(al, HttpStatus.CREATED);

        when(albumService.addAlbum(al, user2)).thenReturn(rp);
        mockMvc.perform(post("/api/albums/")
                        .contentType("application/json").content(objectMapper.writeValueAsString(rp)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithUserDetails("admin")
    void addAlbum_givenAlbumRequest_ReturnForbidden() throws Exception {
        ResponseEntity <Album> rp;
        rp = new ResponseEntity(al, HttpStatus.CREATED);

        when(albumService.addAlbum(al, user2)).thenReturn(rp);
        mockMvc.perform(post("/api/albums/")
                        .contentType("application/json").content(objectMapper.writeValueAsString(rp)))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    void addAlbum_givenAlbumRequest_ReturnUnauthorized() throws Exception {
        ResponseEntity <Album> rp = new ResponseEntity(al, HttpStatus.CREATED);

        when(albumService.addAlbum(al, user2)).thenReturn(rp);
        mockMvc.perform(post("/api/albums/")
                        .contentType("application/json").content(objectMapper.writeValueAsString(rp)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    void getAlbum_givenAlbumId_thenReturnAlbum () throws Exception {
        Album album = new Album();
        album.setCreatedAt(Instant.now());
        album.setUpdatedAt(Instant.now());
        album.setTitle("album nuevo");

        ResponseEntity <Album> rp = new ResponseEntity(album, HttpStatus.OK);

        when(albumService.getAlbum(1L)).thenReturn(rp);

        mockMvc.perform(get("/api/albums/{id}", 1)
                        .contentType("application/json").content(objectMapper.writeValueAsString(rp)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @WithUserDetails("user")
    void updateAlbum_givenAlbumRequest_ReturnOk() throws Exception {
        ResponseEntity <AlbumResponse> rp = new ResponseEntity(albumResponse, HttpStatus.CREATED);

        when(albumService.updateAlbum(1L, al, user2)).thenReturn(rp);
        mockMvc.perform(put("/api/albums/{id}", 1)
                        .contentType("application/json").content(objectMapper.writeValueAsString(rp)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void updateAlbum_givenAlbumRequest_ReturnUnauthorized() throws Exception {
        ResponseEntity <AlbumResponse> rp = new ResponseEntity(albumResponse, HttpStatus.CREATED);

        when(albumService.updateAlbum(1L, al, user2)).thenReturn(rp);
        mockMvc.perform(put("/api/albums/{id}", 1)
                        .contentType("application/json").content(objectMapper.writeValueAsString(rp)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    @WithUserDetails("admin")
    void deleteAlbum_givenAlbumId_ReturnOk() throws Exception {
        ResponseEntity<ApiResponse> ap = new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "You successfully deleted album"), HttpStatus.NO_CONTENT);
        when(albumService.deleteAlbum(1L, user2)).thenReturn(ap);

        mockMvc.perform(delete("/api/albums/{id}", 1)
                        .contentType("application/json").content(objectMapper.writeValueAsString(ap)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void deleteAlbum_givenAlbumId_ReturnUnauthorized() throws Exception {
        ResponseEntity<ApiResponse> ap = new ResponseEntity<>(new ApiResponse(Boolean.TRUE, "You successfully deleted album"), HttpStatus.NO_CONTENT);
        when(albumService.deleteAlbum(1L, user2)).thenReturn(ap);

        mockMvc.perform(delete("/api/albums/{id}", 1)
                        .contentType("application/json").content(objectMapper.writeValueAsString(ap)))
                .andExpect(status().isUnauthorized())
                .andReturn();
    }

    @Test
    void getAllPhotosByAlbum_givenAlbumId_thenReturnOK() throws Exception {
        PhotoResponse p1 = new PhotoResponse(1L, "photo 1", "imagen1.jpg","imagen2.jpg", 1L);

        PagedResponse<PhotoResponse> p = new PagedResponse<>();
        p.setContent(List.of(p1));
        when(photoService.getAllPhotosByAlbum(1L, 0, 10)).thenReturn(p);

        mockMvc.perform(get("/api/albums/{id}/photos?page={page}&size={size}", 1, 0,10)
                        .contentType("application/json").content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isOk())
                .andReturn();
    }
}
