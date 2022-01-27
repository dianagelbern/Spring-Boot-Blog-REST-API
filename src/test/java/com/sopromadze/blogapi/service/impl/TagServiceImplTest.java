package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.AlbumRepository;
import com.sopromadze.blogapi.repository.TagRepository;
import com.sopromadze.blogapi.repository.UserRepository;
import com.sopromadze.blogapi.service.impl.TagServiceImpl;
import com.sopromadze.blogapi.utils.AppUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.*;
import java.util.Arrays;
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
        Tag t1 = new Tag("tagNuevo");
        t1.setId(1L);
        when(tagRepository.findById(1L)).thenReturn(Optional.of(t1));

        Tag result = tagService.getTag(1L);

        assertEquals(t1, result);
    }

    @Test
    void getTagById_givenNotExistTagId_ThrowsResourceNotFoundExceptionTest() {
        Tag t1 = new Tag("tagNuevo");
        t1.setId(1L);

        when(tagRepository.findById(2L)).thenThrow(new ResourceNotFoundException("Tag", "id", 2L));

        assertThrows(ResourceNotFoundException.class, () -> tagService.getTag(2L));
    }


}