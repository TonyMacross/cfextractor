# GitHub Copilot Prompt: ColdFusion Backend to Java Spring Boot Migration

## Complete Migration Prompt for GitHub Copilot

```
Perform a complete backend migration from Adobe ColdFusion to Java 17 with Spring Boot 3.x. Use the analysis from the previous migration-analysis folder as reference.

**Migration Specifications:**
- Source: Adobe ColdFusion (CFML/CFCs)
- Target: Java 17 + Spring Boot 3.2+ + Maven
- Database: SQL Server with Spring Data JPA/Hibernate
- Architecture: RESTful microservices with layered architecture
- Security: Spring Security 6 with JWT authentication

**Project Structure to Create:**
```
/backend-java/
├── src/main/java/com/company/app/
│   ├── config/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   ├── security/
│   ├── util/
│   └── Application.java
├── src/main/resources/
│   ├── application.yml
│   ├── db/migration/
│   └── static/
├── src/test/java/
└── pom.xml
```

**Migration Tasks:**

1. **Project Setup & Dependencies**
   - Generate Spring Boot 3.2+ project structure with Maven
   - Configure dependencies for: Web, JPA, Security, Validation, SQL Server
   - Set up application.yml with database configuration
   - Create main Application class with proper annotations

2. **ColdFusion Components (CFCs) → Java Services**
   - Convert each .cfc file to corresponding Java service class
   - Map cffunction to @Service annotated methods
   - Transform cfproperty to Java class fields with proper annotations
   - Convert access="remote" methods to @RestController endpoints
   - Handle ColdFusion returntype to Java generics and return types
   - Map cferror and try/catch to proper Java exception handling

3. **Database Layer Migration**
   - Convert cfquery tags to @Repository interfaces extending JpaRepository
   - Transform SQL queries to JPA @Query annotations or method names
   - Create @Entity classes from database tables used in cfquery
   - Map ColdFusion query results to Java DTOs/Entities
   - Convert stored procedure calls to @Procedure or native queries
   - Handle database transactions with @Transactional

4. **Data Transfer Objects (DTOs)**
   - Create record classes (Java 17) for request/response objects
   - Map ColdFusion structs to Java DTOs with validation annotations
   - Use @JsonProperty for JSON serialization mapping
   - Implement proper validation with @Valid, @NotNull, @Size annotations

5. **Authentication & Security**
   - Convert ColdFusion session management to JWT tokens
   - Map cflogin/cfloginuser to Spring Security authentication
   - Transform ColdFusion roles to Spring Security authorities
   - Create SecurityConfig class with proper endpoint security
   - Implement UserDetailsService for user authentication

6. **Configuration Management**
   - Convert Application.cfc settings to application.yml properties
   - Map ColdFusion datasources to Spring DataSource configuration
   - Transform custom settings to @ConfigurationProperties classes
   - Handle environment-specific configurations

7. **Utility Classes & Custom Functions**
   - Convert ColdFusion UDFs to static utility methods
   - Map custom tags to Spring components or utility classes
   - Transform ColdFusion date/string functions to Java equivalents
   - Handle file operations and email functionality

**Code Conversion Examples:**

ColdFusion Component → Java Service:
```coldfusion
// UserService.cfc
component {
    remote function getUser(required numeric userId) returntype="struct" {
        var user = queryExecute("SELECT * FROM users WHERE id = ?", [userId]);
        return {"id": user.id, "name": user.name};
    }
}
```

Should become:
```java
@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        return new UserDto(user.getId(), user.getName());
    }
}
```

**Migration Guidelines:**
- Use Java 17 features: records, sealed classes, pattern matching where appropriate
- Implement proper exception handling with custom exceptions
- Add comprehensive logging with SLF4J
- Use Spring Boot auto-configuration wherever possible
- Follow REST API conventions for endpoint naming
- Implement proper validation and error responses
- Add unit tests for each service method
- Use Spring profiles for environment-specific configuration

**For each ColdFusion file found:**
1. Identify the component type (service, utility, etc.)
2. Create corresponding Java class with appropriate annotations
3. Convert all methods with proper parameter mapping
4. Handle database operations with Spring Data JPA
5. Add proper exception handling and logging
6. Create corresponding unit tests

Start migration by analyzing [SPECIFIC_CFC_FILE] and convert it to complete Java Spring Boot implementation.
```

## Step-by-Step Migration Process

### Phase 1: Project Initialization
1. **Create Spring Boot Project**
   ```bash
   spring init --dependencies=web,data-jpa,security,validation,sqlserver \
              --java-version=17 \
              --packaging=jar \
              --group-id=com.company \
              --artifact-id=backend-app \
              backend-java
   ```

2. **Setup Database Configuration**
   - Configure application.yml with SQL Server connection
   - Add Flyway for database migrations
   - Set up connection pooling (HikariCP)

### Phase 2: Core Infrastructure
3. **Security Configuration**
   - Create JWT token service
   - Implement UserDetailsService
   - Configure Spring Security filter chain
   - Set up CORS configuration

4. **Exception Handling Setup**
   - Create global exception handler (@ControllerAdvice)
   - Define custom exception classes
   - Implement proper error response DTOs

### Phase 3: Data Layer Migration
5. **Entity Creation**
   - Convert database tables to JPA entities
   - Define relationships (@OneToMany, @ManyToOne, etc.)
   - Add validation annotations

6. **Repository Layer**
   - Create repository interfaces extending JpaRepository
   - Add custom query methods
   - Implement complex queries with @Query

### Phase 4: Business Logic Migration
7. **Service Layer Conversion**
   - Convert each CFC to @Service class
   - Map business logic methods
   - Implement transaction management
   - Add proper logging and validation

8. **Controller Layer Creation**
   - Create REST controllers for remote CFC methods
   - Define proper HTTP methods and paths
   - Add request/response validation
   - Implement proper status codes

### Phase 5: Integration and Testing
9. **Unit Testing**
   - Create test classes for each service
   - Mock dependencies with @MockBean
   - Test edge cases and error scenarios

10. **Integration Testing**
    - Create integration tests for controllers
    - Test database operations
    - Validate security implementation

## Specific Conversion Commands

### For ColdFusion Components
```
"Convert this ColdFusion component [filename.cfc] to a complete Spring Boot service class with proper annotations, exception handling, and unit tests"
```

### For Database Operations
```
"Transform these ColdFusion queries to Spring Data JPA repository methods with proper entity mapping and transaction management"
```

### For Authentication Logic
```
"Convert this ColdFusion authentication/session management to Spring Security with JWT implementation"
```

### For Utility Functions
```
"Migrate these ColdFusion utility functions to Java utility classes with proper error handling and logging"
```

## Maven Dependencies Template

```xml
<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>com.microsoft.sqlserver</groupId>
        <artifactId>mssql-jdbc</artifactId>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Migration Quality Checklist

- [ ] All CFC components converted to Spring services
- [ ] Database operations using JPA/Hibernate
- [ ] Proper exception handling implemented
- [ ] Security configuration completed
- [ ] Unit tests with >80% coverage
- [ ] Integration tests for critical paths
- [ ] API documentation (OpenAPI/Swagger)
- [ ] Performance optimization applied
- [ ] Logging and monitoring configured
- [ ] Environment-specific configurationsMigrat