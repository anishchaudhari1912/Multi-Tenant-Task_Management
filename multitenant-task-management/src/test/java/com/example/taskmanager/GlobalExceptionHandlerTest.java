package com.example.taskmanager;
import com.example.taskmanager.exception.*;import org.junit.jupiter.api.Test;import org.springframework.http.*;import org.springframework.web.bind.MethodArgumentNotValidException;import static org.junit.jupiter.api.Assertions.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
class GlobalExceptionHandlerTest {
 private final GlobalExceptionHandler handler=new GlobalExceptionHandler();
 @Test void mapsNotFoundTo404(){var response=handler.notFound(new ResourceNotFoundException("missing"));assertEquals(HttpStatus.NOT_FOUND,response.getStatusCode());assertEquals("missing",response.getBody().get("message"));}
 @Test void mapsBadRequestTo400(){var response=handler.badRequest(new BadRequestException("invalid"));assertEquals(HttpStatus.BAD_REQUEST,response.getStatusCode());}
 @Test void mapsAccessDeniedTo403(){var response=handler.forbidden(new AccessDeniedException("no"));assertEquals(HttpStatus.FORBIDDEN,response.getStatusCode());}
 @Test void mapsBadCredentialsTo401(){var response=handler.badCredentials(new BadCredentialsException("bad"));assertEquals(HttpStatus.UNAUTHORIZED,response.getStatusCode());}
 @Test void hidesUnexpectedServerDetails(){var response=handler.other(new IllegalStateException("secret detail"));assertEquals(HttpStatus.INTERNAL_SERVER_ERROR,response.getStatusCode());assertEquals("Something went wrong",response.getBody().get("message"));}
}
