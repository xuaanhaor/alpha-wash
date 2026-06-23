package com.alphawash.endpoint;

import static com.alphawash.constant.Constant.API_AUTH;
import static com.alphawash.constant.Constant.LOGIN_ENDPOINT;

import com.alphawash.request.LoginRequest;
import com.alphawash.response.LoginResponse;
import com.alphawash.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(API_AUTH)
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication endpoints")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Login with username and password")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "Login successful"),
                @ApiResponse(responseCode = "401", description = "Invalid credentials")
            })
    @PostMapping(LOGIN_ENDPOINT)
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("");

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .username(userDetails.getUsername())
                .role(role.replace("ROLE_", ""))
                .expiresIn(jwtUtil.getExpirationMs())
                .build();

        return ResponseEntity.ok(response);
    }
}
