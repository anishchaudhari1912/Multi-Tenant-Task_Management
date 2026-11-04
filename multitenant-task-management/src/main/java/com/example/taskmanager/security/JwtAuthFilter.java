package com.example.taskmanager.security;
import com.example.taskmanager.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
 private final JwtService jwt; private final CustomUserDetailsService users; private final UserRepository userRepository;
 public JwtAuthFilter(JwtService jwt,CustomUserDetailsService users,UserRepository userRepository){this.jwt=jwt;this.users=users;this.userRepository=userRepository;}
 @Override protected void doFilterInternal(HttpServletRequest request,HttpServletResponse response,FilterChain chain)throws ServletException,IOException {
  String header=request.getHeader("Authorization");
  if(header!=null&&header.startsWith("Bearer ")&&SecurityContextHolder.getContext().getAuthentication()==null){
   try {
    String token=header.substring(7);
    String email=jwt.extractEmail(token);
    UserDetails details=users.loadUserByUsername(email);
    if(!jwt.isTokenValid(token,details.getUsername())) { unauthorized(response); return; }
    Claims claims=jwt.extractClaims(token);
    Object tenantClaim=claims.get("tenant_id");
    String role=claims.get("role",String.class);
    if(!(tenantClaim instanceof Number)) { unauthorized(response); return; }
    Long tenantId=((Number)tenantClaim).longValue();
    var dbUser=userRepository.findByEmailIgnoreCase(email).orElseThrow();
    if(!dbUser.getTenant().getId().equals(tenantId)||!dbUser.getRole().name().equals(role)){unauthorized(response);return;}
    TenantContext.setTenantId(tenantId);
    SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(details,null,details.getAuthorities()));
   } catch(Exception ex){SecurityContextHolder.clearContext();TenantContext.clear();unauthorized(response);return;}
  }
  try { chain.doFilter(request,response); }
  finally { TenantContext.clear(); SecurityContextHolder.clearContext(); }
 }
 private void unauthorized(HttpServletResponse response)throws IOException {
  response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
  response.setContentType("application/json");
  response.getWriter().write("{\"message\":\"Invalid or expired token\"}");
 }
}
