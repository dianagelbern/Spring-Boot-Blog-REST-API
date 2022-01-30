package com.sopromadze.blogapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopromadze.blogapi.SpringSecurityTestWebConfig;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.Address;
import com.sopromadze.blogapi.model.user.Company;
import com.sopromadze.blogapi.model.user.Geo;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.*;
import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.impl.PostServiceImpl;
import com.sopromadze.blogapi.service.impl.UserServiceImpl;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {SpringSecurityTestWebConfig.class})
@AutoConfigureMockMvc
@Log
public class UserControllerTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PostServiceImpl postService;

    @MockBean
    UserServiceImpl userService;

    @Test
    void getPostsCreatedBy_givenUsername_ShouldShowPostList() throws Exception {

        User user = User.builder()
                .id(1L)
                .firstName("Ernesto")
                .lastName("Fatuarte")
                .username("efatuarte")
                .password("123456789")
                .email("ernesto.fatuarte@gmail.com")
                .phone("666666666")
                .build();

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Controlador de post");
        post.setBody("Esto es un post de prueba para testear un método");
        post.setCreatedBy(1L);
        post.setUpdatedAt(Instant.now());

        PagedResponse<Post> postList = new PagedResponse<>();
        postList.setContent(List.of(post));
        postList.setPage(0);
        postList.setSize(10);

        when(postService.getPostsByCreatedBy(user.getUsername(), 0, 10)).thenReturn(postList);

        MvcResult result = mockMvc.perform(get("/api/users/{username}/posts?page={page}&size={size}", "efatuarte", 0, 10))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(postList)))
                .andReturn();

    }


    @Test
    void givenUsername_thenCheckAvaiability() throws Exception {

        UserPrincipal userPrincipal = new UserPrincipal(1L, "Diana", "González", "Gelbern", "diana@gmail.com", "123456789", List.of(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())));

        UserIdentityAvailability userIdentityAvailability = new UserIdentityAvailability(true);
        when(userService.checkUsernameAvailability(userPrincipal.getUsername())).thenReturn(userIdentityAvailability);

        mockMvc.perform(get("/api/users/checkUsernameAvailability")
                .contentType("application/json")
                .param("username", userPrincipal.getUsername())
                .content(objectMapper.writeValueAsString(userIdentityAvailability)))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("admin")
    void givenAdmin_givenUsername_shouldShowSucces_test () throws Exception{

        User user = User.builder()
                .id(1L)
                .firstName("Richard")
                .lastName("Cespedes")
                .username("cespedesPeric21")
                .password("123456789")
                .email("cespedesPeric21@triana.com")
                .phone("123456789")
                .build();

        ApiResponse ap = new ApiResponse(Boolean.TRUE, "You gave ADMIN role to user: " + user.getUsername());
        ap.setStatus(HttpStatus.OK);
        when(userService.giveAdmin(user.getUsername())).thenReturn(ap);

        MvcResult result = mockMvc.perform(put("/api/users/{username}/giveAdmin", user.getUsername()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("You gave ADMIN role to user: " + user.getUsername())))
                .andExpect(content().json(objectMapper.writeValueAsString(ap)))
                .andReturn();

    }
  
  @Test
  void givenUserName_thenReturnUSerProfile() throws Exception{
        UserProfile userProfile = UserProfile.builder().username("Gelbern").firstName("Diana").lastName("González").email("diana@gmail.com").build();

        when(userService.getUserProfile(userProfile.getUsername())).thenReturn(userProfile);

        mockMvc.perform(get("/api/users/{username}/profile", userProfile.getUsername())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(userProfile)))
                .andExpect(status().isOk());
   }

     @Test
    @WithUserDetails("admin")
    void setAddress_givenInfoRequest_thenReturnOK_test() throws Exception{

        User user = new User();
        user.setId(1L);
        user.setFirstName("Richard");
        user.setLastName("Céspedes");
        user.setUsername("rick4");
        user.setEmail("richard@cespedes.com");
        user.setPassword("123456");

        UserPrincipal richard = new UserPrincipal(1L, "Richard", "Céspedes", "rick4", "richard@cespedes.com", "123456", List.of(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));
        InfoRequest infoRequest = new InfoRequest();
        infoRequest.setWebsite("website");
        infoRequest.setLng("3458949");
        infoRequest.setLat("2654636");
        infoRequest.setCompanyName("companyName");
        infoRequest.setZipcode("92548");
        infoRequest.setBs("bs");
        infoRequest.setCity("sevilla");
        infoRequest.setSuite("normal");
        infoRequest.setStreet("Calle condes de bustillo");

        Geo geo = new Geo(infoRequest.getLat(), infoRequest.getLng());
        Address address = new Address(infoRequest.getStreet(), infoRequest.getSuite(), infoRequest.getCity(),
                infoRequest.getZipcode(), geo);
        Company company = new Company(infoRequest.getCompanyName(), infoRequest.getCatchPhrase(), infoRequest.getBs());

        user.setAddress(address);
        user.setCompany(company);
        user.setWebsite(infoRequest.getWebsite());
        user.setPhone(infoRequest.getPhone());


        UserProfile up = new UserProfile(user.getId(), user.getUsername(),
                user.getFirstName(), user.getLastName(), user.getCreatedAt(),
                user.getEmail(), user.getAddress(), user.getPhone(), user.getWebsite(),
                user.getCompany(), 0L);
        when(userService.setOrUpdateInfo(richard, infoRequest)).thenReturn(up);

        mockMvc.perform(put("/api/users/setOrUpdateInfo")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(infoRequest)))
                .andExpect(status().isOk());
    }

}
