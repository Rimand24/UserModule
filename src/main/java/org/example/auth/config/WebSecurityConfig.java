package org.example.auth.config;

import org.example.auth.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/registration", "/activate/*", "/static/**").permitAll()
                .antMatchers("/h2-console/**").permitAll() //fixme H2 database config
//                .antMatchers("/admin").hasRole("ADMIN") //todo admin page
                .anyRequest().authenticated()
                .and().formLogin().loginPage("/login").permitAll()
                //.and().formLogin(form -> form.loginPage("/login").permitAll())
                .and().logout().logoutSuccessUrl("/login")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                //                .and().rememberMe() //todo https://docs.spring.io/spring-security/site/docs/5.3.2.RELEASE/reference/html5/#servlet-rememberme
                .invalidateHttpSession(true)//fixme
                .clearAuthentication(true)//fixme

        ;

        http.csrf().disable(); //fixme H2 database config
        http.headers().frameOptions().disable();   //fixme H2 database config
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }
}