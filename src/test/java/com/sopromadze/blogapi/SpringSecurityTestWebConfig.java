package com.sopromadze.blogapi;

import com.sopromadze.blogapi.model.role.Role;
import com.sopromadze.blogapi.model.role.RoleName;
import com.sopromadze.blogapi.model.user.User;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.List;

@TestConfiguration
public class SpringSecurityTestWebConfig {

    @Bean("customUserDetailsServiceImpl")
    @Primary
    public UserDetailsService userDetailsService() {

        Role admin = new Role(1L, RoleName.ROLE_ADMIN);
        Role user = new Role(2L, RoleName.ROLE_USER);

        User user1 = User.builder()
                .username("admin")
                .password("admin")
                .roles(List.of(admin))
                .build();

        User user2 = User.builder()
                .username("user")
                .password("user")
                .roles(List.of(user))
                .build();

        return new InMemoryUserDetailsManager(List.of(user1, user2));
    }

}
