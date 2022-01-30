package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.BlogapiException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PhotoRequest;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.repository.AlbumRepository;
import com.sopromadze.blogapi.repository.PhotoRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.utils.AppUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.parameters.P;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.sopromadze.blogapi.utils.AppConstants.CREATED_AT;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PhotoServiceImplTest {

    @Mock
    PhotoRepository photoRepository;

    @Mock
    AlbumRepository albumRepository;

    @InjectMocks
    PhotoServiceImpl photoService;


    @Test
    void findOneResourceNotFoundException(){
        User diana = User.builder()
                .firstName("Diana")
                .lastName("González")
                .username("Gelbern")
                .password("123456789")
                .email("diana@gmail.com")
                .build();
        diana.setCreatedAt(Instant.now());
        diana.setUpdatedAt(Instant.now());

        Photo foto = Photo.builder().title("Esto es una foto").url("jdsldfsdfs").thumbnailUrl("ksddksfdfs").id(1L).build();

        UserPrincipal dianaUser = UserPrincipal.builder().authorities(Arrays.asList(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))).username(diana.getUsername()).password("1234567").build();


        assertThrows(ResourceNotFoundException.class, () -> photoService.deletePhoto(0L, dianaUser));
    }

    @Test
    void validateBlogapiException(){
        Photo foto = Photo.builder().title("Esto es una foto").url("jdsldfsdfs").thumbnailUrl("ksddksfdfs").id(1L).build();

        PagedResponse p = PagedResponse.builder().content(List.of(foto)).size(-10).page(-10).build();
        assertThrows(BlogapiException.class, () -> photoService.getAllPhotos(p.getPage(), p.getSize()));
    }


    @Test
    void deletePhotoSelectedBeingUser() {
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

        Album album = new Album();
        album.setTitle("Título");
        album.setId(1L);
        album.setUser(diana);

        Photo foto = Photo.builder().id(1L).title("Esto es una foto").album(album).url("jdsldfsdfs").thumbnailUrl("ksddksfdfs").id(1L).build();

        UserPrincipal dianaUser = UserPrincipal.builder().id(1L).username(diana.getUsername()).authorities(Arrays.asList(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))).username(diana.getUsername()).password("1234567").build();

        Mockito.lenient().when(photoRepository.findById(foto.getId())).thenReturn(Optional.of(foto));
        ApiResponse a = new ApiResponse(Boolean.TRUE, "Photo deleted successfully");
        Mockito.doNothing().when(photoRepository).deleteById(foto.getId());
        assertEquals(a, photoService.deletePhoto(foto.getId(), dianaUser ));

    }

    @Test
    void deletePhotoSelectedBeingAdmin(){
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

        Album album = new Album();
        album.setTitle("Título");
        album.setId(1L);
        album.setUser(diana);

        Photo foto = Photo.builder().id(1L).title("Esto es una foto").album(album).url("jdsldfsdfs").thumbnailUrl("ksddksfdfs").id(1L).build();

        UserPrincipal dianaAdmin = UserPrincipal.builder().id(1L).username(diana.getUsername()).authorities(Arrays.asList(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))).username(diana.getUsername()).password("1234567").build();

        Mockito.lenient().when(photoRepository.findById(foto.getId())).thenReturn(Optional.of(foto));
        ApiResponse a = new ApiResponse(Boolean.TRUE, "Photo deleted successfully");
        Mockito.doNothing().when(photoRepository).deleteById(foto.getId());
        assertEquals(a, photoService.deletePhoto(foto.getId(), dianaAdmin ));
    }


    @Test
    void givenPhotoId_thenShowPhotoResponse(){
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

        Album album = new Album();
        album.setTitle("Título");
        album.setId(1L);
        album.setUser(diana);

        Photo foto = Photo.builder().id(1L).title("Esto es una foto").album(album).url("jdsldfsdfs").thumbnailUrl("ksddksfdfs").id(1L).build();
        PhotoResponse p = new PhotoResponse(foto.getId(), foto.getTitle(), foto.getUrl(), foto.getThumbnailUrl(), album.getId());
        Mockito.when(photoRepository.findById(foto.getId())).thenReturn(Optional.of(foto));
        assertEquals(p, photoService.getPhoto(foto.getId()));
    }


    @Test
    void askingForAllPhotos_thenReturnPageResponse(){

        Album a = Album.builder().id(1L).title("Título album").build();
        Photo foto = Photo.builder().title("Esto es una foto").album(a).url("jdsldfsdfs").thumbnailUrl("ksddksfdfs").id(1L).build();
        Pageable pageable = PageRequest.of(0, 1, Sort.Direction.DESC, CREATED_AT);

        Page<Photo> photos = new PageImpl<>(List.of(foto));

        PhotoResponse p = new PhotoResponse(foto.getId(), foto.getTitle(), foto.getUrl(), foto.getThumbnailUrl(), a.getId());
        PagedResponse<PhotoResponse> paginas = new PagedResponse<>();

        paginas.setLast(true);
        paginas.setSize(1);
        paginas.setPage(0);
        paginas.setTotalPages(1);
        paginas.setTotalElements(1);
        paginas.setContent(List.of(p));

        Mockito.when(photoRepository.findAll(pageable)).thenReturn(photos);
        assertEquals(paginas, photoService.getAllPhotos(0, 1));
    }

    @Test
    void askingFotUpdatePhoto_thenReturnPageResponseBeingAdmin(){
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

        Album album = new Album();
        album.setTitle("Título");
        album.setId(1L);
        album.setUser(diana);
        Photo foto = Photo.builder().title("Esto es una foto").album(album).url("jdsldfsdfs").thumbnailUrl("ksddksfdfs").id(1L).build();
        UserPrincipal dianaAdmin = UserPrincipal.builder().id(1L).username(diana.getUsername()).authorities(Arrays.asList(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))).username(diana.getUsername()).password("1234567").build();
        PhotoResponse p = new PhotoResponse(foto.getId(), foto.getTitle(), foto.getUrl(), foto.getThumbnailUrl(), album.getId());
        PhotoRequest pr = PhotoRequest.builder().albumId(album.getId()).thumbnailUrl(foto.getThumbnailUrl()).title(foto.getTitle()).build();

        Mockito.when(albumRepository.findById(pr.getAlbumId())).thenReturn(Optional.of(album));
        Mockito.when(photoRepository.findById(foto.getId())).thenReturn(Optional.of(foto));
        Mockito.when(photoRepository.save(foto)).thenReturn(foto);

        assertEquals(p, photoService.updatePhoto(foto.getId(), pr, dianaAdmin));
    }

    @Test
    void askingFotUpdatePhoto_thenReturnPageResponseBeingUser(){
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

        Album album = new Album();
        album.setTitle("Título");
        album.setId(1L);
        album.setUser(diana);
        Photo foto = Photo.builder().title("Esto es una foto").album(album).url("jdsldfsdfs").thumbnailUrl("ksddksfdfs").id(1L).build();
        UserPrincipal dianaUser = UserPrincipal.builder().id(1L).username(diana.getUsername()).authorities(Arrays.asList(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))).username(diana.getUsername()).password("1234567").build();
        PhotoResponse p = new PhotoResponse(foto.getId(), foto.getTitle(), foto.getUrl(), foto.getThumbnailUrl(), album.getId());
        PhotoRequest pr = PhotoRequest.builder().albumId(album.getId()).thumbnailUrl(foto.getThumbnailUrl()).title(foto.getTitle()).build();

        Mockito.when(albumRepository.findById(pr.getAlbumId())).thenReturn(Optional.of(album));
        Mockito.when(photoRepository.findById(foto.getId())).thenReturn(Optional.of(foto));
        Mockito.when(photoRepository.save(foto)).thenReturn(foto);

        assertEquals(p, photoService.updatePhoto(foto.getId(), pr, dianaUser));
    }

    @Test
    void askingForAddPhoto_thenReturnPhotoResponseBeingUser(){
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

        Album album = new Album();
        album.setTitle("Título");
        album.setId(1L);
        album.setUser(diana);
        UserPrincipal dianaUser = UserPrincipal.builder().id(1L).username(diana.getUsername()).authorities(Arrays.asList(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))).username(diana.getUsername()).password("1234567").build();
        Photo foto = new Photo();
        foto.setAlbum(album);

        PhotoResponse p = new PhotoResponse(foto.getId(), foto.getTitle(), foto.getUrl(), foto.getThumbnailUrl(), album.getId());
        PhotoRequest pr = PhotoRequest.builder().albumId(album.getId()).title(foto.getTitle()).thumbnailUrl(foto.getThumbnailUrl()).build();
        Mockito.when(albumRepository.findById(pr.getAlbumId())).thenReturn(Optional.of(album));
        Mockito.when(photoRepository.save(foto)).thenReturn(foto);
        assertEquals(p, photoService.addPhoto(pr, dianaUser));
    }

    @Test
    void askingForAddPhoto_thenReturnPhotoResponseBeingAdmin(){
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

        Album album = new Album();
        album.setTitle("Título");
        album.setId(1L);
        album.setUser(diana);
        UserPrincipal dianaAdmin = UserPrincipal.builder().id(1L).username(diana.getUsername()).authorities(Arrays.asList(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))).username(diana.getUsername()).password("1234567").build();
        Photo foto = new Photo();
        foto.setAlbum(album);

        PhotoResponse p = new PhotoResponse(foto.getId(), foto.getTitle(), foto.getUrl(), foto.getThumbnailUrl(), album.getId());
        PhotoRequest pr = PhotoRequest.builder().albumId(album.getId()).title(foto.getTitle()).thumbnailUrl(foto.getThumbnailUrl()).build();
        
        Mockito.when(albumRepository.findById(pr.getAlbumId())).thenReturn(Optional.of(album));
        Mockito.when(photoRepository.save(foto)).thenReturn(foto);
        assertEquals(p, photoService.addPhoto(pr, dianaAdmin));
    }

    @Test
    void askingForAllPhotodByAlbum_thenReturnPageResponse(){
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

        Album album = new Album();
        album.setTitle("Título");
        album.setId(1L);
        album.setUser(diana);

        Photo foto = Photo.builder().title("Esto es una foto").album(album).url("jdsldfsdfs").thumbnailUrl("ksddksfdfs").id(1L).build();
        Pageable pageable = PageRequest.of(0, 1, Sort.Direction.DESC, CREATED_AT);
        PhotoResponse p = new PhotoResponse(foto.getId(), foto.getTitle(), foto.getUrl(), foto.getThumbnailUrl(), album.getId());
        Page<Photo> photos = new PageImpl<>(List.of(foto));

        PagedResponse<PhotoResponse> paginas = new PagedResponse<>();

        paginas.setLast(true);
        paginas.setSize(1);
        paginas.setPage(0);
        paginas.setTotalPages(1);
        paginas.setTotalElements(1);
        paginas.setContent(List.of(p));
        Mockito.when(photoRepository.findByAlbumId(album.getId(), pageable)).thenReturn(photos);

        assertEquals(paginas, photoService.getAllPhotosByAlbum(album.getId(), 0, 1));
    }


}