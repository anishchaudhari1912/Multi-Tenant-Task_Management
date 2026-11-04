package com.example.taskmanager.config;
import com.example.taskmanager.security.JwtAuthFilter;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
@Configuration @EnableMethodSecurity
public class SecurityConfig {
 @Bean PasswordEncoder passwordEncoder()
 {
  return new BCryptPasswordEncoder();
 }
 @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration config)throws Exception
 {
  return config.getAuthenticationManager();
 }
 @Bean SecurityFilterChain filterChain(HttpSecurity http,JwtAuthFilter jwtFilter)throws Exception
 {
  return http.csrf(csrf->csrf.disable()).headers(h->h.frameOptions(f->f.sameOrigin()))
          .authorizeHttpRequests(auth->auth.requestMatchers("/tenant/register","/auth/login","/health","/h2-console/**")
                  .permitAll().anyRequest().authenticated()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .addFilterBefore(jwtFilter,UsernamePasswordAuthenticationFilter.class).build();}
}
