# Fixes Applied to Employee Microservices App

## Summary of Issues Fixed

### 1. ✅ JWT Utility Methods Generated
**File**: `Auth/src/main/java/com/sadcodes/auth/util/JwtUtil.java`

**Changes Made**:
- Implemented complete JwtUtil class with JWT token generation, validation, and parsing
- Added the following utility methods:
  - `generateToken(Authentication)` - Generate JWT token from Spring Authentication
  - `generateToken(String username)` - Generate JWT token from username
  - `getUsernameFromJwt(String token)` - Extract username from JWT token
  - `validateToken(String token)` - Validate JWT token signature and expiration
  - `isTokenExpired(String token)` - Check if token has expired
  - `getExpirationDateFromToken(String token)` - Get token expiration date
  - `getSigningKey()` - Private method to get HMAC-SHA signing key
- Uses JJWT library (v0.13.0) for secure JWT handling
- Includes comprehensive error logging for all exception types

**Configuration**:
- JWT Secret: Configurable via `jwt.secret` property (min 256 bits recommended)
- JWT Expiration: Configurable via `jwt.expiration` property (default: 86400000ms = 24 hours)

---

### 2. ✅ GlobalExceptionHandler Now Works Properly
**File**: `Address/src/main/java/com/sadcodes/address/exception/GlobalExceptionHandler.java`

**Changes Made**:
- Added `@Slf4j` annotation for logging
- Added logging to all exception handlers
- Added handler for `FeignException` (service-to-service communication errors)
- Improved error message formatting for better debugging

**Handlers Implemented**:
1. `ResourceNotFoundException` - Returns 404 with custom message
2. `BadRequestException` - Returns 400 with custom message
3. `MissingParameterException` - Returns appropriate HTTP status
4. `CustomException` - Returns appropriate HTTP status
5. `FeignException` - Returns 503 SERVICE_UNAVAILABLE with error message
6. Generic `Exception` - Returns 500 INTERNAL_SERVER_ERROR

---

### 3. ✅ ErrorResponse Class Fixed
**File**: `Address/src/main/java/com/sadcodes/address/exception/ErrorResponse.java`

**Changes Made**:
- Changed `httpStatus` field from `HttpStatus` object to `int status` (HTTP status code)
- Added `@JsonFormat` annotation for proper timestamp formatting
- Changed `localDateTime` to `timestamp` for consistency with Postman JSON response
- Now returns proper JSON format:
  ```json
  {
    "message": "Error description",
    "status": 400,
    "timestamp": "2026-04-04T10:30:45.123Z"
  }
  ```

**Why This Fixes The Issue**:
- `HttpStatus` enum doesn't serialize properly to JSON
- Integer status code (e.g., 400, 500) is the standard for REST API responses
- Timestamp is now properly formatted as ISO 8601 string
- This prevents Spring's default error handling from taking over

---

## How The GlobalExceptionHandler Works

When an exception occurs in the Address microservice:

1. The exception is caught by the `@RestControllerAdvice` GlobalExceptionHandler
2. The appropriate `@ExceptionHandler` method is invoked based on exception type
3. An `ErrorResponse` object is created with:
   - The exception message
   - The HTTP status code (as integer)
   - Current timestamp
4. The response is returned to the client with the proper HTTP status

Example flow:
```
Request → Controller throws exception → GlobalExceptionHandler catches it
→ Creates ErrorResponse with message, status, and timestamp
→ Returns ResponseEntity with proper HTTP status
→ Client receives JSON response instead of default Spring error
```

---

## How The JWT Utility Works

The JWT utility provides secure token management:

1. **Token Generation**: 
   - Creates JWT token signed with HMAC-SHA key
   - Includes issued-at time and expiration time
   - Subject is the username

2. **Token Validation**:
   - Verifies signature integrity
   - Checks token expiration
   - Returns boolean result

3. **Token Parsing**:
   - Extracts username/subject from token
   - Gets expiration date
   - Safely handles all JWT exceptions

---

## Database Configuration (Address Service)

The Address service is configured to:
- Connect to: `jdbc:mysql://localhost:3306/microservice_employee`
- Username: `root`
- Password: `1234`
- Hibernate DDL-auto: `update` (automatically creates/updates tables)
- Show SQL: `true` (logs SQL queries)

**Table**: `address`
- `id` (PK, Auto-increment)
- `emp_id` (Foreign key to employee)
- `street` (String)
- `pin_code` (Long)
- `city` (String)
- `country` (String)
- `address_type` (ENUM)

---

## Testing the Fixes

### Test Global Exception Handler
```bash
# Test with invalid address ID (should return 404 with custom error response)
curl -X GET http://localhost:8082/addresses/999

# Expected Response:
{
  "message": "Address not found with id: 999",
  "status": 404,
  "timestamp": "2026-04-04T10:35:12.456Z"
}
```

### Test Address Save
```bash
# Save new address
curl -X POST http://localhost:8082/addresses/save \
  -H "Content-Type: application/json" \
  -d '{
    "empId": 1,
    "addressRequestDtoList": [
      {
        "street": "123 Main St",
        "pinCode": 12345,
        "city": "New York",
        "country": "USA",
        "addressType": "RESIDENTIAL"
      }
    ]
  }'
```

### Test JWT Token Generation
```java
// In your authentication controller
@Autowired
private JwtUtil jwtUtil;

@PostMapping("/login")
public ResponseEntity<JwtTokenResponse> login(@RequestBody LoginRequest request) {
    Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
    );
    String token = jwtUtil.generateToken(auth);
    return ResponseEntity.ok(new JwtTokenResponse(token));
}
```

---

## Dependencies Already Installed

### For JWT (Auth Service)
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.13.0</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.13.0</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.13.0</version>
    <scope>runtime</scope>
</dependency>
```

### For Address Service
```xml
<dependency>
    <groupId>org.modelmapper</groupId>
    <artifactId>modelmapper</artifactId>
    <version>3.2.4</version>
</dependency>
```

---

## Next Steps

1. **Test the Address save endpoint** with valid employee ID
2. **Test the JWT token generation** with login endpoint
3. **Verify database connectivity** and table creation
4. **Check logs** for SQL queries and exception messages
5. **Monitor error responses** to ensure they match the new format

---

## Files Modified

1. ✅ `Auth/src/main/java/com/sadcodes/auth/util/JwtUtil.java` - JWT utility implementation
2. ✅ `Address/src/main/java/com/sadcodes/address/exception/GlobalExceptionHandler.java` - Exception handling
3. ✅ `Address/src/main/java/com/sadcodes/address/exception/ErrorResponse.java` - Error response serialization

