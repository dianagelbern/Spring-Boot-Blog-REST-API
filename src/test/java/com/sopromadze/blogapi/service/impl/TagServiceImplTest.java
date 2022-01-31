package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.exception.UnauthorizedException;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.AlbumRepository;
import com.sopromadze.blogapi.repository.TagRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.TagServiceImpl;
import com.sopromadze.blogapi.utils.AppUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    TagServiceImpl tagService;

    Tag t1;
    UserPrincipal user2;
    @BeforeEach
    void datos () {
        t1 = new Tag("tagNuevo");
        t1.setId(1L);
        user2 = new UserPrincipal(2L, "Nombre2", "Apellido2", "user", "user@user.com", "user", List.of(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())));
        t1.setCreatedBy(user2.getId());
    }


    @Test
    void getAllTags_ShowAllTagsTest() {

        int page=1,size=1;
        AppUtils.validatePageNumberAndSize(page, size);
        Tag t1 = new Tag("tagNuevo");
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "createdAt");
        Page<Tag> tags = new PageImpl<>(Arrays.asList(t1));

        when(tagRepository.findAll(pageable)).thenReturn(tags);
        PagedResponse<Tag> result = tagService.getAllTags(page, size);

        assertEquals(1, result.getSize());
        assertEquals(t1, result.getContent().get(0));

    }

    @Test
    void getTagById_givenTagId_ShouldShowTagTest() {

        when(tagRepository.findById(1L)).thenReturn(Optional.of(t1));

        Tag result = tagService.getTag(1L);

        assertEquals(t1, result);
    }

    @Test
    void getTagById_givenNotExistTagId_ThrowsResourceNotFoundExceptionTest() {
        when(tagRepository.findById(2L)).thenThrow(new ResourceNotFoundException("Tag", "id", 2L));

        assertThrows(ResourceNotFoundException.class, () -> tagService.getTag(2L));
    }


    @Test
    void addTag_givenTag_ReturnNewTag() {
        when(tagRepository.save(t1)).thenReturn(t1);

        assertEquals(t1, tagService.addTag(t1, user2));
    }

    @Test
    void updateTag_givenTagId_thenReturnTag() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(t1));
        when(tagRepository.save(t1)).thenReturn(t1);

        assertEquals(t1, tagService.updateTag(1L, t1, user2));
    }

    @Test
    void updateTag_givenTagId_thenThrowsResourceNotFoundException () {
        when(tagRepository.findById(2L)).thenThrow(new ResourceNotFoundException("tag", "id", 2L));

        assertThrows(ResourceNotFoundException.class, () -> tagService.updateTag(2L, t1, user2));
    }

    @Test
    void updateTag_givenTagId_thenThrowsUnauthorizedException() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(t1));
        UserPrincipal user1 = new UserPrincipal(3L, "Nombre1", "Apellido1", "admin", "admin@admin.com", "admin", List.of(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())));
        when(tagRepository.save(t1)).thenReturn(t1);

        assertThrows(UnauthorizedException.class, () -> tagService.updateTag(1L, t1, user1));
    }

    @Test
    void deleteTag_givenTagId_thenReturnApiResponse() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(t1));
        when(tagRepository.save(t1)).thenReturn(t1);

        ApiResponse ap = new ApiResponse(Boolean.TRUE, "You successfully deleted tag");
        assertEquals(ap, tagService.deleteTag(1L, user2));
    }

    @Test
    void deleteTag_givenTagId_thenThrowsResourceNotFoundException () {
        when(tagRepository.findById(2L)).thenThrow(new ResourceNotFoundException("tag", "id", 2L));

        assertThrows(ResourceNotFoundException.class, () -> tagService.deleteTag(2L, user2));
    }

    @Test
    void deleteTag_givenTagId_thenThrowsUnauthorizedException() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(t1));
        UserPrincipal user1 = new UserPrincipal(3L, "Nombre1", "Apellido1", "admin", "admin@admin.com", "admin", List.of(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())));
        when(tagRepository.save(t1)).thenReturn(t1);

        assertThrows(UnauthorizedException.class, () -> tagService.deleteTag(1L, user1));
    }
}