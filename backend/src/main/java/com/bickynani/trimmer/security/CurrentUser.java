package com.bickynani.trimmer.security;
import com.bickynani.trimmer.model.User;
import com.bickynani.trimmer.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import jakarta.servlet.http.HttpServletRequest;

@Component
@RequestScope
public class CurrentUser {
    private final HttpServletRequest request;
    private final JwtService jwtService;
    private final UserRepository users;
    public CurrentUser(HttpServletRequest request, JwtService jwtService, UserRepository users) {
        this.request=request; this.jwtService=jwtService; this.users=users;
    }
    public User require() {
        String header=request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) throw new ApiException(401, "Please log in first.");
        try {
            Long id=jwtService.getUserId(header.substring(7));
            return users.findById(id).orElseThrow(() -> new ApiException(401, "Account not found."));
        } catch (ApiException ex) { throw ex; }
        catch (Exception ex) { throw new ApiException(401, "Session expired or token is invalid. Please log in again."); }
    }
}
