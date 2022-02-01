package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.AlbumResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.request.AlbumRequest;
import com.sopromadze.blogapi.repository.AlbumRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.hibernate.engine.spi.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AlbumServiceImplTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    AlbumServiceImpl albumService;

    @Test
    void getAllAlbums_givenAlbumList_shouldReturnAll(){

        Album album = new Album();
        album.setTitle("Thriller");
        album.setCreatedAt(Instant.now());
        album.setUpdatedAt(Instant.now());

        AlbumResponse albumResponse = new AlbumResponse();
        albumResponse.setId(1L);
        albumResponse.setTitle("Thriller");

        Page<Album> result = new PageImpl<>(List.of(album));
        List<AlbumResponse> listResult = List.of(albumResponse);
        AlbumResponse[] albumResponseList = {albumResponse};

        PagedResponse<AlbumResponse> pagedResult = new PagedResponse<>();
        pagedResult.setContent(listResult);
        pagedResult.setTotalPages(1);
        pagedResult.setTotalElements(1);
        pagedResult.setLast(true);
        pagedResult.setSize(1);

        when(albumRepository.findAll(any(Pageable.class))).thenReturn(result);
        when(modelMapper.map(any(), any())).thenReturn(albumResponseList);

        assertEquals(pagedResult, albumService.getAllAlbums(1,10));
    }

    @Test
    void addAlbum_givenAlbumAndUser_ShouldReturnAlbumAdded() {
        User ernesto = new User("Ernesto","Fatuarte", "efatuarte","efatuarte@gmail.com","123456789");
        ernesto.setId(1L);

        UserPrincipal ernestoPrincipal = new UserPrincipal(1L,"Ernesto","Fatuarte", "efatuarte","efatuarte@gmail.com","123456789", List.of(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));
        when(userRepository.getUserByName(ernestoPrincipal.getUsername())).thenReturn(ernesto);

        Album album = new Album();
        album.setUser(userRepository.getUser(ernestoPrincipal));
        when(albumRepository.save(album)).thenReturn(album);

        AlbumRequest albumRequest = new AlbumRequest();

        assertEquals(albumService.addAlbum(albumRequest, ernestoPrincipal), new ResponseEntity<>(album, HttpStatus.CREATED));

    }

    @Test
    void getUserAlbums_givenUsername_shouldReturnAlbumPagedList(){

        User user = new User();
        user.setId(1L);
        user.setFirstName("Ernesto");
        user.setLastName("Fatuarte");
        user.setUsername("efatuarte");
        user.setEmail("efatuarte@gmail.com");
        user.setPassword("12345");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        when(userRepository.getUserByName("efatuarte")).thenReturn(user);

        Album album = new Album();
        album.setTitle("Thriller");

        Pageable pageable = PageRequest.of(1, 10);

        Page<Album> albums = new PageImpl<Album>(List.of(album));

        PagedResponse<Album> pagedList = new PagedResponse<>();
        pagedList.setContent(albums.getContent());
        pagedList.setTotalPages(1);
        pagedList.setTotalElements(1);
        pagedList.setLast(true);
        pagedList.setSize(1);

        when(albumRepository.findByCreatedBy(1L, pageable)).thenReturn(albums);

        assertEquals(pagedList, albumService.getUserAlbums("efatuarte",1,1));
    }

    @Test
    void getUserAlbums_givenNonExistingUsername_shouldThrowNullPointerException(){

        User user = new User();
        user.setId(1L);
        user.setFirstName("Ernesto");
        user.setLastName("Fatuarte");
        user.setUsername("efatuarte");
        user.setEmail("efatuarte@gmail.com");
        user.setPassword("12345");
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(Instant.now());

        when(userRepository.getUserByName("efatuarte")).thenReturn(user);

        Album album = new Album();
        album.setId(1L);
        album.setTitle("Thriller");

        PageImpl<Album> albums = new PageImpl<>(List.of(album));

        Pageable pageable = PageRequest.of(1, 10);

        when(albumRepository.findByCreatedBy(1L,pageable)).thenReturn(albums);

        assertThrows(NullPointerException.class , () -> albumService.getUserAlbums("rick429",1,10));
    }
}
