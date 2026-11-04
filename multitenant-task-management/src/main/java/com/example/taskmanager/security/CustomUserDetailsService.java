package com.example.taskmanager.security;
import com.example.taskmanager.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
@Service
public class CustomUserDetailsService implements UserDetailsService {
 private final UserRepository users;
 public CustomUserDetailsService(UserRepository users)
 {
  this.users=users;
 }
 @Override public UserDetails loadUserByUsername(String email)
 {
  var user=users.findByEmailIgnoreCase(email)
          .orElseThrow(()->new UsernameNotFoundException("User not found"));
  return org.springframework.security.core.userdetails.User.withUsername(user.getEmail()).password(user.getPassword()).authorities("ROLE_"+user.getRole().name()).build();}
}
