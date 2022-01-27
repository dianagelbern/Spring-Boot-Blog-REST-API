package com.sopromadze.blogapi;

import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import com.sopromadze.blogapi.security.UserPrincipal;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.List;

@TestConfiguration
public class SpringSecurityTestWebConfig {

    @Bean("CustomUserDetailsServiceImpl")
    @Primary
    public UserDetailsService userDetailsService() {

        Role admin = new Role(1L, RoleName.ROLE_ADMIN);
        Role user = new Role(2L, RoleName.ROLE_USER);

        UserPrincipal user1 = new UserPrincipal(1L, "Nombre1", "Apellido1", "admin", "admin@admin.com", "admin", List.of(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString())));
        UserPrincipal user2 = new UserPrincipal(2L, "Nombre2", "Apellido2", "user", "user@user.com", "user", List.of(new SimpleGrantedAuthority(RoleName.ROLE_USER.toString())));

        return new InMemoryUserDetailsManager(List.of(user1, user2));
    }

}
