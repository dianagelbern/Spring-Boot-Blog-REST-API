package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.BlogapiException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Album;
import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.payload.PhotoResponse;
import com.sopromadze.blogapi.repository.PhotoRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.utils.AppUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @InjectMocks
    PhotoServiceImpl photoService;

    //Probar con Role user
    //Probar con Role admin
    //Hacer exceptions

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
    void givenIdOfPhoto_thenShowPhotoResponse(){

    }

    /*
    @Test
    void askingForAllPhotos_themReturnPageResponse(){

        Album a = Album.builder().id(1L).title("Título album").build();
        Photo foto = Photo.builder().title("Esto es una foto").album(a).url("jdsldfsdfs").thumbnailUrl("ksddksfdfs").id(1L).build();
        Pageable pageable = PageRequest.of(0, 1, Sort.Direction.DESC, CREATED_AT);

        Page<Photo> photos = new PageImpl<>(List.of(foto));
        PagedResponse<Photo> paginas = new PagedResponse<>();

        paginas.setLast(true);
        paginas.setSize(1);
        paginas.setPage(0);
        paginas.setTotalPages(1);
        paginas.setTotalElements(1);
        paginas.setContent(photos.getContent());


        Mockito.when(photoRepository.findAll(pageable)).thenReturn(photos);
        assertEquals(paginas, photoService.getAllPhotos(0, 1));
    }

 */
}