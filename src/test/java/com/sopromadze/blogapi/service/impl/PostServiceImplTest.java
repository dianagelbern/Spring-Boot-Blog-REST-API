package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.payload.PagedResponse;
import com.sopromadze.blogapi.repository.PostRepository;
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
}