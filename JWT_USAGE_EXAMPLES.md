# JWT Utility Usage Examples

## Overview
The `JwtUtil` class provides comprehensive JWT token management using the JJWT library. It's designed to be used in your authentication and authorization flows.

---

## 1. JWT Token Generation

### Example 1: Generate Token from Spring Authentication
```java
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    
    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponse> login(@RequestBody LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
                )
            );
            
            // Generate JWT token
            String token = jwtUtil.generateToken(authentication);
            
            return ResponseEntity.ok(new JwtTokenResponse(token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(new JwtTokenResponse("Invalid credentials"));
        }
    }
}
```

### Example 2: Generate Token from Username (Without Authentication)
```java
@Service
public class UserService {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public String createTokenForUser(String username) {
        // Useful for password reset, email verification tokens
        return jwtUtil.generateToken(username);
    }
}
```

---

## 2. JWT Token Validation

### Example 1: Validate Token in Request Filter
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getTokenFromRequest(request);
            
            if (token != null && jwtUtil.validateToken(token)) {
                // Token is valid, proceed with authentication
                String username = jwtUtil.getUsernameFromJwt(token);
                
                // Create authentication object and set in SecurityContext
                Authentication auth = new UsernamePasswordAuthenticationToken(
                    username, null, new ArrayList<>()
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            log.error("Could not authenticate user: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

### Example 2: Validate Token in Service Layer
```java
@Service
public class TokenValidationService {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public boolean isTokenValid(String token) {
        return jwtUtil.validateToken(token);
    }
    
    public String getUserFromToken(String token) {
        if (isTokenValid(token)) {
            return jwtUtil.getUsernameFromJwt(token);
        }
        throw new InvalidTokenException("Token is not valid");
    }
}
```

---

## 3. Token Expiration Handling

### Example 1: Check Token Expiration
```java
@Service
public class TokenExpirationService {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    public boolean shouldRefreshToken(String token) {
        try {
            Date expirationDate = jwtUtil.getExpirationDateFromToken(token);
            long timeUntilExpiry = expirationDate.getTime() - System.currentTimeMillis();
            
            // Refresh if token expires in less than 5 minutes
            return timeUntilExpiry < 5 * 60 * 1000;
        } catch (Exception e) {
            return true; // Consider as expired if we can't read it
        }
    }
    
    public boolean isTokenExpired(String token) {
        return jwtUtil.isTokenExpired(token);
    }
}
```

### Example 2: Automatic Token Refresh
```java
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class TokenRefreshController {
    
    private final JwtUtil jwtUtil;
    private final TokenExpirationService expirationService;
    
    @PostMapping("/refresh")
    public ResponseEntity<JwtTokenResponse> refreshToken(@RequestBody String token) {
        if (jwtUtil.validateToken(token)) {
            String username = jwtUtil.getUsernameFromJwt(token);
            String newToken = jwtUtil.generateToken(username);
            return ResponseEntity.ok(new JwtTokenResponse(newToken));
        }
        return ResponseEntity.status(401).body(new JwtTokenResponse("Invalid token"));
    }
}
```

---

## 4. Integration with Authorization

### Example 1: Protected Endpoint with JWT
```java
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            
            if (!jwtUtil.validateToken(token)) {
                return ResponseEntity.status(401).body("Invalid or expired token");
            }
            
            String username = jwtUtil.getUsernameFromJwt(token);
            // Fetch user profile from database
            return ResponseEntity.ok(getUserProfileFromDB(username));
            
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Authentication failed");
        }
    }
}
```

### Example 2: Role-Based Access Control with JWT
```java
@Component
public class RoleExtractor {
    
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    
    public boolean hasRequiredRole(String token, String requiredRole) {
        try {
            String username = jwtUtil.getUsernameFromJwt(token);
            UserEntity user = userRepository.findByUsername(username);
            
            // Parse roles from user entity
            return user.getRoles().contains(requiredRole);
        } catch (Exception e) {
            return false;
        }
    }
}
```

---

## 5. Security Configuration

### Example 1: Spring Security Configuration with JWT
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/auth/login", "/auth/register").permitAll()
                .requestMatchers("/api/user/**").authenticated()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(
                new JwtAuthenticationFilter(), 
                UsernamePasswordAuthenticationFilter.class
            );
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## 6. Error Handling with JWT

### Example 1: Custom Exception Handling for JWT
```java
@RestControllerAdvice
public class JwtExceptionHandler {
    
    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJwt(MalformedJwtException e) {
        ErrorResponse error = new ErrorResponse("Invalid token format", HttpStatus.UNAUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwt(ExpiredJwtException e) {
        ErrorResponse error = new ErrorResponse("Token has expired", HttpStatus.UNAUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedJwt(UnsupportedJwtException e) {
        ErrorResponse error = new ErrorResponse("Unsupported JWT token", HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    
    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ErrorResponse> handleSignatureException(SignatureException e) {
        ErrorResponse error = new ErrorResponse("Invalid JWT signature", HttpStatus.UNAUTHORIZED);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
```

---

## 7. Configuration Properties

### Application.yaml Configuration
```yaml
# JWT Configuration
jwt:
  secret: "your-secret-key-which-should-be-at-least-256-bits-long-for-HS256-algorithm"
  expiration: 86400000  # 24 hours in milliseconds

# Spring Security Configuration
spring:
  security:
    user:
      name: admin
      password: admin123

# Datasource Configuration
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/microservice_employee
    username: root
    password: 1234
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
```

---

## 8. Token Claims (Advanced)

### Example: Extended JWT Usage with Custom Claims
```java
@Component
public class JwtUtilExtended {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Generate token with custom claims
     * Note: The base JwtUtil can be extended for this functionality
     */
    public String generateTokenWithClaims(String username, Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date())
                .signWith(getSigningKey())
                .compact();
    }
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = "your-secret-key".getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

---

## 9. Complete Login/Logout Flow

```java
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthFlowController {
    
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenBlacklistService tokenBlacklist;
    
    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<JwtTokenResponse> login(@RequestBody LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        String token = jwtUtil.generateToken(auth);
        return ResponseEntity.ok(new JwtTokenResponse(token));
    }
    
    // LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        
        if (jwtUtil.validateToken(token)) {
            Date expiration = jwtUtil.getExpirationDateFromToken(token);
            tokenBlacklist.addToBlacklist(token, expiration);
            return ResponseEntity.ok("Logged out successfully");
        }
        
        return ResponseEntity.status(401).body("Invalid token");
    }
    
    // VERIFY TOKEN
    @GetMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        
        if (jwtUtil.validateToken(token) && !tokenBlacklist.isBlacklisted(token)) {
            String username = jwtUtil.getUsernameFromJwt(token);
            return ResponseEntity.ok(Map.of("valid", true, "username", username));
        }
        
        return ResponseEntity.status(401).body(Map.of("valid", false));
    }
}
```

---

## 10. Testing JWT Utilities

```java
@SpringBootTest
class JwtUtilTests {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Test
    void testTokenGeneration() {
        String token = jwtUtil.generateToken("testuser");
        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
    }
    
    @Test
    void testTokenValidation() {
        String token = jwtUtil.generateToken("testuser");
        assertTrue(jwtUtil.validateToken(token));
    }
    
    @Test
    void testUsernameExtraction() {
        String username = "testuser";
        String token = jwtUtil.generateToken(username);
        assertEquals(username, jwtUtil.getUsernameFromJwt(token));
    }
    
    @Test
    void testExpiredToken() {
        String token = jwtUtil.generateToken("testuser");
        assertFalse(jwtUtil.isTokenExpired(token));
    }
    
    @Test
    void testInvalidToken() {
        String invalidToken = "invalid.token.here";
        assertFalse(jwtUtil.validateToken(invalidToken));
    }
}
```

---

## Summary of JWT Methods

| Method | Parameter | Return | Purpose |
|--------|-----------|--------|---------|
| `generateToken(Authentication)` | Spring Authentication | String (JWT) | Generate token from authenticated user |
| `generateToken(String)` | Username | String (JWT) | Generate token from username only |
| `validateToken(String)` | JWT Token | boolean | Validate token signature and expiration |
| `getUsernameFromJwt(String)` | JWT Token | String | Extract username from token |
| `isTokenExpired(String)` | JWT Token | boolean | Check if token is expired |
| `getExpirationDateFromToken(String)` | JWT Token | Date | Get token expiration timestamp |

---

## Best Practices

1. **Secret Key Management**
   - Store JWT secret in environment variables, not in code
   - Use at least 256 bits for HS256 algorithm
   - Rotate secrets periodically

2. **Token Lifetime**
   - Keep token expiration short (15 minutes to 1 hour)
   - Implement refresh token mechanism for long sessions
   - Store refresh tokens securely (httpOnly cookies)

3. **Security**
   - Always use HTTPS in production
   - Validate tokens on every protected request
   - Implement token blacklist for logout
   - Never store sensitive information in JWT claims

4. **Error Handling**
   - Log all JWT validation failures
   - Return generic error messages to clients
   - Monitor for brute force attacks

5. **Performance**
   - Cache token validation results when possible
   - Use async processing for token validation in high-traffic scenarios

