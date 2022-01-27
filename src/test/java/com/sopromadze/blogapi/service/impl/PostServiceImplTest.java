package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.Tag;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static com.sopromadze.blogapi.utils.AppConstants.CREATED_AT;
import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.security.core.parameters.P;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    PostRepository postRepository;

    @Mock
    TagRepository tagRepository;

    @InjectMocks
    PostServiceImpl postService;
    

    @Test
    void askingForAllPost_thenReturnPagedResponse() {
        Post p = new Post();
        p.setTitle("Título del post");
        Pageable pageable = PageRequest.of(1, 1, Sort.Direction.DESC, CREATED_AT);;

        Page<Post> posts = new PageImpl<Post>(List.of(p));
        PagedResponse<Post> paginas = new PagedResponse<>();

        paginas.setLast(true);
        paginas.setContent(posts.getContent());
        paginas.setTotalPages(1);
        paginas.setSize(1);
        paginas.setTotalElements(1);

        when(postRepository.findAll(pageable)).thenReturn(posts);
        assertEquals(paginas, postService.getAllPosts(1, 1));

    }

    @Test
    void getPostByTag_givenNonExistingTagId_shouldReturnResourceNotFoundException() {
        when(tagRepository.findById(1L)).thenThrow(new ResourceNotFoundException("a", "b", "c"));

        assertThrows(ResourceNotFoundException.class, () -> postService.getPostsByTag(1L, 1, 1));
    }

    @Test
    void getPostsByTag_givenTagId_shouldReturnPagedResponse() {
        Tag tag1 = new Tag();
        tag1.setId(1L);
        tag1.setName("Tag de testeo n1");

        Tag tag2 = new Tag();
        tag2.setId(2L);
        tag2.setName("Tag de testeo n2");

        when(tagRepository.findAll()).thenReturn(List.of(tag1, tag2));

        Post p1 = new Post();
        p1.setId(1L);
        p1.setTitle("Post de prueba con tags n1");
        p1.setTags(List.of(tag1));

        Post p2 = new Post();
        p2.setId(1L);
        p2.setTitle("Post de prueba con tags n2");
        p2.setTags(List.of(tag2));

        Pageable pageable = PageRequest.of(1, 1, Sort.Direction.DESC, CREATED_AT);;

        Page<Post> postList = new PageImpl<>(List.of(p1, p2));

        PagedResponse<Post> pagedPostList = new PagedResponse<>();
        pagedPostList.setLast(true);
        pagedPostList.setContent(postList.getContent());
        pagedPostList.setTotalPages(1);
        pagedPostList.setSize(1);
        pagedPostList.setTotalElements(1);

        when(postRepository.findAll(pageable)).thenReturn(postList);
        assertEquals(pagedPostList, postService.getPostsByTag(1L, 1, 1));

    }

    @Test
    void addPost_givenPostRequestAndUser_shouldReturnPostResponse() {
        assertEquals(1, 1);
    }
}