package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Photo;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.repository.PhotoRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

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

        Photo foto = Photo.builder().title("Esto es una foto").url("jdsldfsdfs").thumbnailUrl("ksddksfdfs").id(1L).build();

        UserPrincipal dianaUser = UserPrincipal.builder().username(diana.getUsername()).authorities(Arrays.asList(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString()))).username(diana.getUsername()).password("1234567").build();

        Mockito.lenient().when(photoRepository.findById(foto.getId())).thenReturn(Optional.of(foto));
        assertTrue(photoRepository.findAll().size()==0);

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

        Photo foto = Photo.builder().title("Esto es una foto").url("jdsldfsdfs").thumbnailUrl("ksddksfdfs").id(1L).build();

        UserPrincipal elAdmin = UserPrincipal.builder().username(diana.getUsername()).authorities(Arrays.asList(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))).username(diana.getUsername()).password("1234567").build();

        Mockito.lenient().when(photoRepository.findById(foto.getId())).thenReturn(Optional.of(foto));
        assertTrue(photoRepository.findAll().size()==0);
    }
}