package com.example.taskmanager.exception;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.dao.DataIntegrityViolationException;
import java.time.LocalDateTime;
import java.util.*;
@RestControllerAdvice
public class GlobalExceptionHandler {
 @ExceptionHandler(MethodArgumentNotValidException.class)
 public ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException ex)
 {
  Map<String,String> errors=new LinkedHashMap<>();
  ex.getBindingResult().getFieldErrors().forEach(e->errors.put(e.getField(),e.getDefaultMessage()));
  return response(HttpStatus.BAD_REQUEST,"Validation failed",errors);
 }
 @ExceptionHandler(HttpMessageNotReadableException.class)
 public ResponseEntity<Map<String,Object>> unreadable(Exception ex)
 {
  return response(HttpStatus.BAD_REQUEST,"Request body is invalid or contains an unsupported value",null);
 }
 @ExceptionHandler(ResourceNotFoundException.class)
 public ResponseEntity<Map<String,Object>> notFound(RuntimeException ex)
 {
  return response(HttpStatus.NOT_FOUND,ex.getMessage(),null);
 }
 @ExceptionHandler({BadRequestException.class,DataIntegrityViolationException.class})
 public ResponseEntity<Map<String,Object>> badRequest(Exception ex)
 {
  return response(HttpStatus.BAD_REQUEST,ex instanceof DataIntegrityViolationException?"A record with the supplied unique value already exists":ex.getMessage(),null);
 }
 @ExceptionHandler(BadCredentialsException.class)
 public ResponseEntity<Map<String,Object>> badCredentials(Exception ex)
 {
  return response(HttpStatus.UNAUTHORIZED,"Invalid email or password",null);
 }
 @ExceptionHandler(AccessDeniedException.class)
 public ResponseEntity<Map<String,Object>> forbidden(Exception ex)
 {
  return response(HttpStatus.FORBIDDEN,"You do not have permission to perform this action",null);
 }
 @ExceptionHandler(Exception.class)
 public ResponseEntity<Map<String,Object>> other(Exception ex)
 {
  return response(HttpStatus.INTERNAL_SERVER_ERROR,"Something went wrong",null);
 }
 private ResponseEntity<Map<String,Object>> response(HttpStatus status,String message,Object details)
 {
  Map<String,Object> body=new LinkedHashMap<>();
  body.put("timestamp",LocalDateTime.now());
  body.put("status",status.value());
  body.put("error",status.getReasonPhrase());
  body.put("message",message);
  if(details!=null)body.put("details",details);
  return ResponseEntity.status(status).body(body);
 }
}
