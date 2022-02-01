package com.sopromadze.blogapi.service.impl;

import com.sopromadze.blogapi.exception.AccessDeniedException;
import com.sopromadze.blogapi.exception.AppException;
import com.sopromadze.blogapi.exception.BadRequestException;
import com.sopromadze.blogapi.exception.ResourceNotFoundException;
import com.sopromadze.blogapi.model.Category;
import com.sopromadze.blogapi.model.Post;
import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.Address;
import com.sopromadze.blogapi.model.user.Company;
import com.sopromadze.blogapi.model.user.Geo;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.payload.ApiResponse;
import com.sopromadze.blogapi.payload.InfoRequest;
import com.sopromadze.blogapi.payload.UserIdentityAvailability;
import com.sopromadze.blogapi.payload.UserProfile;
import com.sopromadze.blogapi.repository.PostRepository;
import com.sopromadze.blogapi.repository.RoleRepository;
import com.sopromadze.blogapi.repository.UserRepository;

import com.sopromadze.blogapi.security.UserPrincipal;
import com.sopromadze.blogapi.service.UserService;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;


import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
@Log
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserRepository userRepository;

    @Mock
    PostRepository postRepository;

    @InjectMocks
    UserServiceImpl userService;

    @MockBean
    TestEntityManager entityManager;

    @Test
    void givenEmailExistent_thenReturnFalse() {
        User diana = User.builder()
                .email("diana@gmail.com")
                .build();

        when(!userRepository.existsByEmail(diana.getEmail())).thenReturn(false);

        assertEquals(new UserIdentityAvailability(true), userService.checkEmailAvailability("diana@gmail.com"));
    }


    @Test
    void givenUsername_thenReturnUserProfile() {

        Post p = new Post();
        p.setTitle("Título");
        List<Post> listaDePost = List.of(p);
        Company c = Company.builder().build();
        Address a = Address.builder().build();

        User diana = User.builder()
                .id(1L)
                .firstName("Diana")
                .lastName("González")
                .username("Gelbern")
                .password("123456789")
                .phone("293040586")
                .website("www.diana.com")
                .company(c)
                .address(a)
                .posts(listaDePost)
                .email("diana@gmail.com")
                .build();

        diana.setCreatedAt(Instant.now());
        diana.setUpdatedAt(Instant.now());

        UserProfile up = new UserProfile(diana.getId(), diana.getUsername(), diana.getFirstName(), diana.getLastName(),
                diana.getCreatedAt(), diana.getEmail(), diana.getAddress(), diana.getPhone(), diana.getWebsite(),
                diana.getCompany(), postRepository.countByCreatedBy(diana.getId()));

        when(userRepository.save(diana)).thenReturn(diana);
        when(userRepository.getUserByName(diana.getUsername())).thenReturn(diana);
        when(postRepository.countByCreatedBy(any(Long.class))).thenReturn((long) diana.getPosts().size());
        
        assertEquals(up.getUsername(), userService.getUserProfile(diana.getUsername()).getUsername());

    }

    @Test
    void addUser_givenUser_thenReturnUserTest(){
        User u = new User();
        u.setUsername("cesperic21");
        u.setFirstName("Richard");
        u.setLastName("Céspedes Pedrazas");
        u.setEmail("cespedes@pedrazas.com");
        u.setPassword("123456");
        u.setUpdatedAt(Instant.now());
        u.setCreatedAt(Instant.now());
        Role role = new Role();
        role.setName(RoleName.ROLE_USER);

        when(userRepository.existsByUsername("cesperic")).thenReturn(false);
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(u.getPassword())).thenReturn(u.getPassword());
        when(userRepository.save(u)).thenReturn(u);

        User result = userService.addUser(u);

        assertEquals(u, result);
    }

    @Test
    void addUser_givenExistUsername_ThrowsBadRequestException_Test() {
        User u = new User();
        u.setUsername("cesperic21");
        u.setFirstName("Richard");
        u.setLastName("Céspedes Pedrazas");
        u.setEmail("cespedes@pedrazas.com");
        u.setPassword("123456");
        u.setUpdatedAt(Instant.now());
        u.setCreatedAt(Instant.now());
        Role role = new Role();
        role.setName(RoleName.ROLE_USER);

        when(userRepository.existsByUsername("cesperic21")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.addUser(u));
    }

    @Test
    void addUser_givenExistEmail_ThrowsBadRequestException_Test() {
        User u = new User();
        u.setUsername("cesperic21");
        u.setFirstName("Richard");
        u.setLastName("Céspedes Pedrazas");
        u.setEmail("cespedes@pedrazas.com");
        u.setPassword("123456");
        u.setUpdatedAt(Instant.now());
        u.setCreatedAt(Instant.now());
        Role role = new Role();
        role.setName(RoleName.ROLE_USER);

        when(userRepository.existsByUsername("cesperic")).thenReturn(false);
        when(userRepository.existsByEmail("cespedes@pedrazas.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.addUser(u));
    }

    @Test
    void addUser_givenRole_throwsAppException_Test(){
        User u = new User();
        u.setUsername("cesperic21");
        u.setFirstName("Richard");
        u.setLastName("Céspedes Pedrazas");
        u.setEmail("cespedes@pedrazas.com");
        u.setPassword("123456");
        u.setUpdatedAt(Instant.now());
        u.setCreatedAt(Instant.now());
        Role role = new Role();
        role.setName(RoleName.ROLE_USER);

        when(userRepository.existsByUsername("cesperic")).thenReturn(false);
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenThrow(new AppException("User role not set"));
        when(passwordEncoder.encode(u.getPassword())).thenReturn(u.getPassword());
        when(userRepository.save(u)).thenReturn(u);

        assertThrows(AppException.class, () -> userService.addUser(u));
    }

    @Test
    void setOrUpdateInfo_givenInfoRequest_thenReturnUserProfile_Test (){
        Post p = new Post();
        p.setTitle("Título");
        List<Post> listaDePost = List.of(p);

        UserPrincipal richard = new UserPrincipal(1L, "Richard", "Céspedes", "rick4", "richard@cespedes.com", "123456", List.of(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));

        User u1 = new User();
        u1.setId(1L);
        u1.setUsername("rick4");
        u1.setFirstName("Richard");
        u1.setLastName("Céspedes");
        u1.setEmail("richard@cespedes.com");
        u1.setPassword("123456");
        u1.setPosts(listaDePost);
        u1.setRoles(List.of(new Role(RoleName.ROLE_ADMIN)));
        u1.setUpdatedAt(Instant.now());
        u1.setCreatedAt(Instant.now());

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(u1));

        InfoRequest infoRequest = new InfoRequest();
        infoRequest.setStreet("condes de bustillo");
        infoRequest.setSuite("presidencial");
        infoRequest.setCity("Sevilla");
        infoRequest.setZipcode("41010");
        infoRequest.setBs("bs");
        infoRequest.setCatchPhrase("catchPhrase");
        infoRequest.setCompanyName("company");
        infoRequest.setLat("-358436123");
        infoRequest.setLng("582133363");
        infoRequest.setWebsite("www.google.com");
        
        Geo geo = new Geo(infoRequest.getLat(), infoRequest.getLng());
        Address address = new Address(infoRequest.getStreet(), infoRequest.getSuite(), infoRequest.getCity(),
                infoRequest.getZipcode(), geo);
        Company company = new Company(infoRequest.getCompanyName(), infoRequest.getCatchPhrase(), infoRequest.getBs());
        u1.setAddress(address);
        u1.setCompany(company);
        u1.setWebsite(infoRequest.getWebsite());
        u1.setPhone(infoRequest.getPhone());
        u1.setUpdatedAt(Instant.now());
        u1.setCreatedAt(Instant.now());

        when(userRepository.save(u1)).thenReturn(u1);
        when(postRepository.countByCreatedBy(any())).thenReturn(1L);

        UserProfile u2 = new UserProfile(u1.getId(), u1.getUsername(),
                u1.getFirstName(), u1.getLastName(), u1.getCreatedAt(),
                u1.getEmail(), u1.getAddress(), u1.getPhone(), u1.getWebsite(),
                u1.getCompany(), 1L);

        UserProfile result = userService.setOrUpdateInfo(richard, infoRequest);
        assertEquals(u2, result);

    }

    @Test
    void setOrUpdateInfo_givenUsername_ThrowsResourceNotFoundException_Test (){

        UserPrincipal richard = new UserPrincipal(1L, "Richard", "Céspedes", "rick4", "richard@cespedes.com", "123456", List.of(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));
        InfoRequest infoRequest = new InfoRequest();

        when(userRepository.findByUsername(any())).thenThrow(new ResourceNotFoundException("User", "username", richard.getUsername()));

        assertThrows(ResourceNotFoundException.class, () -> userService.setOrUpdateInfo(richard, infoRequest));

    }

    @Test
    void setOrUpdateInfo_givenUsername_ThrowsAccessDeniedException_Test (){

        UserPrincipal richard = new UserPrincipal(1L, "Richard", "Céspedes", "rick4", "richard@cespedes.com", "123456", List.of(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())));

        User u1 = new User();
        u1.setId(2L);
        u1.setUsername("rick4");
        u1.setFirstName("Richard");
        u1.setLastName("Céspedes");
        u1.setEmail("richard@cespedes.com");
        u1.setPassword("123456");
        u1.setRoles(List.of(new Role(RoleName.ROLE_ADMIN)));
        u1.setUpdatedAt(Instant.now());
        u1.setCreatedAt(Instant.now());

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(u1));

        InfoRequest infoRequest = new InfoRequest();

        assertThrows(AccessDeniedException.class, () -> userService.setOrUpdateInfo(richard, infoRequest));

    }

    @Test
    void checkUsernameAvailability_givenUsername_returnTrue () {
        User u1 = new User();
        u1.setUsername("Richard");

        when(userRepository.existsByUsername(u1.getUsername())).thenReturn(false);
        UserIdentityAvailability u = new UserIdentityAvailability(true);
        assertEquals(u, userService.checkUsernameAvailability(u1.getUsername()));
    }

    @Test
    void deleteUser_givenUserName_returnApiResponse() {

        UserPrincipal u = new UserPrincipal(1L, "Richard","Céspedes Pedrazas", "Richard", "richard@gmail.com", "123456", List.of(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));
        User u1 = new User();
        u1.setId(1L);
        u1.setUsername("Richard");
        u1.setFirstName("Richard");
        u1.setLastName("Céspedes Pedrazas");
        u1.setEmail("richard@gmail.com");
        u1.setRoles(List.of(new Role(RoleName.ROLE_ADMIN)));
        u1.setPassword("123456");
        u1.setCreatedAt(Instant.now());
        u1.setUpdatedAt(Instant.now());

        when(userRepository.findByUsername(u1.getUsername())).thenReturn(Optional.of(u1));
        doNothing().when(userRepository).deleteById(u1.getId());
        ApiResponse ap = new ApiResponse(Boolean.TRUE, "You successfully deleted profile of: " + u1.getUsername());
        assertEquals(ap, userService.deleteUser(u1.getUsername(), u));

    }

    @Test
    void deleteUser_givenUserName_throwsResourceNotFoundException() {

        UserPrincipal u = new UserPrincipal(1L, "Richard","Céspedes Pedrazas", "Richard", "richard@gmail.com", "123456", List.of(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())));
        User u1 = new User();
        u1.setId(1L);
        u1.setUsername("Richard");
        u1.setFirstName("Richard");
        u1.setLastName("Céspedes Pedrazas");
        u1.setEmail("richard@gmail.com");
        u1.setRoles(List.of(new Role(RoleName.ROLE_USER)));
        u1.setPassword("123456");
        u1.setCreatedAt(Instant.now());
        u1.setUpdatedAt(Instant.now());

        when(userRepository.findByUsername("Pedro")).thenThrow(new ResourceNotFoundException("User", "id", "Pedro"));

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser("Pedro", u));
    }

    @Test
    void deleteUser_givenUserName_throwsAccessDeniedException() {

        UserPrincipal u = new UserPrincipal(2L, "Richard","Céspedes Pedrazas", "Richard", "richard@gmail.com", "123456", List.of(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())));
        User u1 = new User();
        u1.setId(1L);
        u1.setUsername("Richard");
        u1.setFirstName("Richard");
        u1.setLastName("Céspedes Pedrazas");
        u1.setEmail("richard@gmail.com");
        u1.setRoles(List.of(new Role(RoleName.ROLE_USER)));
        u1.setPassword("123456");
        u1.setCreatedAt(Instant.now());
        u1.setUpdatedAt(Instant.now());

        when(userRepository.findByUsername(u1.getUsername())).thenReturn(Optional.of(u1));

        assertThrows(AccessDeniedException.class, () -> userService.deleteUser(u1.getUsername(), u));
    }

    @Test
    void givenAdmin_givenUserName_ShouldShowSuccess() {
        User u1 = new User();
        u1.setId(1L);
        u1.setUsername("Richard");
        u1.setFirstName("Richard");
        u1.setLastName("Céspedes Pedrazas");
        u1.setEmail("richard@gmail.com");
        u1.setPassword("123456");
        u1.setCreatedAt(Instant.now());
        u1.setUpdatedAt(Instant.now());

        when(userRepository.getUserByName(u1.getUsername())).thenReturn(u1);
        when(roleRepository.findByName(RoleName.ROLE_ADMIN)).thenReturn(Optional.of(new Role(RoleName.ROLE_ADMIN)));
        when(roleRepository.findByName(RoleName.ROLE_USER)).thenReturn(Optional.of(new Role(RoleName.ROLE_USER)));

        u1.setRoles(List.of(new Role(RoleName.ROLE_ADMIN),new Role(RoleName.ROLE_USER)));
        when(userRepository.save(u1)).thenReturn(u1);
        ApiResponse ap = new ApiResponse(Boolean.TRUE, "You gave ADMIN role to user: " + u1.getUsername());

        assertEquals(ap, userService.giveAdmin(u1.getUsername()));
    }

    @Test
    void givenAdmin_givenUserName_throwAppException () {
        User u1 = new User();
        u1.setId(1L);
        u1.setUsername("Richard");
        u1.setFirstName("Richard");
        u1.setLastName("Céspedes Pedrazas");
        u1.setEmail("richard@gmail.com");
        u1.setPassword("123456");
        u1.setCreatedAt(Instant.now());
        u1.setUpdatedAt(Instant.now());

        when(userRepository.getUserByName(u1.getUsername())).thenReturn(u1);
        when(roleRepository.findByName(any())).thenThrow(new AppException("User role not set"));

        assertThrows(AppException.class, () -> userService.giveAdmin(u1.getUsername()));

    }
}