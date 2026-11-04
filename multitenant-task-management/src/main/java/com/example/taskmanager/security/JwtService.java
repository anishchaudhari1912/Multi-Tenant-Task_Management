package com.example.taskmanager.security;
import com.example.taskmanager.entity.AppUser;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
@Service
public class JwtService {
 private final SecretKey key;
 private final long expirationMs;
 public JwtService(@Value("${app.jwt.secret}") String secret,@Value("${app.jwt.expiration-ms}") long expirationMs)
 {
  this.key=Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  this.expirationMs=expirationMs;
 }
 public String generateToken(AppUser user)
 {
  Date now=new Date();
  return Jwts.builder().subject(user.getEmail()).claim("tenant_id",user.getTenant().getId()).claim("role",user.getRole().name()).issuedAt(now).expiration(new Date(now.getTime()+expirationMs)).signWith(key).compact();
 }
 public Claims extractClaims(String token)
 {
  return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
 }
 public String extractEmail(String token)
 {
  return extractClaims(token).getSubject();
 }
 public boolean isTokenValid(String token,String email)
 {
  try
  {
   return email.equals(extractEmail(token))&&extractClaims(token).getExpiration().after(new Date());
  }
  catch(JwtException|IllegalArgumentException ex)
  {
   return false;
  }
 }
}
